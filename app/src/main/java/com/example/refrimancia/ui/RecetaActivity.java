package com.example.refrimancia.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.adaptador.AdaptadorComentario;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ComentarioService;
import com.example.refrimancia.modelo.Comentario;
import com.example.refrimancia.modelo.ComentarioRequest;
import com.example.refrimancia.modelo.Receta;
import com.example.refrimancia.modelo.RespuestaPaginada;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecetaActivity extends AppCompatActivity {
    private static final String EXTRA_RECETA = "extra_receta";
    private Receta receta;
    public static Intent crearIntent(Context context, Receta receta) {
        Intent intent = new Intent(context, RecetaActivity.class);
        intent.putExtra(EXTRA_RECETA, receta);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);
        receta = (Receta) getIntent().getSerializableExtra(EXTRA_RECETA);
        ImageView ivImagen = findViewById(R.id.iv_detalle_imagen);
        TextView tvTitulo = findViewById(R.id.tv_detalle_titulo);
        TextView tvDesc = findViewById(R.id.tv_detalle_desc);
        TextView tvPasos = findViewById(R.id.tv_detalle_pasos);
        TextView tvAutor = findViewById(R.id.tv_detalle_autor);
        TextView tvTiempo = findViewById(R.id.tv_detalle_tiempo);
        Button btnVerComentarios = findViewById(R.id.btn_ver_comentarios);
        ImageButton btnVolver = findViewById(R.id.btn_volver_detalle);
        btnVolver.setOnClickListener(v -> finish());
        if (receta != null) {
            tvTitulo.setText(receta.getTitulo() != null ? receta.getTitulo() : "Receta sin titulo");
            tvDesc.setText(receta.getDescripcion() != null ? receta.getDescripcion() : "Sin descripcion.");
            tvPasos.setText(receta.getPasos() != null ? receta.getPasos() : "Sin pasos documentados.");
            tvAutor.setText(receta.getNombreUsuario() != null ? "@" + receta.getNombreUsuario() : "@usuario_desconocido");
            tvTiempo.setText(receta.getTiempoPreparacion() > 0 ? formatTiempo(receta.getTiempoPreparacion()) : "N/A");
            if (receta.getImagenUrl() != null && !receta.getImagenUrl().isEmpty()) {
                Glide.with(this).load(receta.getImagenUrl()).into(ivImagen);
            }

            btnVerComentarios.setOnClickListener(v -> mostrarPopupComentarios(receta));
        }
    }
    private String formatTiempo(int minutos) {
        if (minutos <= 0) return "0 min";
        int horas = minutos / 60;
        int minRestantes = minutos % 60;
        if (horas > 0) {
            return minRestantes > 0 ? horas + " h " + minRestantes + " min" : horas + " h";
        }
        return minutos + " min";
    }
    private void mostrarPopupComentarios(Receta receta) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_comentarios);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        dialog.setCanceledOnTouchOutside(true);

        RecyclerView rvComentarios = dialog.findViewById(R.id.rv_comentarios);
        rvComentarios.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorComentario adaptadorComentario = new AdaptadorComentario(new ArrayList<>());
        rvComentarios.setAdapter(adaptadorComentario);

        EditText etNuevoComentario = dialog.findViewById(R.id.et_nuevo_comentario);
        ImageButton btnEnviarComentario = dialog.findViewById(R.id.btn_enviar_comentario);

        ComentarioService comentarioService = ClienteRetrofit.obtenerInstancia(this).create(ComentarioService.class);
        cargarComentariosReceta(comentarioService, receta.getIdReceta(), adaptadorComentario);

        if (btnEnviarComentario != null && etNuevoComentario != null) {
            btnEnviarComentario.setOnClickListener(v -> {
                String mensaje = etNuevoComentario.getText().toString().trim();
                if (mensaje.isEmpty()) {
                    Toast.makeText(this, R.string.recipe_comment_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviarComentario.setEnabled(false);

                ComentarioRequest comentarioRequest = new ComentarioRequest(receta.getIdReceta(), mensaje);
                comentarioService.crearComentario(comentarioRequest).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        btnEnviarComentario.setEnabled(true);
                        if (response.isSuccessful()) {
                            etNuevoComentario.setText("");
                            cargarComentariosReceta(comentarioService, receta.getIdReceta(), adaptadorComentario);
                            Toast.makeText(RecetaActivity.this, R.string.recipe_comment_sent, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RecetaActivity.this, R.string.recipe_comment_send_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        btnEnviarComentario.setEnabled(true);
                        Toast.makeText(RecetaActivity.this, R.string.recipe_comment_send_network_error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    private void cargarComentariosReceta(ComentarioService comentarioService, int recetaId, AdaptadorComentario adaptadorComentario) {
        Call<RespuestaPaginada<Comentario>> call = comentarioService.obtenerComentariosPorReceta(recetaId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Comentario>> call, @NonNull Response<RespuestaPaginada<Comentario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comentario> comentarios = response.body().getData();
                    if (comentarios != null) {
                        adaptadorComentario.setComentarios(comentarios);
                    }
                } else {
                    Toast.makeText(RecetaActivity.this, R.string.recipe_comments_load_error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Comentario>> call, @NonNull Throwable t) {
                Toast.makeText(RecetaActivity.this, R.string.recipe_comments_load_network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
