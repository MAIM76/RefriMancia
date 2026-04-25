package com.example.refrimancia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class VentanaEditarPerfil extends AppCompatActivity {

    private ApiService apiService;
    private Retrofit retrofit;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagenUri;
    private Uri croppedImageUri;

    EditText etNombreUserEditar;
    EditText etNombreEditar;
    EditText etApellidosEditar;
    EditText etFechaEditar;
    ImageView ivImagenEditar;
    Button botonActualizarEditar;
    Button botonCancelarEditar;
    Button botonEditarImagen;

    private String token = "TOKEN_PRUEBA"; // luego se tiene que cambiar
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_editar_perfil);

        //Recibir datos
        token = getIntent().getStringExtra("TOKEN");
        idUsuario = getIntent().getIntExtra("ID_USUARIO", -1);
        Log.d("EDITAR", "Token: " + token);
        Log.d("EDITAR", "ID Usuario: " + idUsuario);

        etNombreUserEditar = findViewById(R.id.etNombreUserEditar);
        etNombreEditar = findViewById(R.id.etNombreEditar);
        etApellidosEditar = findViewById(R.id.etApellidosEditar);
        etFechaEditar = findViewById(R.id.etFechaEditar);
        ivImagenEditar = findViewById(R.id.ivImagenEditar);

        botonActualizarEditar = findViewById(R.id.botonActualizarEditar);
        botonCancelarEditar = findViewById(R.id.botonCancelarEditar);
        botonEditarImagen = findViewById(R.id.botonEditarImagen);

        // Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://refrimacia-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        cargarPerfil();

        botonCancelarEditar.setOnClickListener(v -> finish());
        botonEditarImagen.setOnClickListener(v -> abrirSelectorImagen());
        botonActualizarEditar.setOnClickListener(v -> actualizarUsuario());
    }
    private void cargarPerfil() {

        Call<PerfilRespuesta> call = apiService.obtenerPerfil("Bearer " + token);

        call.enqueue(new Callback<PerfilRespuesta>() {
            @Override
            public void onResponse(Call<PerfilRespuesta> call, Response<PerfilRespuesta> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Perfil data = response.body().getData();

                    // Guardamos el ID (más seguro que intent)
                    idUsuario = data.getIdUsuario();

                    etNombreUserEditar.setText(data.getNombreUsuario());

                    // Separar nombre y apellidos
                    String nombreCompleto = data.getNombreCompleto();
                    if (nombreCompleto.contains(" ")) {
                        String[] partes = nombreCompleto.split(" ", 2);
                        etNombreEditar.setText(partes[0]);
                        etApellidosEditar.setText(partes[1]);
                    } else {
                        etNombreEditar.setText(nombreCompleto);
                    }

                    // Fecha (quitar la T)
                    String fecha = data.getFechaNac().split("T")[0];
                    etFechaEditar.setText(fecha);

                    // Imagen (si usas Glide mejor)
                    Glide.with(VentanaEditarPerfil.this).load(data.getImagenPerfil()).into(ivImagenEditar);

                } else {
                    Toast.makeText(VentanaEditarPerfil.this,
                            "Error al cargar perfil",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilRespuesta> call, Throwable t) {
                Toast.makeText(VentanaEditarPerfil.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imagenUri = data.getData();
            iniciarRecorte(imagenUri);
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            croppedImageUri = UCrop.getOutput(data);

            if (croppedImageUri != null) {
                ivImagenEditar.setImageURI(croppedImageUri);
            }
        }

        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error al recortar imagen", Toast.LENGTH_SHORT).show();
        }
    }
    public void iniciarRecorte(Uri sourceUri) {

        Uri destinationUri = Uri.fromFile(
                new File(getCacheDir(), "imagen_recortada.jpg")
        );

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(500, 500)
                .start(this);
    }

    private void actualizarUsuario() {

        String nombreUser = etNombreUserEditar.getText().toString().trim();
        String nombre = etNombreEditar.getText().toString().trim();
        String apellidos = etApellidosEditar.getText().toString().trim();
        String fecha = etFechaEditar.getText().toString().trim();

        String nombreCompleto = nombre + " " + apellidos;
        if (nombreUser.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // Convertir a RequestBody
        RequestBody nombreUserBody =
                RequestBody.create(nombreUser, MediaType.parse("text/plain"));

        RequestBody nombreCompletoBody =
                RequestBody.create(nombreCompleto, MediaType.parse("text/plain"));

        RequestBody fechaBody =
                RequestBody.create(fecha, MediaType.parse("text/plain"));

        MultipartBody.Part imagenPart = null; // luego lo añadimos si quieres

        Call<RegistroRespuesta> call = apiService.actualizarUsuario(
                "Bearer " + token,
                idUsuario,
                nombreUserBody,
                nombreCompletoBody,
                fechaBody,
                imagenPart
        );

        call.enqueue(new Callback<RegistroRespuesta>() {
            @Override
            public void onResponse(Call<RegistroRespuesta> call, Response<RegistroRespuesta> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(VentanaEditarPerfil.this,
                            "Perfil actualizado",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(VentanaEditarPerfil.this,
                            "Error al actualizar",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroRespuesta> call, Throwable t) {
                Toast.makeText(VentanaEditarPerfil.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}

