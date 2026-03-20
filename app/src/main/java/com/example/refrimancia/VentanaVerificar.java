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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class VentanaVerificar extends AppCompatActivity {
    private Retrofit retrofit;
    private ApiService apiService;
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

        //Para que la fecha se envíe bien
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        // Configurar Retrofit con tu URL de Render
        retrofit = new Retrofit.Builder()
                .baseUrl("https://refrimacia-backend.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);

        botonEnviarCode.setOnClickListener(v -> {
            String correo = etCorreoVerificar.getText().toString().trim();
            //Verificamos el correo para que no esté vacío y cumpla con el patrón habitual de correo
            if (correo.isEmpty()) {
                etCorreoVerificar.setError("Por favor, introduce un correo");
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreoVerificar.setError("Correo no válido");
                return;
            }else {
                solicitarCodigo(correo);
            }
        });
        botonCancelarVerificar.setOnClickListener(v -> {finish();});
    }

    //Metodo que manda el correo a la api para solicitar un codigo y abre la ventana de recuperacion
    public void solicitarCodigo(String correo) {

        CorreoRequest request = new CorreoRequest(correo);

        Call<CorreoRespuesta> call = apiService.solicitarCodigo(request);

        call.enqueue(new Callback<CorreoRespuesta>() {
            @Override
            public void onResponse(Call<CorreoRespuesta> call, Response<CorreoRespuesta> response) {

                if (response.isSuccessful() && response.body() != null) {

                    CorreoRespuesta res = response.body();

                    if (res.getStatus().equals("success")) {

                        Toast.makeText(VentanaVerificar.this,
                                res.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        // Abrimos la nueva ventana si es exitoso y cerramos la actual
                        Intent intent = new Intent(VentanaVerificar.this, VentanaRecuperarPassword.class);
                        intent.putExtra("correo", correo);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(VentanaVerificar.this,
                                "Correo no registrado",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VentanaVerificar.this,
                            "Error del servidor",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CorreoRespuesta> call, Throwable t) {
                Toast.makeText(VentanaVerificar.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
