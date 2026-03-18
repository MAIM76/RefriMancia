package com.example.refrimancia;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VentanaRegistro extends AppCompatActivity {
    // Configuración de Retrofit
    private Retrofit retrofit;
    private ApiService apiService;

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
    String imagenPorDefecto = "https://mi-servidor.com/foto.png";
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

        //Para que la fecha se envíe bien
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        // Configurar Retrofit con tu URL de Render
        retrofit = new Retrofit.Builder()
                .baseUrl("https://refrimacia-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
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

            String regexPassword = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

            if (nombreUser.isEmpty() || password.isEmpty() || correo.isEmpty()
                    || nombre.isEmpty() || apellidos.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreoRegistro.setError("Correo no válido");
                Toast.makeText(this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show();

            }  else if (!password.matches(regexPassword)) {

                Toast.makeText(this,
                        "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número",
                        Toast.LENGTH_LONG).show();

            } else {
                registrar(nombreUser, password, correo, nombreCompleto, fecha);
            }
        });
        botonCancelarRegistrar.setOnClickListener(v -> finish());
    }

    //Metodo para boton registrar
    public void registrar(String nombreUser, String password, String correo, String nombreCompleto, String fecha) {
        Log.d("REGISTRO", "Entrando en metodo registrar");
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaNac = null;
        try {
            fechaNac = formato.parse(fecha);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha incorrecto. Usa yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }
        RegistroRequest request = new RegistroRequest(nombreUser, password, correo, nombreCompleto, fechaNac, imagenPorDefecto);
        Log.d("API_CALL", "Enviando registro al servidor");

        Call<RegistroRespuesta> call = apiService.registro(request);
        call.enqueue(new Callback<RegistroRespuesta>() {
            @Override
            public void onResponse(Call<RegistroRespuesta> call, Response<RegistroRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Mostrar mensaje de exito y cerrar ventana
                    Toast.makeText(VentanaRegistro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    // Mostrar Toast solo cuando el login es incorrecto
                    Toast.makeText(VentanaRegistro.this, "Error: Usuario o correo ya registrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistroRespuesta> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(VentanaRegistro.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
