package com.example.refrimancia.ui.pablo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.adapter.ComentarioAdapter;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ComentarioService;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.model.entity.Comentario;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.request.ComentarioRequest;
import com.example.refrimancia.model.response.RespuestaPaginada;
import com.example.refrimancia.model.response.RespuestaUnica;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Actividad de detalle de una receta.
 * Muestra toda la información de la receta (imagen, descripción, ingredientes, pasos,
 * semáforo nutricional) y permite ver y publicar comentarios mediante un popup.
 * Recibe el objeto {@link Receta} parcial vía Intent y carga el detalle completo desde la API.
 */
public class RecetaActivity extends AppCompatActivity {

    // ======================== CONSTANTES ========================

    private static final String EXTRA_RECETA = "extra_receta";

    // ======================== VARIABLES DE INSTANCIA ========================

    private Receta receta;

    private ImageView ivImagen;
    private TextView tvTitulo;
    private TextView tvDescripcion;
    private TextView tvIngredientes;
    private TextView tvPasos;
    private TextView tvAutor;
    private TextView tvTiempo;
    private TextView tvDificultad;
    private View semaforoDot;
    private TextView tvSemaforoTexto;
    private Button btnVerComentarios;

    // ======================== MÉTHODO FÁBRICA ========================

    /**
     * Crea el Intent necesario para abrir esta actividad con la receta indicada.
     * @param context Contexto desde el que se lanza
     * @param receta  Receta a mostrar
     * @return Intent listo para usar en {@code startActivity}
     */
    public static Intent crearIntent(Context context, Receta receta) {
        Intent intent = new Intent(context, RecetaActivity.class);
        intent.putExtra(EXTRA_RECETA, receta);
        return intent;
    }

    // ======================== CICLO DE VIDA ========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);
        receta = (Receta) getIntent().getSerializableExtra(EXTRA_RECETA);

        ivImagen = findViewById(R.id.iv_detalle_imagen);
        tvTitulo = findViewById(R.id.tv_detalle_titulo);
        tvDescripcion = findViewById(R.id.tv_detalle_descripcion);
        tvIngredientes = findViewById(R.id.tv_detalle_ingredientes);
        tvPasos = findViewById(R.id.tv_detalle_pasos);
        tvAutor = findViewById(R.id.tv_detalle_autor);
        tvTiempo = findViewById(R.id.tv_detalle_tiempo);
        tvDificultad = findViewById(R.id.tv_detalle_dificultad);
        semaforoDot = findViewById(R.id.tv_detalle_semaforo_dot);
        tvSemaforoTexto = findViewById(R.id.tv_detalle_semaforo_texto);
        btnVerComentarios = findViewById(R.id.btn_ver_comentarios);

        ImageButton btnVolver = findViewById(R.id.btn_volver_detalle);
        btnVolver.setOnClickListener(v -> finish());

