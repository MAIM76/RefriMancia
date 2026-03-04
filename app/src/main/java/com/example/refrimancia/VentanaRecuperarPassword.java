package com.example.refrimancia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class VentanaRecuperarPassword extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_recuperar_password);

        TextView tvTituloRecuperar = findViewById(R.id.tvTituloRecuperar);
        TextView tvNuevaPasswordRecuperar = findViewById(R.id.tvNuevaPasswordRecuperar);
        TextView tvRepetirPasswordRecuperar = findViewById(R.id.tvRepetirPasswordRecuperar);
        EditText etpNuevaPasswordRecuperar = findViewById(R.id.etpNuevaPasswordRecuperar);
        EditText etpRepetirPasswordRecuperar = findViewById(R.id.etpRepetirPasswordRecuperar);
        Button botonActualizarPassword = findViewById(R.id.botonActualizarPassword);
    }

    //Metodo actualizar contraseña
    public void onActualizar() {

    }
}
