package com.example.refrimancia;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.refrimancia.ui.ContenedorPrincipalActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvForgotPassword, tvCreateAccount;
    
    // Variables para la sesión y la API
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Inicializar SessionManager
        sessionManager = new SessionManager(this);

        // 2. AUTO-LOGIN: Si ya hay un token guardado, saltamos directamente a la pantalla principal
        if (sessionManager.fetchAuthToken() != null) {
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

        // Estética: subrayado
        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvCreateAccount.setPaintFlags(tvCreateAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // 3. Inicializar ApiService mediante tu RetrofitClient
        apiService = RetrofitClient.getApiService(this);

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
        
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // 4. Guardamos el token y los datos del usuario en la sesión
                    sessionManager.saveAuthToken(loginResponse.getToken());
                    
                    if (loginResponse.getData() != null) {
                        sessionManager.saveUserDetail(
                            loginResponse.getData().getId_usuario(),
                            loginResponse.getData().getNombre_usuario()
                        );
                    }

                    // Navegar a la actividad principal tras el login exitoso
                    Intent intent = new Intent(LoginActivity.this, ContenedorPrincipalActivity.class);
                    startActivity(intent);
                    finish(); 
                } else {
                    Toast.makeText(LoginActivity.this, "Error: Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(LoginActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
