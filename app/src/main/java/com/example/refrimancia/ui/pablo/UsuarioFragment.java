package com.example.refrimancia.ui.pablo;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.util.ColorUtils;
import com.example.refrimancia.util.DateUtils;
import com.example.refrimancia.util.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.entity.Usuario;
import com.example.refrimancia.model.response.RespuestaPaginada;
import com.example.refrimancia.model.response.RespuestaUnica;
import com.example.refrimancia.ui.LoginActivity;
import com.example.refrimancia.ui.VentanaEditarPerfil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioFragment extends Fragment {

    // ======================== CONSTANTES ========================

    private static final String TAG = "UsuarioFragment";
    private static final int MAX_PAGES = 50;
    private static final long REFRESH_INTERVAL_MS = 60_000L;

    // ======================== VARIABLES DE INSTANCIA ========================

    private final ActivityResultLauncher<Intent> recetaResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    recargar();
                    if (getActivity() instanceof OnRecetaListener) {
                        ((OnRecetaListener) getActivity()).onRecetaCambiada();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> perfilResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    perfilCargado = false;
                    cargarPerfil();
                }
            }
    );

    private ImageView imagenPerfil;
    private TextView nombreUsuario;
    private Button btnEditarPerfil;
    private Button btnCerrarSesion;
    private RecyclerView rvRecetas;
    private View emptyView;
    private SessionManager sessionManager;
    private AdaptadorMisRecetas adaptador;
    private SwipeRefreshLayout swipeRecetas;
    private boolean perfilCargado = false;
    private boolean cargando = false;
    private boolean primera = true;
    private long ultimaRecarga = 0L;

    // ======================== CICLO DE VIDA ========================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle estadoGuardado) {
        return inflater.inflate(R.layout.usuario_fragment, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);

        initVistas(vista);
        cargarDatos();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (perfilCargado && adaptador != null && !cargando) {
            long ahora = SystemClock.elapsedRealtime();
            if (ahora - ultimaRecarga >= REFRESH_INTERVAL_MS) {
                refrescar(false);
            }
        }
    }

    // ======================== INICIALIZACIÓN ========================

    private void initVistas(View vista) {
        sessionManager = new SessionManager(requireContext());
        imagenPerfil = vista.findViewById(R.id.profile_image);
        nombreUsuario = vista.findViewById(R.id.nombre_usuario);
        btnEditarPerfil = vista.findViewById(R.id.btn_editar_perfil);
        btnCerrarSesion = vista.findViewById(R.id.btn_cerrar_sesion);
        rvRecetas = vista.findViewById(R.id.rv_recetas_usuario);
        emptyView = vista.findViewById(R.id.empty_state);
        swipeRecetas = vista.findViewById(R.id.swipe_recetas_usuario);

        rvRecetas.setLayoutManager(new LinearLayoutManager(getContext()));

        btnEditarPerfil.setOnClickListener(v -> editarPerfil());
        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
        if (swipeRecetas != null) {
            swipeRecetas.setOnRefreshListener(() -> refrescar(true));
        }
    }

    // ======================== CARGA DE DATOS ========================

    private void cargarDatos() {
        if (!sessionManager.isSessionValid()) {
            mostrarEstadoVacio();
            return;
        }
        if (!perfilCargado) {
            cargarPerfil();
        } else if (!cargando) {
            refrescar(false);
        }
    }

    private void cargarPerfil() {
        UsuarioService usuarioService = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(UsuarioService.class);
        usuarioService.obtenerPerfil().enqueue(new Callback<RespuestaUnica<Usuario>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Usuario>> call, 
                    Response<RespuestaUnica<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null 
                        && response.body().getData() != null) {
                    Usuario usuario = response.body().getData();
                    mostrarPerfil(usuario);
                    perfilCargado = true;
                    primera = false;
                    cargarRecetas(usuario.getIdUsuario(), usuario.getNombreUsuario());
                } else {
                    mostrarSesion();
                }
            }

            @Override
            public void onFailure(Call<RespuestaUnica<Usuario>> call, Throwable t) {
                Log.e(TAG, getString(R.string.log_error_api_call, t.getMessage()));
                mostrarSesion();
            }
        });
    }

    private void mostrarSesion() {
        int idUsuario = sessionManager.fetchUserId();
        String nombre = sessionManager.fetchUserName();

        if (idUsuario > 0) {
            nombreUsuario.setText(getString(R.string.recipe_user_format, nombre));
            imagenPerfil.setImageResource(R.drawable.bg_placeholder_circular);
            emptyView.setVisibility(View.GONE);
            perfilCargado = true;
            primera = false;
            cargarRecetas(idUsuario, nombre);
        } else {
            mostrarEstadoVacio();
        }
    }

    public void recargar() {
        refrescar(true);
    }

    private void refrescar(boolean forzar) {
        if (primera) {
            return;
        }
        long ahora = SystemClock.elapsedRealtime();
        if (!forzar && (ahora - ultimaRecarga) < REFRESH_INTERVAL_MS) {
            return;
        }
        int idUsuario = sessionManager.fetchUserId();
        String nombre = sessionManager.fetchUserName();
        if (idUsuario <= 0) {
            if (swipeRecetas != null) {
                swipeRecetas.setRefreshing(false);
            }
            return;
        }
        ultimaRecarga = ahora;
        cargando = true;
        cargarPerfil();
        cargarRecetas(idUsuario, nombre);
    }

    private void cargarRecetas(int idUsuario, String nombreUsuario) {
        RecetaService recetaService = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        emptyView.setVisibility(View.GONE);
        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, 1, new ArrayList<>());
    }

    // Carga página a página acumulando las recetas del usuario (filtra por ID o nombre)
    private void cargarPaginaRecetas(RecetaService recetaService, int idUsuario, String nombreUsuario,
            int pagina, List<Receta> acumuladas) {
        recetaService.obtenerRecetas(pagina).enqueue(new Callback<RespuestaPaginada<Receta>>() {
            @Override
            public void onResponse(Call<RespuestaPaginada<Receta>> call, 
                    Response<RespuestaPaginada<Receta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RespuestaPaginada<Receta> cuerpo = response.body();
                    List<Receta> todasRecetas = cuerpo.getData();
                    String nombreNormalizado = normalizarUsuario(nombreUsuario);
                    if (todasRecetas != null) {
                        for (Receta r : todasRecetas) {
                            boolean coincideId = r.getIdUsuario() == idUsuario;
                            boolean coincideNombre = 
                                    normalizarUsuario(r.getNombreUsuario()).equals(nombreNormalizado);
                            if (coincideId || coincideNombre) {
                                acumuladas.add(r);
                            }
                        }
                    }

                    Integer totalPaginas = cuerpo.getTotalPages();
                    boolean hayDatos = todasRecetas != null && !todasRecetas.isEmpty();
                    if (totalPaginas != null) {
                        if (pagina < totalPaginas) {
                            cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, 
                                    acumuladas);
                        } else {
                            mostrarRecetas(acumuladas);
                        }
                    } else if (hayDatos && pagina < MAX_PAGES) {
                        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, 
                                acumuladas);
                    } else {
                        mostrarRecetas(acumuladas);
                    }
                } else {
                    mostrarRecetas(acumuladas);
                }
                if (swipeRecetas != null && swipeRecetas.isRefreshing()) {
                    swipeRecetas.setRefreshing(false);
                }
                cargando = false;
            }

            @Override
            public void onFailure(Call<RespuestaPaginada<Receta>> call, Throwable t) {
                Log.e(TAG, "Error al cargar recetas API: " + t.getMessage());
                mostrarRecetas(acumuladas);
                if (swipeRecetas != null && swipeRecetas.isRefreshing()) {
                    swipeRecetas.setRefreshing(false);
                }
                cargando = false;
            }
        });
    }

    private String normalizarUsuario(String nombre) {
        if (nombre == null) {
            return "";
        }
        String limpio = nombre.trim();
        if (limpio.startsWith("@")) {
            limpio = limpio.substring(1);
        }
        return limpio.toLowerCase();
    }

    private void mostrarRecetas(List<Receta> recetas) {
        if (recetas == null || recetas.isEmpty()) {
            if (adaptador != null) {
                adaptador.actualizarDatos(new ArrayList<>());
            }
            mostrarEstadoVacio();
            return;
        }

        emptyView.setVisibility(View.GONE);
        if (adaptador == null) {
            adaptador = new AdaptadorMisRecetas(recetas);
            rvRecetas.setAdapter(adaptador);
        } else {
            adaptador.actualizarDatos(recetas);
        }
    }

    // ======================== INTERFAZ DE COMUNICACIÓN ========================

    public interface OnFotoListener {
        void onFotoPerfilCargada(String url);
    }

    public interface OnRecetaListener {
        void onRecetaCambiada();
    }

    // ======================== MÉTODOS DE UI ========================

    private void mostrarPerfil(Usuario usuario) {
        if (usuario != null) {
            nombreUsuario.setText(getString(R.string.recipe_user_format, usuario.getNombreUsuario()));

            String urlFoto = usuario.getUrlFotoPerfil();
            sessionManager.saveUserPhoto(urlFoto);
            if (getActivity() instanceof OnFotoListener) {
                ((OnFotoListener) getActivity()).onFotoPerfilCargada(urlFoto);
            }

            if (urlFoto != null && !urlFoto.isEmpty()) {
                Glide.with(this)
                        .load(urlFoto)
                        .placeholder(R.drawable.bg_placeholder_circular)
                        .error(R.drawable.bg_placeholder_circular)
                        .fallback(R.drawable.bg_placeholder_circular)
                        .circleCrop()
                        .into(imagenPerfil);
            } else {
                imagenPerfil.setImageResource(R.drawable.bg_placeholder_circular);
            }

            emptyView.setVisibility(View.GONE);
        } else {
            mostrarEstadoVacio();
        }
    }

    private void mostrarEstadoVacio() {
        emptyView.setVisibility(View.VISIBLE);
        Log.d(TAG, "No hay datos de usuario disponibles");
    }

    // ======================== ACCIONES DE USUARIO ========================

    private void editarPerfil() {
        if (!sessionManager.isSessionValid()) {
            Toast.makeText(requireContext(), R.string.error_session_not_available,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ultimaRecarga = 0L;
        Intent intent = new Intent(requireContext(), VentanaEditarPerfil.class);
        perfilResult.launch(intent);
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_confirmation_title)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.logout_confirm, (dialog, which) -> cerrarSesion())
                .setNegativeButton(R.string.logout_cancel, null)
                .show();
    }

    private void cerrarSesion() {
        UsuarioService usuarioService = ClienteRetrofit.obtenerInstancia(requireContext())
                .create(UsuarioService.class);
        usuarioService.logout().enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<okhttp3.ResponseBody> call,
                    @NonNull retrofit2.Response<okhttp3.ResponseBody> response) {
                sessionManager.clearSession();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<okhttp3.ResponseBody> call,
                    @NonNull Throwable t) {
                sessionManager.clearSession();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    // ======================== ADAPTADOR INTERNO ========================

    private class AdaptadorMisRecetas extends RecyclerView.Adapter<AdaptadorMisRecetas.MiRecetaViewHolder> {
        final List<Receta> recetas;

        public AdaptadorMisRecetas(List<Receta> recetas) {
            this.recetas = new ArrayList<>(recetas);
        }

        public void actualizarDatos(List<Receta> nuevasRecetas) {
            this.recetas.clear();
            if (nuevasRecetas != null) {
                this.recetas.addAll(nuevasRecetas);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MiRecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View vista = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.elemento_receta_usuario, parent, false);
            return new MiRecetaViewHolder(vista);
        }

        @Override
        public void onBindViewHolder(@NonNull MiRecetaViewHolder holder, int position) {
            Receta r = recetas.get(position);

            holder.itemView.setOnClickListener(v ->
                    recetaResult.launch(RecetaActivity.crearIntent(requireContext(), r)));

            holder.tvTitulo.setText(r.getTitulo() != null ? r.getTitulo() : getString(R.string.recipe_no_title));
            holder.tvFecha.setText(r.getFechaCreacion() != null ? DateUtils.formatFecha(r.getFechaCreacion()) : getString(R.string.recipe_recent));
            
            // Configurar el círculo del semáforo
            configurarSemaforo(holder, r);
            
            String meta = construirMetaReceta(r);
            if (meta.isEmpty()) {
                holder.tvMeta.setVisibility(View.GONE);
            } else {
                holder.tvMeta.setVisibility(View.VISIBLE);
                holder.tvMeta.setText(meta);
            }
            if (r.getImagenUrl() != null && !r.getImagenUrl().isEmpty()) {
                Glide.with(holder.itemView)
                        .load(r.getImagenUrl())
                        .placeholder(R.drawable.bg_placeholder_circular)
                        .error(R.drawable.bg_placeholder_circular)
                        .fallback(R.drawable.bg_placeholder_circular)
                        .centerCrop()
                        .into(holder.ivImagen);
            } else {
                holder.ivImagen.setImageResource(R.drawable.bg_placeholder_circular);
            }

        }

        private String construirMetaReceta(Receta receta) {
            String rawCategoria = receta.getCategoria();
            boolean categoriaVacia = rawCategoria == null || rawCategoria.trim().isEmpty()
                    || rawCategoria.equalsIgnoreCase("No disponible");
            String categoria = categoriaVacia ? null : rawCategoria;
            String tiempo = receta.getTiempoPreparacion() > 0 ? DateUtils.formatTiempo(requireContext(), receta.getTiempoPreparacion()) : null;
            if (categoria != null && tiempo != null) return categoria + " | " + tiempo;
            if (categoria != null) return categoria;
            if (tiempo != null) return tiempo;
            return "";
        }

        @Override
        public int getItemCount() {
            return recetas.size();
        }

        class MiRecetaViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImagen;
            TextView tvTitulo;
            TextView tvFecha;
            TextView tvMeta;
            View semaforoDot;

            MiRecetaViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImagen = itemView.findViewById(R.id.iv_receta_usuario);
                tvTitulo = itemView.findViewById(R.id.tv_titulo_receta_usuario);
                tvFecha = itemView.findViewById(R.id.tv_fecha_receta_usuario);
                tvMeta = itemView.findViewById(R.id.tv_meta_receta_usuario);
                semaforoDot = itemView.findViewById(R.id.tv_semaforo_usuario_dot);
            }
        }

        private void configurarSemaforo(MiRecetaViewHolder holder, Receta receta) {
            if (holder.semaforoDot == null) return;
            String semaforo = receta.getSemaforo();
            int color = ColorUtils.colorSemaforo(requireContext(), semaforo);
            if (color != 0) {
                holder.semaforoDot.setVisibility(View.VISIBLE);
                holder.semaforoDot.getBackground().mutate().setColorFilter(color,
                        android.graphics.PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.semaforoDot.setVisibility(View.GONE);
            }
        }
    }
}
