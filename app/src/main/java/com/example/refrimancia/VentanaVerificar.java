package com.example.refrimancia;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class VentanaVerificar extends AppCompatActivity {
    TextView tvTituloVerificar;
    TextView tvCorreoVerificar;
    TextView tvSeparadorVerificar;
    TextView tvEsperarVerificar;
    TextView tvSegundosVerificar;
    TextView tvSegundosTextoVerificar;
    TextView tvCodigoVerificar;
    EditText etCorreoVerificar;
    EditText etCodigoVerificar;
    Button botonEnviarCode;
    Button botonVerificar;
    int codigoGenerado = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_verificar);

        tvTituloVerificar = findViewById(R.id.tvTituloVerificar);
        tvCorreoVerificar = findViewById(R.id.tvCorreoVerificar);
        tvSeparadorVerificar = findViewById(R.id.tvSeparadorVerificar);
        tvEsperarVerificar = findViewById(R.id.tvEsperarVerificar);
        tvSegundosVerificar = findViewById(R.id.tvSegundosVerificar);
        tvSegundosTextoVerificar = findViewById(R.id.tvSegundosTextoVerificar);
        tvCodigoVerificar = findViewById(R.id.tvCodigoVerificar);
        etCorreoVerificar = findViewById(R.id.etCorreoVerificar);
        etCodigoVerificar = findViewById(R.id.etCodigoVerificar);
        botonEnviarCode = findViewById(R.id.botonEnviarCode);
        botonVerificar = findViewById(R.id.botonVerificar);

        botonEnviarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enviar el código
                enviarCodigo();
                //Iniciar la cuenta atrás para bloquear el botón
                reducirContador(60); // por ejemplo, 60 segundos
            }
        });
        botonVerificar.setOnClickListener(v -> verificarCodigo());
    }

    //Metodo de contador para volver a enviar codigo
    public void reducirContador(int segundos) {
        botonEnviarCode.setEnabled(false); // bloquear el botón
        tvSegundosVerificar.setText(String.valueOf(segundos));
        botonEnviarCode.setText("Reenviar en " + segundos + "s"); // cambiar texto al inicio

        new CountDownTimer(segundos * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                int segundosRestantes = (int) millisUntilFinished / 1000;
                tvSegundosVerificar.setText(String.valueOf(segundosRestantes));
                botonEnviarCode.setText("Reenviar en " + segundosRestantes + "s"); // actualizar el texto cada segundo
            }

            public void onFinish() {
                tvSegundosVerificar.setText("0");
                botonEnviarCode.setEnabled(true); // habilitar el botón otra vez
                botonEnviarCode.setText("Enviar código"); // volver al texto original
            }
        }.start();
    }
    //Metodo para generar codigo de verificacion
    public int generarCodigo() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
    //Metodo que envia el codigo
    public void enviarCodigo() {
        String correo = etCorreoVerificar.getText().toString().trim();

        if(correo.isEmpty()){
            Toast.makeText(this,"Introduce un correo",Toast.LENGTH_SHORT).show();
            return;
        }

        codigoGenerado = generarCodigo();

        enviarCorreo(correo, codigoGenerado);
    }
    //Metodo para crear y enviar el correo con el codigo
    public void enviarCorreo(String correo, int codigo) {
        OkHttpClient client = new OkHttpClient();
        //A FALTA DE CAMBIAR
        String json = "{"
                + "\"service_id\":\"service_xxxxx\","
                + "\"template_id\":\"template_xxxxx\","
                + "\"user_id\":\"TU_PUBLIC_KEY\","
                + "\"template_params\":{"
                + "\"email\":\"" + correo + "\","
                + "\"codigo\":\"" + codigo + "\""
                + "}"
                + "}";

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.emailjs.com/api/v1.0/email/send")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Error enviando correo",
                                Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(),
                                "Correo enviado",
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    //Metodo para verificar
    public void verificarCodigo() {
        if(codigoGenerado == 0){
            Toast.makeText(this,"Primero solicita un código",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int codigoUsuario = Integer.parseInt(etCodigoVerificar.getText().toString());
            if(codigoUsuario == codigoGenerado){
                Toast.makeText(this,"Código correcto",Toast.LENGTH_SHORT).show();
                //AQUI VA EL ABRIR LA OTRA VENTANA
            }
            else{
                Toast.makeText(this,"Código incorrecto",Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e){

            Toast.makeText(this,"El código debe ser numérico",Toast.LENGTH_SHORT).show();
        }
    }
}
