package com.example.refrimancia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class VentanaRegistro extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_registro);

        TextView tvTituloRegistro = findViewById(R.id.tvTituloRegistro);
        TextView tvNombreUserRegistro = findViewById(R.id.tvNombreUserRegistro);
        TextView tvAviso1Registro = findViewById(R.id.tvAviso1Registro);
        TextView tvCorreoRegistro = findViewById(R.id.tvCorreoRegistro);
        TextView tvAviso2Registro = findViewById(R.id.tvAviso2Registro);
        TextView tvContrasenaRegistro = findViewById(R.id.tvContrasenaRegistro);
        TextView tvNombreRegistro = findViewById(R.id.tvNombreRegistro);
        TextView tvApellidosRegistro = findViewById(R.id.tvApellidosRegistro);
        TextView tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        EditText etNombreUserRegistro = findViewById(R.id.etNombreUserRegistro);
        EditText etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        EditText etpContrasenaRegistro = findViewById(R.id.etpConstrasenaRegistro);
        EditText etNombreRegistro = findViewById(R.id.etNombreRegistro);
        EditText etApellidosRegistro = findViewById(R.id.etApellidosRegistro);
        EditText etdFechaRegistro = findViewById(R.id.etdFechaRegistro);
        Button botonRegistro = findViewById(R.id.botonRegistro);
    }

    //Metodo para boton registrar
    public void registrar() {

    }
}