        if (receta != null) {
            bindReceta(receta);
            btnVerComentarios.setOnClickListener(v -> mostrarPopupComentarios(this.receta));
            cargarDetalleReceta(receta.getIdReceta());
        }
    }

    // ======================== MÉTODOS DE UI ========================

    /**
     * Formatea minutos en texto legible (ej. "1 h 30 min").
     * @param minutos Tiempo total en minutos
     * @return Cadena formateada
     */
    private String formatTiempo(int minutos) {
        if (minutos <= 0) return getString(R.string.time_zero_minutes);
        int horas = minutos / 60;
        int minRestantes = minutos % 60;
        if (horas > 0) {
            if (minRestantes > 0) {
                return getString(R.string.time_hours_minutes_format, horas, minRestantes);
            } else {
                return getString(R.string.time_hours_format, horas);
            }
        }
        return getString(R.string.time_minutes_format, minutos);
    }
    /**
     * Muestra el popup inferior de comentarios de la receta.
     * Carga los comentarios existentes y permite publicar nuevos.
     * @param receta Receta cuyos comentarios se van a mostrar
     */
    private void mostrarPopupComentarios(Receta receta) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_comentarios);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        dialog.setCanceledOnTouchOutside(true);

        RecyclerView rvComentarios = dialog.findViewById(R.id.rv_comentarios);
        rvComentarios.setLayoutManager(new LinearLayoutManager(this));
        ComentarioAdapter adaptadorComentario = new ComentarioAdapter(new ArrayList<>());
        rvComentarios.setAdapter(adaptadorComentario);

        EditText etNuevoComentario = dialog.findViewById(R.id.et_nuevo_comentario);
        ImageButton btnEnviarComentario = dialog.findViewById(R.id.btn_enviar_comentario);

        ComentarioService comentarioService = 
                ClienteRetrofit.obtenerInstancia(this).create(ComentarioService.class);
        cargarComentariosReceta(comentarioService, receta.getIdReceta(), adaptadorComentario);

        if (btnEnviarComentario != null && etNuevoComentario != null) {
            btnEnviarComentario.setOnClickListener(v -> {
                String mensaje = etNuevoComentario.getText().toString().trim();
                if (mensaje.isEmpty()) {
                    Toast.makeText(this, R.string.error_comment_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviarComentario.setEnabled(false);

                ComentarioRequest comentarioRequest = new ComentarioRequest(receta.getIdReceta(), mensaje);
                comentarioService.crearComentario(comentarioRequest).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, 
                            @NonNull Response<ResponseBody> response) {
                        btnEnviarComentario.setEnabled(true);
                        if (response.isSuccessful()) {
                            etNuevoComentario.setText("");
                            cargarComentariosReceta(comentarioService, receta.getIdReceta(), 
                                    adaptadorComentario);
                            Toast.makeText(RecetaActivity.this, R.string.comment_published, 
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RecetaActivity.this, R.string.error_publish_comment, 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        btnEnviarComentario.setEnabled(true);
                        Toast.makeText(RecetaActivity.this, R.string.error_publish_comment_network, 
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    /**
     * Obtiene los comentarios de la receta desde la API y los carga en el adaptador.
     * @param comentarioService Servicio Retrofit de comentarios
     * @param recetaId          ID de la receta
     * @param adaptadorComentario Adaptador del RecyclerView de comentarios
     */
    private void cargarComentariosReceta(ComentarioService comentarioService, int recetaId,
            ComentarioAdapter adaptadorComentario) {
        Call<RespuestaPaginada<Comentario>> call = comentarioService.obtenerComentariosPorReceta(recetaId);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Comentario>> call, 
                    @NonNull Response<RespuestaPaginada<Comentario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comentario> comentarios = response.body().getData();
                    if (comentarios != null) {
                        adaptadorComentario.setComentarios(comentarios);
                    }
                } else {
                    Toast.makeText(RecetaActivity.this, R.string.error_load_comments, 
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Comentario>> call, 
                    @NonNull Throwable t) {
                Toast.makeText(RecetaActivity.this, R.string.error_load_comments_network, 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ======================== MÉTODOS DE DATOS ========================

    /**
     * Vincula los datos de la receta a las vistas.
     * @param receta Receta con los datos a mostrar
     */
    private void bindReceta(Receta receta) {
        tvTitulo.setText(receta.getTitulo() != null ? receta.getTitulo() : getString(R.string.recipe_no_title));

        setTextOrHide(tvDescripcion, receta.getDescripcion(), null);
        setTextOrHide(tvIngredientes, receta.getIngredientes(), getString(R.string.recipe_no_ingredients));
        setTextOrHide(tvPasos, receta.getPasos(), getString(R.string.recipe_no_steps));

        String dificultad = receta.getDificultad();
        tvDificultad.setText(dificultad != null && !dificultad.isEmpty()
                ? dificultad : getString(R.string.recipe_difficulty_not_specified));

        tvAutor.setText(receta.getNombreUsuario() != null
                ? getString(R.string.recipe_user_format, receta.getNombreUsuario())
                : getString(R.string.recipe_unknown_user));
        tvTiempo.setText(receta.getTiempoPreparacion() > 0
                ? formatTiempo(receta.getTiempoPreparacion())
                : getString(R.string.recipe_time_not_available));

        String valorSemaforo = receta.getSemaforo();
        int colorSemaforo = obtenerColorSemaforo(valorSemaforo);
        if (semaforoDot != null) {
            if (colorSemaforo != 0) {
                semaforoDot.setVisibility(View.VISIBLE);
                ((GradientDrawable) semaforoDot.getBackground().mutate()).setColor(colorSemaforo);
            } else {
                semaforoDot.setVisibility(View.GONE);
            }
        }
        if (tvSemaforoTexto != null) {
            tvSemaforoTexto.setText(obtenerTextoSemaforo(valorSemaforo));
        }

        if (receta.getImagenUrl() != null && !receta.getImagenUrl().isEmpty()) {
            Glide.with(this)
                    .load(receta.getImagenUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .fallback(android.R.drawable.ic_menu_gallery)
                    .into(ivImagen);
        } else {
            ivImagen.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    /**
     * Muestra {@code texto} en {@code tv} si no es nulo/vacío, o {@code fallback} si se provee;
     * si fallback es null y el texto está vacío, oculta la vista.
     */
    private void setTextOrHide(TextView tv, String texto, String fallback) {
        if (texto != null && !texto.trim().isEmpty()) {
            tv.setText(texto);
            tv.setVisibility(View.VISIBLE);
        } else if (fallback != null) {
            tv.setText(fallback);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    /**
     * Convierte el valor del semáforo nutricional a un color de recurso.
     * @param semaforo Valor del semáforo (rojo, naranja, amarillo, verde_claro, verde_oscuro)
     * @return Color resuelto o 0 si es desconocido
     */
    private int obtenerColorSemaforo(String semaforo) {
        if (semaforo == null) {
            return 0;
        }
        switch (semaforo.toLowerCase()) {
            case "rojo":
                return ContextCompat.getColor(this, android.R.color.holo_red_dark);
            case "naranja":
                return ContextCompat.getColor(this, android.R.color.holo_orange_dark);
            case "amarillo":
                return ContextCompat.getColor(this, android.R.color.holo_orange_light);
            case "verde_claro":
                return ContextCompat.getColor(this, android.R.color.holo_green_light);
            case "verde_oscuro":
                return ContextCompat.getColor(this, android.R.color.holo_green_dark);
            default:
                return 0;
        }
    }

    /**
     * Convierte el valor del semáforo nutricional a texto descriptivo.
     * @param semaforo Valor del semáforo (rojo, naranja, amarillo, verde_claro, verde_oscuro, gris)
     * @return Texto descriptivo del nivel de salud
     */
    private String obtenerTextoSemaforo(String semaforo) {
        if (semaforo == null) {
            return getString(R.string.semaforo_unknown);
        }
        switch (semaforo.toLowerCase()) {
            case "rojo":
                return getString(R.string.semaforo_red);
            case "naranja":
                return getString(R.string.semaforo_orange);
            case "amarillo":
                return getString(R.string.semaforo_yellow);
            case "verde_claro":
                return getString(R.string.semaforo_light_green);
            case "verde_oscuro":
                return getString(R.string.semaforo_dark_green);
            case "gris":
            default:
                return getString(R.string.semaforo_unknown);
        }
    }

    /**
     * Carga el detalle completo de la receta desde la API y actualiza las vistas.
     * Si falla, mantiene los datos parciales ya mostrados.
     * @param idReceta ID de la receta a cargar
     */
    private void cargarDetalleReceta(int idReceta) {
        RecetaService recetaService = ClienteRetrofit.obtenerInstancia(this).create(RecetaService.class);
        recetaService.obtenerReceta(idReceta).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaUnica<Receta>> call, 
                    @NonNull Response<RespuestaUnica<Receta>> response) {
                if (!response.isSuccessful() || response.body() == null 
                        || response.body().getData() == null) {
                    return;
                }

                receta = combinarRecetas(receta, response.body().getData());
                bindReceta(receta);
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaUnica<Receta>> call, @NonNull Throwable t) {
                // Se mantiene la receta parcial ya mostrada si falla la carga de detalle.
            }
        });
    }

    /**
     * Combina los datos de la receta base (parcial) con los del detalle (completo).
     * Los campos del detalle sobreescriben los de la base si no son nulos.
     * @param base    Receta con datos parciales ya mostrados
     * @param detalle Receta con datos completos de la API
     * @return Receta combinada
     */
    private Receta combinarRecetas(Receta base, Receta detalle) {
        if (base == null) {
            return detalle;
        }

        if (detalle.getTitulo() != null) base.setTitulo(detalle.getTitulo());
        if (detalle.getDescripcion() != null) base.setDescripcion(detalle.getDescripcion());
        if (detalle.getIngredientes() != null) base.setIngredientes(detalle.getIngredientes());
        if (detalle.getPasos() != null) base.setPasos(detalle.getPasos());
        if (detalle.getDificultad() != null) base.setDificultad(detalle.getDificultad());
        if (detalle.getNombreUsuario() != null) base.setNombreUsuario(detalle.getNombreUsuario());
        if (detalle.getImagenUrl() != null) base.setImagenUrl(detalle.getImagenUrl());
        if (detalle.getTiempoPreparacion() > 0) base.setTiempoPreparacion(detalle.getTiempoPreparacion());
        if (detalle.getSemaforo() != null) base.setSemaforo(detalle.getSemaforo());

        return base;
    }
}
