package com.example.refrimancia.ui;

import android.content.Intent;
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

import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.model.response.RecetaCreada;
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
    private Button btnAddIngredient, btnPublish;
    private ImageView recipeImagePlaceholder;
    private LinearLayout timeContainer;
    private TextView tvPrepTimeValue;
    private Spinner spinnerCategory;

    private List<String> listaIngredientes = new ArrayList<>();
    private File croppedImageFile = null;
    private int totalMinutes = 0;
    private static final String PERMANENT_IMAGE_NAME = "recipe_upload.jpg";

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

        ingredientsList = findViewById(R.id.ingredientsList);
        etNewIngredient = findViewById(R.id.etNewIngredient);
        etRecipeName = findViewById(R.id.etRecipeName);
        etRecipeDescription = findViewById(R.id.etRecipeDescription);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnPublish = findViewById(R.id.btnPublish);
        recipeImagePlaceholder = findViewById(R.id.recipeImagePlaceholder);
        timeContainer = findViewById(R.id.timeContainer);
        tvPrepTimeValue = findViewById(R.id.tvPrepTimeValue);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        String[] categorias = {"Desayuno", "Almuerzo", "Cena", "Postre", "Snack"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        recipeImagePlaceholder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

        btnPublish.setOnClickListener(v -> publicarReceta());

        File savedImage = new File(getFilesDir(), PERMANENT_IMAGE_NAME);
        if (savedImage.exists()) {
            croppedImageFile = savedImage;
            recipeImagePlaceholder.setImageURI(Uri.fromFile(croppedImageFile));
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
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
        tv.setTextSize(16);
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

    private void publicarReceta() {
        String titulo = etRecipeName.getText().toString().trim();
        String desc = etRecipeDescription.getText().toString().trim();
        String tipo = spinnerCategory.getSelectedItem().toString();

        if (titulo.isEmpty() || desc.isEmpty() || listaIngredientes.isEmpty() || croppedImageFile == null || totalMinutes == 0) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        if (!croppedImageFile.exists()) {
            Toast.makeText(this, "Error con la imagen. Selecciónala de nuevo.", Toast.LENGTH_LONG).show();
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

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), croppedImageFile);
        MultipartBody.Part bodyImagen = MultipartBody.Part.createFormData("imagen_receta", croppedImageFile.getName(), reqFile);

        btnPublish.setEnabled(false);
        btnPublish.setText("Publicando...");

        RecetaService recetaService = ClienteRetrofit.obtenerInstancia(this).create(RecetaService.class);
        Call<RecetaCreada> call = recetaService.crearRecetaConRespuesta(reqTitulo, reqIngr, reqDesc, reqTipo, reqTiempo, bodyImagen);

        call.enqueue(new Callback<RecetaCreada>() {
            @Override
            public void onResponse(Call<RecetaCreada> call, Response<RecetaCreada> response) {
                btnPublish.setEnabled(true);
                btnPublish.setText("Publicar");
                if (response.isSuccessful()) {
                    Toast.makeText(CreateRecipeActivity.this, "¡Receta publicada!", Toast.LENGTH_LONG).show();
                    if (croppedImageFile != null && croppedImageFile.exists()) {
                        croppedImageFile.delete();
                    }
                    finish();
                } else {
                    Toast.makeText(CreateRecipeActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecetaCreada> call, Throwable t) {
                btnPublish.setEnabled(true);
                btnPublish.setText("Publicar");
                Toast.makeText(CreateRecipeActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            tvPrepTimeValue.setText(h + "h " + m + " min");
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
