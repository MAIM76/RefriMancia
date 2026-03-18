package com.example.refrimancia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;



public class VentanaVerificar extends AppCompatActivity {
    TextView tvTituloVerificar;
    TextView tvCorreoVerificar;
    EditText etCorreoVerificar;
    Button botonEnviarCode;
    Button botonCancelarVerificar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_verificar);

        tvTituloVerificar = findViewById(R.id.tvTituloVerificar);
        tvCorreoVerificar = findViewById(R.id.tvCorreoVerificar);
        etCorreoVerificar = findViewById(R.id.etCorreoVerificar);
        botonEnviarCode = findViewById(R.id.botonEnviarCode);
        botonCancelarVerificar = findViewById(R.id.botonCancelarVerificar);

        botonEnviarCode.setOnClickListener(v -> {
            String correo = etCorreoVerificar.getText().toString().trim();
            //Verificamos el correo para que no esté vacío y cumpla con el patrón habitual de correo
            if (correo.isEmpty()) {
                etCorreoVerificar.setError("Por favor, introduce un correo");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreoVerificar.setError("Correo no válido");
                return;
            }

            // AQUI IRA LA API MAS ADELANTE, CUANDO TENGA PREPARADAS BIEN LAS VENTANAS

            // Abrir la ventana de recuperación
            Intent intent = new Intent(VentanaVerificar.this, VentanaRecuperarPassword.class);
            startActivity(intent);
            // Cerrar esta pantalla
            finish();
        });
        botonCancelarVerificar.setOnClickListener(v -> {finish();});
    }
}
