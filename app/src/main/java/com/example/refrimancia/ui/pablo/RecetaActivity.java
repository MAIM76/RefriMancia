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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.ui.CreateRecipeActivity;
import com.example.refrimancia.util.ColorUtils;
import com.example.refrimancia.util.ComentariosPopupHelper;
import com.example.refrimancia.util.DateUtils;
import com.example.refrimancia.util.SessionManager;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.response.RespuestaUnica;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecetaActivity extends AppCompatActivity {

    // ======================== CONSTANTES ========================

    private static final String EXTRA_RECETA = "extra_receta";

    // ======================== VARIABLES DE INSTANCIA ========================

    private Receta receta;
    private RecetaService recetaService;

    private final ActivityResultLauncher<Intent> editarRecetaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    setResult(RESULT_OK);
                    cargarReceta(receta.getIdReceta());
                }
            }
    );

    // ======================== VISTAS ========================

    private ImageView ivImagen;
    private TextView tvTitulo;
    private TextView tvDescripcion;
    private TextView tvIngredientes;
    private TextView tvAutor;
    private TextView tvTiempo;
    private TextView tvDificultad;
    private View semaforoDot;
    private TextView tvSemaforoTexto;
    private Button btnVerComentarios;
    private View llBotones;
    private Button btnModificarReceta;
    private Button btnEliminarReceta;

    private Button btnTabTotal;
    private Button btnTabPer100g;
    private boolean tabTotalSeleccionado = true;

    private TextView tvKcal;
    private TextView tvProteinas;
    private TextView tvCarbos;
    private TextView tvGrasas;
    private TextView tvGrasasSat;
    private TextView tvAzucares;
    private TextView tvFibra;
    private TextView tvSal;
    private View barProteinas;
    private View barCarbohidratos;
    private View barGrasas;
    private View barGrasasSaturadas;
    private View barAzucares;
    private View barFibra;
    private View barSal;


    // ======================== MÉTHODO FÁBRICA ========================

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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            receta = getIntent().getSerializableExtra(EXTRA_RECETA, Receta.class);
        } else {
            receta = (Receta) getIntent().getSerializableExtra(EXTRA_RECETA);
        }

        ivImagen = findViewById(R.id.iv_detalle_imagen);
        tvTitulo = findViewById(R.id.tv_detalle_titulo);
        tvDescripcion = findViewById(R.id.tv_detalle_descripcion);
        tvIngredientes = findViewById(R.id.tv_detalle_ingredientes);
        tvAutor = findViewById(R.id.tv_detalle_autor);
        tvTiempo = findViewById(R.id.tv_detalle_tiempo);
        tvDificultad = findViewById(R.id.tv_detalle_dificultad);
        semaforoDot = findViewById(R.id.tv_detalle_semaforo_dot);
        tvSemaforoTexto = findViewById(R.id.tv_detalle_semaforo_texto);
        btnVerComentarios = findViewById(R.id.btn_ver_comentarios);
        llBotones = findViewById(R.id.ll_botones_propietario);
        btnModificarReceta = findViewById(R.id.btn_modificar_receta);
        btnEliminarReceta = findViewById(R.id.btn_eliminar_receta);
        btnTabTotal = findViewById(R.id.btn_tab_total);
        btnTabPer100g = findViewById(R.id.btn_tab_per100g);

        tvKcal = findViewById(R.id.tv_nutricion_kcal);
        tvProteinas = findViewById(R.id.tv_nutricion_proteinas);
        tvCarbos = findViewById(R.id.tv_nutricion_carbohidratos);
        tvGrasas = findViewById(R.id.tv_nutricion_grasas);
        tvGrasasSat = findViewById(R.id.tv_nutricion_grasas_saturadas);
        tvAzucares = findViewById(R.id.tv_nutricion_azucares);
        tvFibra = findViewById(R.id.tv_nutricion_fibra);
        tvSal = findViewById(R.id.tv_nutricion_sal);
        barProteinas = findViewById(R.id.bar_proteinas);
        barCarbohidratos = findViewById(R.id.bar_carbohidratos);
        barGrasas = findViewById(R.id.bar_grasas);
        barGrasasSaturadas = findViewById(R.id.bar_grasas_saturadas);
        barAzucares = findViewById(R.id.bar_azucares);
        barFibra = findViewById(R.id.bar_fibra);
        barSal = findViewById(R.id.bar_sal);


        ImageButton btnVolver = findViewById(R.id.btn_volver_detalle);
        btnVolver.setOnClickListener(v -> finish());

        setupNutricionTabs();

        if (receta != null) {
            bindReceta(receta);
            btnVerComentarios.setOnClickListener(v -> mostrarPopupComentarios(this.receta));
            configBotones();
            cargarReceta(receta.getIdReceta());
        }
    }

    // ======================== MÉTODOS DE UI ========================

    private void mostrarPopupComentarios(Receta receta) {
        ComentariosPopupHelper.mostrar(this, receta);
    }

    // ======================== MÉTODOS DE DATOS ========================

    private void bindReceta(Receta receta) {
        tvTitulo.setText(receta.getTitulo() != null ? receta.getTitulo() : getString(R.string.recipe_no_title));

        setTextOrHide(tvIngredientes, receta.getIngredientes(), getString(R.string.recipe_no_ingredients));
        setTextOrHide(tvDescripcion, receta.getDescripcion(), null);

        String dificultad = receta.getDificultad();
        tvDificultad.setText(dificultad != null && !dificultad.isEmpty()
                ? dificultad : getString(R.string.recipe_difficulty_not_specified));

        tvAutor.setText(receta.getNombreUsuario() != null
                ? getString(R.string.recipe_user_format, receta.getNombreUsuario())
                : getString(R.string.recipe_unknown_user));
        tvTiempo.setText(receta.getTiempoPreparacion() > 0
                ? DateUtils.formatTiempo(this, receta.getTiempoPreparacion())
                : getString(R.string.recipe_time_not_available));

        String valorSemaforo = receta.getSemaforo();
        int colorSemaforo = ColorUtils.colorSemaforo(this, valorSemaforo);
        if (semaforoDot != null) {
            if (colorSemaforo != 0) {
                semaforoDot.setVisibility(View.VISIBLE);
                ((GradientDrawable) semaforoDot.getBackground().mutate()).setColor(colorSemaforo);
            } else {
                semaforoDot.setVisibility(View.GONE);
            }
        }
        if (tvSemaforoTexto != null) {
            tvSemaforoTexto.setText(ColorUtils.textoSemaforo(this, valorSemaforo));
        }

        bindNutricion(receta);

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

    private void setupNutricionTabs() {
        if (btnTabTotal == null || btnTabPer100g == null) return;
        btnTabTotal.setOnClickListener(v -> seleccionarTab(true));
        btnTabPer100g.setOnClickListener(v -> seleccionarTab(false));
        seleccionarTab(true);
    }

    private void seleccionarTab(boolean totalSeleccionado) {
        tabTotalSeleccionado = totalSeleccionado;
        if (receta != null) bindNutricion(totalSeleccionado);

        int colorActivo = android.graphics.Color.parseColor("#D9CDB8");
        int colorInactivo = android.graphics.Color.TRANSPARENT;
        int textoActivo = androidx.core.content.ContextCompat.getColor(this, R.color.azul_oscuro);
        int textoInactivo = android.graphics.Color.parseColor("#9E9E9E");

        btnTabTotal.setBackgroundTintList(android.content.res.ColorStateList
                .valueOf(totalSeleccionado ? colorActivo : colorInactivo));
        btnTabTotal.setTextColor(totalSeleccionado ? textoActivo : textoInactivo);
        btnTabTotal.setTypeface(null, totalSeleccionado
                ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        btnTabPer100g.setBackgroundTintList(android.content.res.ColorStateList
                .valueOf(totalSeleccionado ? colorInactivo : colorActivo));
        btnTabPer100g.setTextColor(totalSeleccionado ? textoInactivo : textoActivo);
        btnTabPer100g.setTypeface(null, totalSeleccionado
                ? android.graphics.Typeface.NORMAL : android.graphics.Typeface.BOLD);
    }

    private void bindNutricion(Receta receta) {
        if (tvKcal == null) return;
        if (btnTabTotal != null && receta.getPesoTotalG() > 0) {
            btnTabTotal.setText(getString(R.string.recipe_tab_total_peso,
                    String.valueOf((int) receta.getPesoTotalG())));
        }
        bindNutricion(tabTotalSeleccionado);
    }

    private void bindNutricion(boolean total) {
        if (tvKcal == null || receta == null) return;

        float kcal       = total ? receta.getKcal()          : receta.getKcal100g();
        float proteinas  = total ? receta.getProteinas()     : receta.getProteinas100g();
        float carbos     = total ? receta.getCarbohidratos()  : receta.getCarbohidratos100g();
        float azucares   = total ? receta.getAzucares()       : receta.getAzucares100g();
        float grasas     = total ? receta.getGrasas()         : receta.getGrasas100g();
        float grasSat    = total ? receta.getGrasasSaturadas(): receta.getGrasasSaturadas100g();
        float fibra      = total ? receta.getFibra()          : receta.getFibra100g();
        float sal        = total ? receta.getSal()            : receta.getSal100g();

        tvKcal.setText(getString(R.string.recipe_kcal_format, kcal));
        tvProteinas.setText(getString(R.string.recipe_nutrient_format, proteinas));
        tvCarbos.setText(getString(R.string.recipe_nutrient_format, carbos));
        tvGrasas.setText(getString(R.string.recipe_nutrient_format, grasas));
        tvFibra.setText(getString(R.string.recipe_nutrient_format, fibra));
        tvSal.setText(getString(R.string.recipe_nutrient_format, sal));

        if (tvAzucares != null) {
            if (azucares > 0) {
                tvAzucares.setText(getString(R.string.recipe_nutrient_subvalue_label,
                        getString(R.string.recipe_sugars_label), azucares));
                tvAzucares.setVisibility(View.VISIBLE);
            } else {
                tvAzucares.setVisibility(View.GONE);
            }
        }
        if (tvGrasasSat != null) {
            if (grasSat > 0) {
                tvGrasasSat.setText(getString(R.string.recipe_nutrient_subvalue_label_f,
                        getString(R.string.recipe_sat_fat_label), grasSat));
                tvGrasasSat.setVisibility(View.VISIBLE);
            } else {
                tvGrasasSat.setVisibility(View.GONE);
            }
        }

        float max = Math.max(1f, Math.max(
                Math.max(proteinas, carbos),
                Math.max(grasas, fibra)));

        ajustarBarra(barProteinas, proteinas, max);
        ajustarBarra(barCarbohidratos, carbos, max);
        ajustarBarra(barAzucares, azucares, max);
        ajustarBarra(barGrasas, grasas, max);
        ajustarBarra(barGrasasSaturadas, grasSat, max);
        ajustarBarra(barFibra, fibra, max);
        ajustarBarra(barSal, sal, max);
    }

    private void ajustarBarra(View bar, float valor, float max) {
        if (bar == null) return;
        Runnable aplicar = () -> {
            int totalAncho = ((android.view.View) bar.getParent()).getWidth();
            if (totalAncho > 0) {
                int ancho = (int) (totalAncho * (valor / max));
                android.view.ViewGroup.LayoutParams lp = bar.getLayoutParams();
                lp.width = ancho;
                bar.setLayoutParams(lp);
            }
        };
        if (((android.view.View) bar.getParent()).getWidth() > 0) {
            bar.post(aplicar);
        } else {
            bar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    bar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    aplicar.run();
                }
            });
        }
    }

    // ======================== UTILIDADES ========================

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

    // ======================== BOTONES DE PROPIETARIO ========================

    private void configBotones() {
        int idUsuarioSesion = new SessionManager(this).fetchUserId();
        if (receta != null && receta.getIdUsuario() == idUsuarioSesion) {
            llBotones.setVisibility(View.VISIBLE);
            btnModificarReceta.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateRecipeActivity.class);
                intent.putExtra("receta_editar", receta);
                editarRecetaLauncher.launch(intent);
            });
            btnEliminarReceta.setOnClickListener(v -> {
                String titulo = receta.getTitulo() != null ? receta.getTitulo() : "";
                new AlertDialog.Builder(this)
                        .setTitle(R.string.recipe_delete_title)
                        .setMessage(getString(R.string.recipe_delete_message, titulo))
                        .setPositiveButton(R.string.recipe_delete_confirm, (dialog, which) -> eliminarReceta())
                        .setNegativeButton(R.string.recipe_delete_cancel, null)
                        .show();
            });
        } else {
            llBotones.setVisibility(View.GONE);
        }
    }

    // ======================== OPERACIONES DE API ========================

    private void eliminarReceta() {
        RecetaService recetaService = ClienteRetrofit.obtenerInstancia(this).create(RecetaService.class);
        recetaService.eliminarReceta(receta.getIdReceta()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                    @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RecetaActivity.this, R.string.recipe_delete_success, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(RecetaActivity.this, R.string.recipe_delete_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(RecetaActivity.this, R.string.recipe_delete_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarReceta(int idReceta) {
        RecetaService recetaService = ClienteRetrofit.obtenerInstancia(this).create(RecetaService.class);
        recetaService.obtenerReceta(idReceta).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaUnica<Receta>> call, 
                    @NonNull Response<RespuestaUnica<Receta>> response) {
                if (!response.isSuccessful() || response.body() == null 
                        || response.body().getData() == null) {
                    return;
                }

                receta = combinar(receta, response.body().getData());
                bindReceta(receta);
                configBotones();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaUnica<Receta>> call, @NonNull Throwable t) {
                // Se mantiene la receta parcial ya mostrada si falla la carga de detalle.
            }
        });
    }

    private Receta combinar(Receta base, Receta detalle) {
        if (base == null) {
            return detalle;
        }

        if (detalle.getTitulo() != null) base.setTitulo(detalle.getTitulo());
        if (detalle.getIngredientes() != null) base.setIngredientes(detalle.getIngredientes());
        if (detalle.getDescripcion() != null) base.setDescripcion(detalle.getDescripcion());
        if (detalle.getDificultad() != null) base.setDificultad(detalle.getDificultad());
        if (detalle.getNombreUsuario() != null) base.setNombreUsuario(detalle.getNombreUsuario());
        if (detalle.getImagenUrl() != null) base.setImagenUrl(detalle.getImagenUrl());
        if (detalle.getTiempoPreparacion() > 0) base.setTiempoPreparacion(detalle.getTiempoPreparacion());
        if (detalle.getSemaforo() != null) base.setSemaforo(detalle.getSemaforo());
        if (detalle.getIdUsuario() > 0) base.setIdUsuario(detalle.getIdUsuario());
        if (detalle.getKcal() != 0) base.setKcal(detalle.getKcal());
        if (detalle.getProteinas() != 0) base.setProteinas(detalle.getProteinas());
        if (detalle.getCarbohidratos() != 0) base.setCarbohidratos(detalle.getCarbohidratos());
        if (detalle.getGrasas() != 0) base.setGrasas(detalle.getGrasas());
        if (detalle.getFibra() != 0) base.setFibra(detalle.getFibra());
        if (detalle.getPesoTotalG() != 0) base.setPesoTotalG(detalle.getPesoTotalG());
        if (detalle.getAzucares() != 0) base.setAzucares(detalle.getAzucares());
        if (detalle.getGrasasSaturadas() != 0) base.setGrasasSaturadas(detalle.getGrasasSaturadas());
        if (detalle.getSal() != 0) base.setSal(detalle.getSal());
        if (detalle.getKcal100g() != 0) base.setKcal100g(detalle.getKcal100g());
        if (detalle.getProteinas100g() != 0) base.setProteinas100g(detalle.getProteinas100g());
        if (detalle.getCarbohidratos100g() != 0) base.setCarbohidratos100g(detalle.getCarbohidratos100g());
        if (detalle.getAzucares100g() != 0) base.setAzucares100g(detalle.getAzucares100g());
        if (detalle.getGrasas100g() != 0) base.setGrasas100g(detalle.getGrasas100g());
        if (detalle.getGrasasSaturadas100g() != 0) base.setGrasasSaturadas100g(detalle.getGrasasSaturadas100g());
        if (detalle.getFibra100g() != 0) base.setFibra100g(detalle.getFibra100g());
        if (detalle.getSal100g() != 0) base.setSal100g(detalle.getSal100g());

        return base;
    }
}
