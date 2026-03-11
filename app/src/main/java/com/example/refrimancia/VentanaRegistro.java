package com.example.refrimancia;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import android.widget.Toast;

import java.io.IOException;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class VentanaRegistro extends AppCompatActivity {
    TextView tvTituloRegistro;
    TextView tvNombreUserRegistro;
    TextView tvCorreoRegistro;
    TextView tvContrasenaRegistro;
    TextView tvNombreRegistro;
    TextView tvApellidosRegistro;
    TextView tvFechaRegistro;
    EditText etNombreUserRegistro;
    EditText etCorreoRegistro;
    EditText etpContrasenaRegistro;
    EditText etNombreRegistro;
    EditText etApellidosRegistro;
    EditText etFechaRegistro;
    Button botonRegistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ventana_registro);

        tvTituloRegistro = findViewById(R.id.tvTituloRegistro);
        tvNombreUserRegistro = findViewById(R.id.tvNombreUserRegistro);
        tvCorreoRegistro = findViewById(R.id.tvCorreoRegistro);
        tvContrasenaRegistro = findViewById(R.id.tvContrasenaRegistro);
        tvNombreRegistro = findViewById(R.id.tvNombreRegistro);
        tvApellidosRegistro = findViewById(R.id.tvApellidosRegistro);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        etNombreUserRegistro = findViewById(R.id.etNombreUserRegistro);
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etpContrasenaRegistro = findViewById(R.id.etpContrasenaRegistro);
        etNombreRegistro = findViewById(R.id.etNombreRegistro);
        etApellidosRegistro = findViewById(R.id.etApellidosRegistro);
        etFechaRegistro = findViewById(R.id.etFechaRegistro);
        botonRegistro = findViewById(R.id.botonRegistro);

        //Asociar el boton con el metodo
        botonRegistro.setOnClickListener(v -> registrar());
    }

    //Metodo para boton registrar
    public void registrar() {
        Toast.makeText(this, "Registrar llamado", Toast.LENGTH_SHORT).show();
        String usuario = etNombreUserRegistro.getText().toString().trim();
        String correo = etCorreoRegistro.getText().toString().trim();
        String contrasena = etpContrasenaRegistro.getText().toString().trim();
        String nombre = etNombreRegistro.getText().toString().trim();
        String apellidos = etApellidosRegistro.getText().toString().trim();
        String fecha = etFechaRegistro.getText().toString().trim();

        String nombreCompleto = nombre + " " + apellidos;
        if(usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty()){
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //Crear el json que mandaremos al servidor
            JSONObject json = new JSONObject();
            json.put("nombre_usuario", usuario);
            json.put("contrasena", contrasena);
            json.put("correo_electronico", correo);
            json.put("nombre_completo", nombreCompleto);
            json.put("fecha_nac", fecha);
            json.put("imagen_perfil", "https://mi-servidor.com/foto.png");

            //Crear cliente para el http
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json")
            );
            //Prepara el post para postman
            Request request = new Request.Builder()
                    .url("https://refrimacia-backend.onrender.com/api/usuarios/crear")
                    .post(body)
                    .build();
            //Mandar la peticion al backend
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //En caso de error de conexion, salta un aviso
                    runOnUiThread(() ->
                            Toast.makeText(VentanaRegistro.this, "Error de conexión", Toast.LENGTH_SHORT).show()
                    );

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String respuesta = response.body().string();

                    runOnUiThread(() -> {

                        try {
                            //Leer la respuesta del servidor
                            JSONObject jsonRespuesta = new JSONObject(respuesta);
                            String status = jsonRespuesta.getString("status");
                            String mensaje = jsonRespuesta.getString("message");
                            //Mostramos el mensaje de exito o error
                            Toast.makeText(VentanaRegistro.this, mensaje, Toast.LENGTH_LONG).show();
                            if ("success".equals(status)) {
                                //Cerrar esta ventana si se cumple la condicion
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
