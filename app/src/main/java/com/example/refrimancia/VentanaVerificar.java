package com.example.refrimancia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VentanaVerificar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_verificar);

        TextView tvTituloVerificar = findViewById(R.id.tvTituloVerificar);
        TextView tvCorreoVerificar = findViewById(R.id.tvCorreoVerificar);
        TextView tvSeparadorVerificar = findViewById(R.id.tvSeparadorVerificar);
        TextView tvEsperarVerificar = findViewById(R.id.tvEsperarVerificar);
        TextView tvSegundosVerificar = findViewById(R.id.tvSegundosVerificar);
        TextView tvSegundosTextoVerificar = findViewById(R.id.tvSegundosTextoVerificar);
        TextView tvCodigoVerificar = findViewById(R.id.tvCodigoVerificar);
        EditText etCorreoVerificar = findViewById(R.id.etCorreoVerificar);
        EditText etCodigoVerificar = findViewById(R.id.etCodigoVerificar);
        Button botonEnviarCode = findViewById(R.id.botonEnviarCode);
        Button botonVerificar = findViewById(R.id.botonVerificar);

        botonEnviarCode.setOnClickListener(v -> enviarCodigo());
        botonVerificar.setOnClickListener(v -> verificarCodigo());
    }

    //Metodo para enviar codigo de validacion
    public void enviarCodigo() {

    }

    //Metodo para verificar
    public void verificarCodigo() {

    }
}
