package com.example.refrimancia.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import androidx.core.content.res.ResourcesCompat;
import android.widget.ImageView;

import com.example.refrimancia.R;
import com.example.refrimancia.util.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.model.request.LoginRequest;
import com.example.refrimancia.model.response.Login;
import com.example.refrimancia.ui.pablo.ContenedorPrincipalActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvForgotPassword, tvCreateAccount;
    
    // Variables para la sesión y la API
    private SessionManager sessionManager;
    private UsuarioService usuarioService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Inicializar SessionManager
        sessionManager = new SessionManager(this);

        // 2. AUTO-LOGIN: Si hay sesión válida (token + userId), saltamos directamente a la pantalla principal
        if (sessionManager.isSessionValid()) {
            Intent intent = new Intent(this, ContenedorPrincipalActivity.class);
            startActivity(intent);
            finish();
            return; 
        }

        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        
        // Lógica para el botón de mostrar/ocultar contraseña
        ImageView ivShowPassword = findViewById(R.id.ivShowPassword);
        ivShowPassword.setOnClickListener(v -> {
            int cursorPosition = etPassword.getSelectionStart();
            if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Mostrar contraseña
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowPassword.setImageResource(R.drawable.ic_visibility);
            } else {
                // Ocultar contraseña
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowPassword.setImageResource(R.drawable.ic_visibility_off);
            }
            // Importante: Al cambiar el inputType, Android resetea la fuente. Forzamos Alexandria.
            etPassword.setTypeface(ResourcesCompat.getFont(this, R.font.alexandria));
            etPassword.setSelection(cursorPosition);
        });

        // Estética: subrayado
        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvCreateAccount.setPaintFlags(tvCreateAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // 3. Inicializar UsuarioService mediante ClienteRetrofit
        usuarioService = ClienteRetrofit.obtenerInstancia(this).create(UsuarioService.class);

        // Lógica del botón Login
        btnLogin.setOnClickListener(v -> {
            String usuario = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                realizarLogin(usuario, password);
            }
        });

        // 345: Temporary code for testing navigation to the new screen.
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, VentanaVerificar.class));
        });

        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, VentanaRegistro.class));
        });
    }

    private void realizarLogin(String user, String pass) {
        LoginRequest loginRequest = new LoginRequest(user, pass);

        Call<Login> call = usuarioService.login(loginRequest);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Login loginResponse = response.body();

                    // 4. Guardamos el token y los datos del usuario en la sesión
                    if (loginResponse.getToken() != null && loginResponse.getData() != null) {
                        sessionManager.saveAuthToken(loginResponse.getToken());
                        sessionManager.saveUserDetail(
                            loginResponse.getIdUsuario(),
                            loginResponse.getNombreUsuario()
                        );
                        if (loginResponse.getImagenPerfil() != null) {
                            sessionManager.saveUserPhoto(loginResponse.getImagenPerfil());
                        }

                        // Navegar a la actividad principal solo si la sesión quedó válida
                        if (sessionManager.isSessionValid()) {
                            Intent intent = new Intent(LoginActivity.this, ContenedorPrincipalActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error al guardar la sesión. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: respuesta del servidor incompleta.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(LoginActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
