package com.example.refrimancia.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.model.request.CambiarPassRequest;
import com.example.refrimancia.model.response.Estado;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentanaRecuperarPassword extends AppCompatActivity {
    private UsuarioService usuarioService;
    String correo; //Para recuperar el correo enviado desde la otra ventana
    TextView tvTituloRecuperar;
    TextView tvCodigoRecuperar;
    TextView tvNuevaPasswordRecuperar;
    TextView tvRepetirPasswordRecuperar;
    EditText etCodigoRecuperar;
    EditText etpNuevaPasswordRecuperar;
    EditText etpRepetirPasswordRecuperar;
    Button botonActualizarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_recuperar_password);

        tvTituloRecuperar = findViewById(R.id.tvTituloRecuperar);
        tvCodigoRecuperar = findViewById(R.id.tvCodigoRecuperar);
        tvNuevaPasswordRecuperar = findViewById(R.id.tvNuevaPasswordRecuperar);
        tvRepetirPasswordRecuperar = findViewById(R.id.tvRepetirPasswordRecuperar);
        etCodigoRecuperar = findViewById(R.id.etCodigoRecuperar);
        etpNuevaPasswordRecuperar = findViewById(R.id.etpNuevaPasswordRecuperar);
        etpRepetirPasswordRecuperar = findViewById(R.id.etpRepetirPasswordRecuperar);
        botonActualizarPassword = findViewById(R.id.botonActualizarPassword);

        // Recuperar correo
        correo = getIntent().getStringExtra("correo");

        usuarioService = ClienteRetrofit.obtenerInstancia(this).create(UsuarioService.class);

        botonActualizarPassword.setOnClickListener(v -> cambiarPassword());
    }

    //Metodo actualizar contraseña
    public void cambiarPassword() {
        String codigo = etCodigoRecuperar.getText().toString().trim();
        String nuevaPwd = etpNuevaPasswordRecuperar.getText().toString().trim();
        String repetirPwd = etpRepetirPasswordRecuperar.getText().toString().trim();
        String regexPassword = "^(?=.*[A-Z])(?=.*\\d).{8,}$";

        // VALIDACIONES
        if (codigo.isEmpty() || nuevaPwd.isEmpty() || repetirPwd.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nuevaPwd.equals(repetirPwd)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nuevaPwd.matches(regexPassword)) {
            Toast.makeText(this,
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // CREAR REQUEST
        CambiarPassRequest request =
                new CambiarPassRequest(correo, codigo, nuevaPwd);
        // LLAMADA API
        Call<Estado> call = usuarioService.cambiarContrasena(request);
        call.enqueue(new Callback<Estado>() {
            @Override
            public void onResponse(Call<Estado> call, Response<Estado> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(VentanaRecuperarPassword.this,
                            "Contraseña actualizada correctamente",
                            Toast.LENGTH_SHORT).show();

                    finish(); // volver al login
                } else {
                    Toast.makeText(VentanaRecuperarPassword.this,
                            "Código incorrecto o caducado",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                Toast.makeText(VentanaRecuperarPassword.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
