package com.example.refrimancia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExampleActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        sessionManager = new SessionManager(this);

        // Referenciar el TextView de bienvenida
        tvWelcome = findViewById(R.id.tvWelcome);

        // Recuperar el nombre del usuario desde el SessionManager
        String nombreUsuario = sessionManager.fetchUserName();
        int idUsuario = sessionManager.fetchUserId();

        // Mostrar el nombre en pantalla
        tvWelcome.setText("¡Bienvenido, " + nombreUsuario + "! (ID: " + idUsuario + ")");

        Button btnBack = findViewById(R.id.btnBack);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ExampleActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(ExampleActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
