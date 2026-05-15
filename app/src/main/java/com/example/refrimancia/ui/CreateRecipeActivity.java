package com.example.refrimancia.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.response.RecetaCreada;
import com.example.refrimancia.model.response.RespuestaUnica;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRecipeActivity extends AppCompatActivity {

    private LinearLayout ingredientsList;
    private EditText etNewIngredient, etRecipeName, etRecipeDescription;
    private Button btnAddIngredient, btnPublish, btnCancel;
    private ImageView recipeImagePlaceholder;
    private LinearLayout timeContainer;
    private TextView tvPrepTimeValue;
    private Spinner spinnerCategory;

    private List<String> listaIngredientes = new ArrayList<>();
    private File croppedImageFile = null;
    private int totalMinutes = 0;
    private static final String PERMANENT_IMAGE_NAME = "recipe_upload.jpg";

    // Variables para el modo edición
    private boolean isEditing = false;
    private Receta recetaAEditar;
    private RecetaService recetaService;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri sourceUri = result.getData().getData();
                    if (sourceUri != null) {
                        iniciarRecorte(sourceUri);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cropResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        try {
                            File sourceFile = new File(resultUri.getPath());
                            File destFile = new File(getFilesDir(), PERMANENT_IMAGE_NAME);
                            copyFile(sourceFile, destFile);

                            croppedImageFile = destFile;
                            recipeImagePlaceholder.setImageURI(null);
                            recipeImagePlaceholder.setImageURI(Uri.fromFile(croppedImageFile));
                        } catch (IOException e) {
                            Log.e("CreateRecipe", "Error al procesar imagen", e);
                            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    Throwable cropError = UCrop.getError(result.getData());
                    Log.e("CreateRecipe", "Error al recortar la imagen", cropError);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        // Inicializamos el servicio de API
        recetaService = ClienteRetrofit.obtenerInstancia(this).create(RecetaService.class);

        ingredientsList = findViewById(R.id.ingredientsList);
        etNewIngredient = findViewById(R.id.etNewIngredient);
        etRecipeName = findViewById(R.id.etRecipeName);
        etRecipeDescription = findViewById(R.id.etRecipeDescription);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnPublish = findViewById(R.id.btnPublish);
        btnCancel = findViewById(R.id.btnCancel);
        recipeImagePlaceholder = findViewById(R.id.recipeImagePlaceholder);
        timeContainer = findViewById(R.id.timeContainer);
        tvPrepTimeValue = findViewById(R.id.tvPrepTimeValue);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        String[] categorias = {"Desayuno", "Almuerzo", "Comida", "Cena", "Postre", "Snack"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_theme, categorias);
        adapter.setDropDownViewResource(R.layout.spinner_theme_dropdown);
        spinnerCategory.setAdapter(adapter);

        recipeImagePlaceholder.setOnClickListener(v -> {
            //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //imagePickerLauncher.launch(intent);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        timeContainer.setOnClickListener(v -> mostrarSelectorTiempo());

        btnAddIngredient.setOnClickListener(v -> {
            String texto = etNewIngredient.getText().toString().trim().replaceAll("\\s+", " ");
            if (!texto.isEmpty()) {
                listaIngredientes.add(texto);
                agregarIngredienteAVista(texto);
                etNewIngredient.setText("");
            } else {
                Toast.makeText(this, "Escribe un ingrediente", Toast.LENGTH_SHORT).show();
            }
        });

        btnPublish.setOnClickListener(v -> guardarReceta());
        btnCancel.setOnClickListener(v -> finish());

        // Manejo de Edición: Si viene una receta, pedimos el detalle completo
        recetaAEditar = (Receta) getIntent().getSerializableExtra("receta_editar");
        if (recetaAEditar != null) {
            isEditing = true;
            btnPublish.setText("Cargando...");
            btnPublish.setEnabled(false);
            obtenerDetalleReceta(recetaAEditar.getIdReceta());
        } else {
            // Modo creación: limpiar rastro de imagen previa
            File savedImage = new File(getFilesDir(), PERMANENT_IMAGE_NAME);
            if (savedImage.exists()) {
                savedImage.delete();
            }
            croppedImageFile = null;
        }
    }

    private void obtenerDetalleReceta(int id) {
        recetaService.obtenerReceta(id).enqueue(new Callback<RespuestaUnica<Receta>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Receta>> call, Response<RespuestaUnica<Receta>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    recetaAEditar = response.body().getData();
                    cargarDatosParaEdicion();
                } else {
                    Toast.makeText(CreateRecipeActivity.this, "Error al cargar receta completa", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RespuestaUnica<Receta>> call, Throwable t) {
                Toast.makeText(CreateRecipeActivity.this, "Error de red al cargar receta", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cargarDatosParaEdicion() {
        etRecipeName.setText(recetaAEditar.getTitulo());
        etRecipeDescription.setText(recetaAEditar.getDescripcion());
        totalMinutes = recetaAEditar.getTiempoPreparacion();
        tvPrepTimeValue.setText(formatTiempo(totalMinutes));
        btnPublish.setText("Actualizar");
        btnPublish.setEnabled(true);

        // Categoría
        String cat = recetaAEditar.getCategoria();
        if (cat != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
            int pos = adapter.getPosition(cat);
            if (pos >= 0) spinnerCategory.setSelection(pos);
        }

        // Ingredientes (Separamos por coma y espacio opcional)
        String ingStr = recetaAEditar.getIngredientes();
        if (ingStr != null && !ingStr.isEmpty()) {
            String[] split = ingStr.split(",\\s*");
            for (String s : split) {
                String limpio = s.trim();
                if (!limpio.isEmpty() && !listaIngredientes.contains(limpio)) {
                    listaIngredientes.add(limpio);
                    agregarIngredienteAVista(limpio);
                }
            }
        }

        // Imagen con Glide
        if (recetaAEditar.getImagenUrl() != null && !recetaAEditar.getImagenUrl().isEmpty()) {
            Glide.with(this)
                    .load(recetaAEditar.getImagenUrl())
                    .placeholder(R.drawable.bg_input)
                    .into(recipeImagePlaceholder);
        }
    }

    private String formatTiempo(int min) {
        int h = min / 60;
        int m = min % 60;
        return h + "h " + m + " min";
    }

    private void guardarReceta() {
        String titulo = etRecipeName.getText().toString().trim();
        String desc = etRecipeDescription.getText().toString().trim();
        String tipo = spinnerCategory.getSelectedItem().toString();

        if (titulo.isEmpty() || desc.isEmpty() || listaIngredientes.isEmpty() || totalMinutes == 0) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        if (titulo.length() < 5) {
            Toast.makeText(this, "El título debe tener minimo 5 caracteres", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listaIngredientes.size(); i++) {
            sb.append(listaIngredientes.get(i));
            if (i < listaIngredientes.size() - 1) sb.append(", ");
        }

        RequestBody reqTitulo = RequestBody.create(MediaType.parse("text/plain"), titulo);
        RequestBody reqIngr = RequestBody.create(MediaType.parse("text/plain"), sb.toString());
        RequestBody reqDesc = RequestBody.create(MediaType.parse("text/plain"), desc);
        RequestBody reqTipo = RequestBody.create(MediaType.parse("text/plain"), tipo);
        RequestBody reqTiempo = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(totalMinutes));

        MultipartBody.Part bodyImagen = null;
        if (croppedImageFile != null && croppedImageFile.exists()) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), croppedImageFile);
            bodyImagen = MultipartBody.Part.createFormData("imagen_receta", croppedImageFile.getName(), reqFile);
        }

        btnPublish.setEnabled(false);
        btnPublish.setText(isEditing ? "Actualizando..." : "Publicando...");

        if (isEditing) {
            // CORREGIDO: Se asegura de pasar reqDesc y luego reqIngr para coincidir con RecetaService
            recetaService.modificarReceta(recetaAEditar.getIdReceta(), reqTitulo, reqDesc, reqIngr, reqTipo, reqTiempo, bodyImagen)
                .enqueue(new Callback<RespuestaUnica<Receta>>() {
                    @Override
                    public void onResponse(Call<RespuestaUnica<Receta>> call, Response<RespuestaUnica<Receta>> response) {
                        manejarRespuesta(response.isSuccessful(), response.code());
                    }
                    @Override
                    public void onFailure(Call<RespuestaUnica<Receta>> call, Throwable t) {
                        manejarFallo(t);
                    }
                });
        } else {
            // CORREGIDO: reqDesc antes que reqIngr para coincidir con el orden esperado en el servidor
            recetaService.crearRecetaConRespuesta(reqTitulo, reqDesc, reqIngr, reqTipo, reqTiempo, bodyImagen)
                .enqueue(new Callback<RecetaCreada>() {
                    @Override
                    public void onResponse(Call<RecetaCreada> call, Response<RecetaCreada> response) {
                        manejarRespuesta(response.isSuccessful(), response.code());
                    }
                    @Override
                    public void onFailure(Call<RecetaCreada> call, Throwable t) {
                        manejarFallo(t);
                    }
                });
        }
    }

    private void manejarRespuesta(boolean success, int code) {
        btnPublish.setEnabled(true);
        btnPublish.setText(isEditing ? "Actualizar" : "Publicar");
        if (success) {
            Toast.makeText(this, isEditing ? "¡Receta actualizada!" : "¡Receta publicada!", Toast.LENGTH_LONG).show();
            if (croppedImageFile != null && croppedImageFile.exists()) croppedImageFile.delete();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error del servidor: " + code, Toast.LENGTH_SHORT).show();
        }
    }

    private void manejarFallo(Throwable t) {
        btnPublish.setEnabled(true);
        btnPublish.setText(isEditing ? "Actualizar" : "Publicar");
        Toast.makeText(this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) destFile.getParentFile().mkdirs();
        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    private void agregarIngredienteAVista(String texto) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(0, 4, 0, 4);

        TextView tv = new TextView(this);
        tv.setText("• " + texto);
        tv.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        tv.setTextSize(18);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.alexandria);
        tv.setTypeface(typeface);

        itemLayout.addView(tv, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView btnDelete = new TextView(this);
        btnDelete.setText("✕");
        btnDelete.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        btnDelete.setTextSize(18);
        btnDelete.setPadding(16, 0, 16, 0);
        btnDelete.setOnClickListener(v -> {
            ingredientsList.removeView(itemLayout);
            listaIngredientes.remove(texto);
        });

        itemLayout.addView(btnDelete);
        ingredientsList.addView(itemLayout);
    }

    private void mostrarSelectorTiempo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        builder.setView(dialogView);
        NumberPicker npH = dialogView.findViewById(R.id.npHours);
        NumberPicker npM = dialogView.findViewById(R.id.npMinutes);
        npH.setMinValue(0); npH.setMaxValue(24);
        npM.setMinValue(0); npM.setMaxValue(59);
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btnConfirmTime).setOnClickListener(v -> {
            int h = npH.getValue();
            int m = npM.getValue();
            totalMinutes = (h * 60) + m;
            tvPrepTimeValue.setText(h + "h " + m + "min");
            dialog.dismiss();
        });
        dialog.show();
    }

    private void iniciarRecorte(Uri sourceUri) {
        File tempFile = new File(getCacheDir(), "temp_crop.jpg");
        Uri destUri = Uri.fromFile(tempFile);

        UCrop.Options opt = new UCrop.Options();
        opt.setToolbarTitle("Recortar Imagen");
        opt.setToolbarColor(ContextCompat.getColor(this, android.R.color.black));
        opt.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        opt.setToolbarWidgetColor(ContextCompat.getColor(this, android.R.color.white));

        Intent intent = UCrop.of(sourceUri, destUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1000, 1000)
                .withOptions(opt)
                .getIntent(this);

        cropResultLauncher.launch(intent);
    }
}
