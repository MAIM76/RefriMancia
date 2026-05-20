package com.example.refrimancia.ui;

import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.model.response.Registro;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
import android.text.InputFilter;
import android.text.InputType;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class VentanaRegistro extends AppCompatActivity {
    private UsuarioService usuarioService;

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
    ImageView ivMostrarPasswordRegistro;
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
        setContentView(R.layout.ventana_registro);

        tvNombreUserRegistro = findViewById(R.id.tvNombreUserRegistro);
        tvCorreoRegistro = findViewById(R.id.tvCorreoRegistro);
        tvContrasenaRegistro = findViewById(R.id.tvContrasenaRegistro);
        tvNombreRegistro = findViewById(R.id.tvNombreRegistro);
        tvApellidosRegistro = findViewById(R.id.tvApellidosRegistro);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        etNombreUserRegistro = findViewById(R.id.etNombreUserRegistro);
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etpContrasenaRegistro = findViewById(R.id.etpContrasenaRegistro);
        ivMostrarPasswordRegistro = findViewById(R.id.ivMostrarPasswordRegistro);
        etNombreRegistro = findViewById(R.id.etNombreRegistro);
        etApellidosRegistro = findViewById(R.id.etApellidosRegistro);
        etFechaRegistro = findViewById(R.id.etFechaRegistro);
        botonRegistro = findViewById(R.id.botonRegistro);
        botonCancelarRegistrar = findViewById(R.id.botonCancelarRegistrar);
        botonSelecImagenRegistro = findViewById(R.id.botonSelecImagenRegistro);
        ivRegistro = findViewById(R.id.ivRegistro);

        // Lógica para mostrar/ocultar contraseña
        ivMostrarPasswordRegistro.setOnClickListener(v -> {
            int cursorPosition = etpContrasenaRegistro.getSelectionStart();
            // Si está oculta → mostrar
            if (etpContrasenaRegistro.getInputType() ==
                    (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etpContrasenaRegistro.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                ivMostrarPasswordRegistro.setImageResource(R.drawable.ic_visibility);
            } else {
                // Si está visible → ocultar
                etpContrasenaRegistro.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                ivMostrarPasswordRegistro.setImageResource(R.drawable.ic_visibility_off);
            }
            // Mantener la fuente personalizada
            etpContrasenaRegistro.setTypeface(
                    ResourcesCompat.getFont(this, R.font.alexandria)
            );
            // Mantener el cursor al final
            etpContrasenaRegistro.setSelection(cursorPosition);
        });

        //Filtro para los EditText de Nombre y Apellidos: Acepta solo letras, espacios y guion simple
        FiltroSoloLetras filtroSoloLetras = new FiltroSoloLetras();
        etNombreRegistro.setFilters(new InputFilter[]{filtroSoloLetras});
        etApellidosRegistro.setFilters(new InputFilter[]{filtroSoloLetras});

        usuarioService = ClienteRetrofit.obtenerInstancia(this).create(UsuarioService.class);

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

            // 1. Nombre de usuario
            if (nombreUser.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un nombre de usuario", Toast.LENGTH_SHORT).show();
                etNombreUserRegistro.requestFocus();
                return;
            }
            if (nombreUser.length() > 50) {
                Toast.makeText(this, "El nombre de usuario es demasiado largo (máximo 50)", Toast.LENGTH_SHORT).show();
                etNombreUserRegistro.requestFocus();
                return;
            }

            // 2. Correo electrónico
            if (correo.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe tu correo", Toast.LENGTH_SHORT).show();
                etCorreoRegistro.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreoRegistro.setError("Correo no válido");
                Toast.makeText(this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show();
                etCorreoRegistro.requestFocus();
                return;
            }

            // 3. Contraseña
            if (password.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe una contraseña", Toast.LENGTH_SHORT).show();
                etpContrasenaRegistro.requestFocus();
                return;
            }
            if (!password.matches(regexPassword)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número", Toast.LENGTH_LONG).show();
                etpContrasenaRegistro.requestFocus();
                return;
            }

            // 4. Nombre
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe tu nombre", Toast.LENGTH_SHORT).show();
                etNombreRegistro.requestFocus();
                return;
            }

            // 5. Apellidos
            if (apellidos.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe tus apellidos", Toast.LENGTH_SHORT).show();
                etApellidosRegistro.requestFocus();
                return;
            }

            // 6. Validación de Nombre Completo (Límite 100).
            if (nombreCompleto.length() > 100) {
                Toast.makeText(this, "El nombre y apellidos combinados no pueden superar los 100 caracteres", Toast.LENGTH_SHORT).show();
                etNombreRegistro.requestFocus();
                return;
            }

            // 7. Fecha de nacimiento
            if (fecha.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce tu fecha de nacimiento", Toast.LENGTH_SHORT).show();
                etFechaRegistro.requestFocus();
                return;
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
        Call<Registro> call = usuarioService.registrarUsuario(
                nombreUsuarioBody,
                passwordBody,
                nombreCompletoBody,
                correoBody,
                fechaBody,
                imagenPart
        );
        call.enqueue(new Callback<Registro>() {
            @Override
            public void onResponse(Call<Registro> call, Response<Registro> response) {
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
            public void onFailure(Call<Registro> call, Throwable t) {
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
