package com.example.refrimancia.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.util.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.modelo.entidad.Usuario;
import com.example.refrimancia.modelo.response.RespuestaUnica;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentanaEditarPerfil extends AppCompatActivity {

    private UsuarioService usuarioService;
    private SessionManager sessionManager;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagenUri;
    private Uri croppedImageUri;
    private String imagenActualUrl;

    EditText etNombreUserEditar;
    EditText etNombreEditar;
    EditText etApellidosEditar;
    EditText etFechaEditar;
    ImageView ivImagenEditar;
    Button botonActualizarEditar;
    Button botonCancelarEditar;
    Button botonEditarImagen;

    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_editar_perfil);

        sessionManager = new SessionManager(this);
        idUsuario = sessionManager.fetchUserId();

        Log.d("EDITAR", "ID Usuario: " + idUsuario);


        etNombreUserEditar = findViewById(R.id.etNombreUserEditar);
        etNombreEditar = findViewById(R.id.etNombreEditar);
        etApellidosEditar = findViewById(R.id.etApellidosEditar);
        etFechaEditar = findViewById(R.id.etFechaEditar);
        ivImagenEditar = findViewById(R.id.ivImagenEditar);

        botonActualizarEditar = findViewById(R.id.botonActualizarEditar);
        botonCancelarEditar = findViewById(R.id.botonCancelarEditar);
        botonEditarImagen = findViewById(R.id.botonEditarImagen);

        usuarioService = ClienteRetrofit.obtenerInstancia(this).create(UsuarioService.class);

        //Cargar los datos del usuario
        cargarPerfil();


        botonCancelarEditar.setOnClickListener(v -> finish());
        botonEditarImagen.setOnClickListener(v -> abrirSelectorImagen());
        botonActualizarEditar.setOnClickListener(v -> actualizarUsuario());
    }


    //Metodo para cargar los datos del perfil
    private void cargarPerfil() {

        usuarioService.obtenerPerfil().enqueue(new Callback<RespuestaUnica<Usuario>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Usuario>> call,
                    Response<RespuestaUnica<Usuario>> response) {

                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {

                    Usuario data = response.body().getData();

                    // Actualizar ID real del usuario
                    idUsuario = data.getIdUsuario();

                    etNombreUserEditar.setText(data.getNombreUsuario());

                    // Separar nombre completo
                    String nombreCompleto = data.getNombreCompleto();
                    if (nombreCompleto != null && nombreCompleto.contains(" ")) {
                        String[] partes = nombreCompleto.split(" ", 2);
                        etNombreEditar.setText(partes[0]);
                        etApellidosEditar.setText(partes[1]);
                    } else {
                        etNombreEditar.setText(nombreCompleto);
                    }

                    // Formatear fecha
                    if (data.getFechaNac() != null) {
                        etFechaEditar.setText(data.getFechaNac().split("T")[0]);
                    }

                    // Guardar la ruta de la imagen y cargarla
                    imagenActualUrl = data.getUrlFotoPerfil();
                    Glide.with(VentanaEditarPerfil.this)
                            .load(imagenActualUrl)
                            .into(ivImagenEditar);

                } else {
                    Toast.makeText(VentanaEditarPerfil.this,
                            "Error al cargar perfil",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaUnica<Usuario>> call, Throwable t) {
                Toast.makeText(VentanaEditarPerfil.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Metodo para el boton que abre el selector de imagenes
    public void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    // Recibe la imagen seleccionada por el usuario y la muestra en el ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Imagen seleccionada desde galería
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imagenUri = data.getData();
            // Abrimos uCrop para recortar
            iniciarRecorte(imagenUri);
        }
        // Imagen ya recortada
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            croppedImageUri = UCrop.getOutput(data);

            if (croppedImageUri != null) {
                ivImagenEditar.setImageURI(croppedImageUri);
            }
        }
        // Error en recorte
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Error al recortar imagen", Toast.LENGTH_SHORT).show();
        }
    }
    //Metodo para iniciar el recorte de imagen
    public void iniciarRecorte(Uri sourceUri) {

        Uri destinationUri = Uri.fromFile(
                new File(getCacheDir(), "imagen_recortada.jpg")
        );

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(500, 500)
                .start(this);
    }

    //Metodo para actualizar los datos del usuario
    private void actualizarUsuario() {

        String nombreUser = etNombreUserEditar.getText().toString().trim();
        String nombre = etNombreEditar.getText().toString().trim();
        String apellidos = etApellidosEditar.getText().toString().trim();
        String fecha = etFechaEditar.getText().toString().trim();

        String nombreCompleto = nombre + " " + apellidos;

        //Validar la fecha
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-M-d");
            parser.setLenient(false);

            Date date = parser.parse(fecha);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            fecha = formatter.format(date);

        } catch (Exception e) {
            etFechaEditar.setError("Formato incorrecto (yyyy-MM-dd)");
            Toast.makeText(this,
                    "Introduce una fecha válida",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Crear los REQUEST BODY
        RequestBody nombreUserBody =
                RequestBody.create(nombreUser, MediaType.parse("text/plain"));

        RequestBody nombreCompletoBody =
                RequestBody.create(nombreCompleto, MediaType.parse("text/plain"));

        RequestBody fechaBody =
                RequestBody.create(fecha, MediaType.parse("text/plain"));

        MultipartBody.Part imagenPart = null;

        if (croppedImageUri != null) {
            File file = crearArchivoDesdeUri(croppedImageUri);

            RequestBody requestFile =
                    RequestBody.create(file, MediaType.parse("image/*"));

            imagenPart = MultipartBody.Part.createFormData(
                    "imagen_perfil",
                    file.getName(),
                    requestFile
            );
        }

        RequestBody contrasenaVacia = RequestBody.create("", MediaType.parse("text/plain"));
        RequestBody correoVacio = RequestBody.create("", MediaType.parse("text/plain"));

        //Realizar la llamada a la API
        Call<ResponseBody> call = usuarioService.modificarUsuario(
                idUsuario,
                nombreUserBody,
                contrasenaVacia,
                nombreCompletoBody,
                correoVacio,
                fechaBody,
                imagenPart
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(VentanaEditarPerfil.this,
                            "Perfil actualizado",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("API_ERROR", "Código: " + response.code());
                    try {
                        Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(VentanaEditarPerfil.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(VentanaEditarPerfil.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Metodo auxiliar para convertir URI a FILE
    private File crearArchivoDesdeUri(Uri uri) {

        File file = new File(getCacheDir(), "imagen_subida.jpg");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}