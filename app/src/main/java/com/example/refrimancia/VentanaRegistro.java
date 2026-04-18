package com.example.refrimancia;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class VentanaRegistro extends AppCompatActivity {
    // Configuración de Retrofit
    private Retrofit retrofit;
    private ApiService apiService;

    // Para la imagen
    private static final int PICK_IMAGE_REQUEST = 1; //identificador que usa Android para saber:“esta respuesta viene de la galería”
    private Uri imagenUri;
    private Uri croppedImageUri;

    TextView tvTituloRegistro;
    TextView tvNombreUserRegistro;
    TextView tvCorreoRegistro;
    TextView tvContrasenaRegistro;
    TextView tvNombreRegistro;
    TextView tvApellidosRegistro;
    TextView tvFechaRegistro;
    EditText etNombreUserRegistro;
    EditText etCorreoRegistro;
    EditText etpContrasenaRegistro;
    EditText etNombreRegistro;
    EditText etApellidosRegistro;
    EditText etFechaRegistro;
    Button botonRegistro;
    Button botonCancelarRegistrar;
    ImageView ivRegistro;
    Button botonSelecImagenRegistro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ventana_registro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTituloRegistro = findViewById(R.id.tvTituloRegistro);
        tvNombreUserRegistro = findViewById(R.id.tvNombreUserRegistro);
        tvCorreoRegistro = findViewById(R.id.tvCorreoRegistro);
        tvContrasenaRegistro = findViewById(R.id.tvContrasenaRegistro);
        tvNombreRegistro = findViewById(R.id.tvNombreRegistro);
        tvApellidosRegistro = findViewById(R.id.tvApellidosRegistro);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        etNombreUserRegistro = findViewById(R.id.etNombreUserRegistro);
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etpContrasenaRegistro = findViewById(R.id.etpContrasenaRegistro);
        etNombreRegistro = findViewById(R.id.etNombreRegistro);
        etApellidosRegistro = findViewById(R.id.etApellidosRegistro);
        etFechaRegistro = findViewById(R.id.etFechaRegistro);
        botonRegistro = findViewById(R.id.botonRegistro);
        botonCancelarRegistrar = findViewById(R.id.botonCancelarRegistrar);
        botonSelecImagenRegistro = findViewById(R.id.botonSelecImagenRegistro);
        ivRegistro = findViewById(R.id.ivRegistro);


        // Configurar Retrofit con tu URL de Render
        retrofit = new Retrofit.Builder()
                .baseUrl("https://refrimacia-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        //Asociar el boton con el metodo
        botonRegistro.setOnClickListener(v -> {
            String nombreUser = etNombreUserRegistro.getText().toString().trim();
            String correo = etCorreoRegistro.getText().toString().trim();
            String password = etpContrasenaRegistro.getText().toString().trim();
            String nombre = etNombreRegistro.getText().toString().trim();
            String apellidos = etApellidosRegistro.getText().toString().trim();
            String nombreCompleto = nombre + " " + apellidos;
            String fecha = etFechaRegistro.getText().toString().trim();

            String regexPassword = "^(?=.*[A-Z])(?=.*\\d).{8,}$"; //Formato de la password (al menos 1 mayúscula, 1 número y mínimo 8 caracteres)

            if (nombreUser.isEmpty() || password.isEmpty() || correo.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();

            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreoRegistro.setError("Correo no válido");
                Toast.makeText(this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show();

            } else if (!password.matches(regexPassword)) {
                Toast.makeText(this,
                        "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número",
                        Toast.LENGTH_LONG).show();

            } else {
                // Validar y formatear la fecha
                try {
                    // Acepta fechas tipo 1999-5-4
                    SimpleDateFormat parser = new SimpleDateFormat("yyyy-M-d");
                    parser.setLenient(false);
                    Date date = parser.parse(fecha);
                    // Convierte a formato correcto 1999-05-04
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    fecha = formatter.format(date);
                } catch (Exception e) {
                    etFechaRegistro.setError("Formato incorrecto (yyyy-MM-dd)");
                    Toast.makeText(this, "Introduce una fecha válida", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Si todo es correcto se llama al metodo
                registrar(nombreUser, password, correo, nombreCompleto, fecha);
            }
        });
        botonCancelarRegistrar.setOnClickListener(v -> finish());
        // Abre la galería para que el usuario seleccione una imagen de perfil
        botonSelecImagenRegistro.setOnClickListener(v -> abrirSelectorImagen());
    }

    //Metodo para el boton que abre el selector de imagenes
    public void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Solo permitimos seleccionar archivos de tipo imagen
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
        if (requestCode == UCrop.REQUEST_CROP
                && resultCode == RESULT_OK) {

            croppedImageUri = UCrop.getOutput(data);

            if (croppedImageUri != null) {
                ivRegistro.setImageURI(croppedImageUri);
            }
        }
        // Error en recorte
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this,
                    "Error al recortar imagen",
                    Toast.LENGTH_SHORT).show();
        }
    }
    //Metodo para iniciar el recorte de imagen
    public void iniciarRecorte(Uri sourceUri) {

        Uri destinationUri = Uri.fromFile(
                new File(getCacheDir(), "imagen_recortada.jpg")
        );

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) //Foto cuadrada
                .withMaxResultSize(500, 500) //Reducir peso del archivo
                .start(this);
    }
    //Metodo para boton registrar
    public void registrar(String nombreUser, String password, String correo, String nombreCompleto, String fecha) {
        Log.d("REGISTRO", "Entrando en metodo registrar");
        // Convertimos todos los datos de TEXTO a RequestBody para enviarlos en multipart/form-data
        RequestBody nombreUsuarioBody =
                RequestBody.create(nombreUser, MediaType.parse("text/plain"));
        RequestBody passwordBody =
                RequestBody.create(password, MediaType.parse("text/plain"));
        RequestBody correoBody =
                RequestBody.create(correo, MediaType.parse("text/plain"));
        RequestBody nombreCompletoBody =
                RequestBody.create(nombreCompleto, MediaType.parse("text/plain"));
        RequestBody fechaBody =
                RequestBody.create(fecha, MediaType.parse("text/plain"));
        MultipartBody.Part imagenPart = null;

        // Si el usuario ha seleccionado una imagen, la convertimos a archivo y la añadimos al form-data
        // Ademas, usa la imagen recortada
        if (croppedImageUri  != null) {
            File archivoImagen = crearArchivoDesdeUri(croppedImageUri );
            RequestBody requestFile =
                    RequestBody.create(archivoImagen, MediaType.parse("image/*"));
            imagenPart = MultipartBody.Part.createFormData(
                    "imagen_perfil",
                    archivoImagen.getName(),
                    requestFile
            );
        }
        // Realizamos la llamada asíncrona al servidor para registrar al usuario
        Call<RegistroRespuesta> call = apiService.registro(
                nombreUsuarioBody,
                passwordBody,
                correoBody,
                nombreCompletoBody,
                fechaBody,
                imagenPart
        );
        call.enqueue(new Callback<RegistroRespuesta>() {
            @Override
            public void onResponse(Call<RegistroRespuesta> call, Response<RegistroRespuesta> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VentanaRegistro.this,
                            "Usuario registrado correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(VentanaRegistro.this,
                            "Error: Usuario o correo ya registrados",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RegistroRespuesta> call, Throwable t) {
                Toast.makeText(VentanaRegistro.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    //Metodo auxiliar para convertir URI a FILE
    private File crearArchivoDesdeUri(Uri uri) {
        // Creamos un archivo temporal en la caché de la aplicación
        // Copiamos el contenido de la imagen seleccionada dentro del archivo temporal
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
