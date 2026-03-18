package com.example.refrimancia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class VentanaRecuperarPassword extends AppCompatActivity {
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
    }

    //Metodo actualizar contraseña
    public void onActualizar() {
        String nuevaPwd = etpNuevaPasswordRecuperar.getText().toString().trim();
        String repetirPwd = etpRepetirPasswordRecuperar.getText().toString().trim();

        if(nuevaPwd == repetirPwd) {
            //PARTE DE BASE DE DATOS
        } else {
            Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
        }
    }
}
