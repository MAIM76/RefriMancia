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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragmento de perfil del usuario.
 * Muestra los datos del usuario logueado (nombre, foto) y el listado de sus recetas.
 * Soporta refresco manual por swipe y recarga automática cada {@code REFRESH_INTERVAL_MS} ms
 * al volver al fragmento. Permite editar el perfil y cerrar sesión.
 */
public class UsuarioFragment extends Fragment {

    // ======================== CONSTANTES ========================

    private static final String TAG = "UsuarioFragment";
    /** Máximo de páginas a recorrer en la paginación de recetas (guard frente a bucles). */
    private static final int MAX_PAGES = 50;
    /** Intervalo mínimo entre recargas automáticas de recetas al hacer onResume. */
    private static final long REFRESH_INTERVAL_MS = 60_000L;

    private final ActivityResultLauncher<Intent> recetaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    recargarRecetas();
                    if (getActivity() instanceof OnRecetaCambiadaListener) {
                        ((OnRecetaCambiadaListener) getActivity()).onRecetaCambiada();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> editarPerfilLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    perfilCargado = false;
                    obtenerPerfilUsuario();
                }
            }
    );

    // ======================== VARIABLES DE INSTANCIA ========================

    private ImageView imagenPerfil;
    private TextView nombreUsuario;
    private Button btnEditarPerfil;
    private Button btnCerrarSesion;
    private RecyclerView rvMisRecetas;
    private View contenidoVacio;
    private SessionManager sessionManager;
    private AdaptadorMisRecetas adaptadorMisRecetas;
    private SwipeRefreshLayout swipeRecetas;
    /** true una vez que el perfil del usuario se ha cargado correctamente. */
    private boolean perfilCargado = false;
    /** true mientras hay una recarga de recetas en curso (evita llamadas duplicadas). */
    private boolean refrescandoRecetas = false;
    /** true hasta que finaliza la primera carga, impide recargas prematuras en onResume. */
    private boolean primeraCarga = true;
    /** Marca de tiempo (elapsedRealtime) de la última recarga de recetas. */
    private long lastRefreshAt = 0L;

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

        inicializarVistas(vista);
        cargarDatosUsuario();
    }

    /**
     * Recarga las recetas del usuario si ha pasado el intervalo mínimo desde la última recarga.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (perfilCargado && adaptadorMisRecetas != null && !refrescandoRecetas) {
            long ahora = SystemClock.elapsedRealtime();
            if (ahora - lastRefreshAt >= REFRESH_INTERVAL_MS) {
                refrescarRecetas(false);
            }
        }
    }

    // ======================== INICIALIZACIÓN ========================

    /**
     * Enlaza las vistas del layout, configura el adaptador y asigna los listeners de botones.
     */
    private void inicializarVistas(View vista) {
        sessionManager = new SessionManager(requireContext());
        imagenPerfil = vista.findViewById(R.id.profile_image);
        nombreUsuario = vista.findViewById(R.id.nombre_usuario);
        btnEditarPerfil = vista.findViewById(R.id.btn_editar_perfil);
        btnCerrarSesion = vista.findViewById(R.id.btn_cerrar_sesion);
        rvMisRecetas = vista.findViewById(R.id.rv_recetas_usuario);
        contenidoVacio = vista.findViewById(R.id.empty_state);
        swipeRecetas = vista.findViewById(R.id.swipe_recetas_usuario);

        rvMisRecetas.setLayoutManager(new LinearLayoutManager(getContext()));

        btnEditarPerfil.setOnClickListener(v -> abrirEditarPerfil());
        btnCerrarSesion.setOnClickListener(v -> mostrarConfirmacionCerrarSesion());
        if (swipeRecetas != null) {
            swipeRecetas.setOnRefreshListener(() -> refrescarRecetas(true));
        }
    }

    // ======================== CARGA DE DATOS ========================

    /**
     * Punto de entrada para la carga inicial de datos.
     * Si no hay sesión válida muestra estado vacío; si ya se cargó el perfil, refresca recetas.
     */
    private void cargarDatosUsuario() {
        if (!sessionManager.isSessionValid()) {
            mostrarEstadoVacio();
            return;
        }
        if (!perfilCargado) {
            obtenerPerfilUsuario();
        } else if (!refrescandoRecetas) {
            refrescarRecetas(false);
        }
    }

    /**
     * Llama a la API para obtener el perfil completo del usuario y actualiza la UI.
     * Si falla, intenta mostrar los datos guardados en sesión local.
     */
    private void obtenerPerfilUsuario() {
        UsuarioService usuarioService = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(UsuarioService.class);
        usuarioService.obtenerPerfil().enqueue(new Callback<RespuestaUnica<Usuario>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Usuario>> call, 
                    Response<RespuestaUnica<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null 
                        && response.body().getData() != null) {
                    Usuario usuario = response.body().getData();
                    actualizarUI(usuario);
                    perfilCargado = true;
                    primeraCarga = false;
                    cargarRecetasUsuarioLogueado(usuario.getIdUsuario(), usuario.getNombreUsuario());
                } else {
                    mostrarDesdeSesion();
                }
            }

            @Override
            public void onFailure(Call<RespuestaUnica<Usuario>> call, Throwable t) {
                Log.e(TAG, getString(R.string.log_error_api_call, t.getMessage()));
                mostrarDesdeSesion();
            }
        });
    }

    /**
     * Fallback cuando la API de perfil no responde.
     * Muestra los datos básicos guardados en {@link com.example.refrimancia.util.SessionManager}.
     */
    private void mostrarDesdeSesion() {
        int idUsuario = sessionManager.fetchUserId();
        String nombre = sessionManager.fetchUserName();

        if (idUsuario > 0) {
            nombreUsuario.setText(getString(R.string.recipe_user_format, nombre));
            imagenPerfil.setImageResource(R.drawable.bg_placeholder_circular);
            contenidoVacio.setVisibility(View.GONE);
            perfilCargado = true;
            primeraCarga = false;
            cargarRecetasUsuarioLogueado(idUsuario, nombre);
        } else {
            mostrarEstadoVacio();
        }
    }

    public void recargarRecetas() {
        refrescarRecetas(true);
    }

    /**
     * Recarga las recetas del usuario.
     * @param forzar Si es {@code true}, ignora el intervalo de refresco y carga siempre.
     */
    private void refrescarRecetas(boolean forzar) {
        if (primeraCarga) {
            return;
        }
        long ahora = SystemClock.elapsedRealtime();
        if (!forzar && (ahora - lastRefreshAt) < REFRESH_INTERVAL_MS) {
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
        lastRefreshAt = ahora;
        refrescandoRecetas = true;
        obtenerPerfilUsuario();
        cargarRecetasUsuarioLogueado(idUsuario, nombre);
    }

    /**
     * Inicia la carga paginada de todas las recetas del usuario.
     * @param idUsuario     ID del usuario logueado
     * @param nombreUsuario Nombre del usuario (para filtrar por nombre cuando el ID no coincide)
     */
    private void cargarRecetasUsuarioLogueado(int idUsuario, String nombreUsuario) {
        RecetaService recetaService = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        contenidoVacio.setVisibility(View.GONE);
        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, 1, new ArrayList<>());
    }

    /**
     * Carga recursivamente página a página y filtra las recetas que pertenecen al usuario.
     * @param recetaService  Servicio Retrofit de recetas
     * @param idUsuario      ID del usuario
     * @param nombreUsuario  Nombre del usuario (normalizdo)
     * @param pagina         Página actual a cargar
     * @param acumuladas     Lista donde se acumulan las recetas del usuario
     */
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
                            mostrarRecetasUsuario(acumuladas);
                        }
                    } else if (hayDatos && pagina < MAX_PAGES) {
                        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, 
                                acumuladas);
                    } else {
                        mostrarRecetasUsuario(acumuladas);
                    }
                } else {
                    mostrarRecetasUsuario(acumuladas);
                }
                if (swipeRecetas != null && swipeRecetas.isRefreshing()) {
                    swipeRecetas.setRefreshing(false);
                }
                refrescandoRecetas = false;
            }

            @Override
            public void onFailure(Call<RespuestaPaginada<Receta>> call, Throwable t) {
                Log.e(TAG, "Error al cargar recetas API: " + t.getMessage());
                mostrarRecetasUsuario(acumuladas);
                if (swipeRecetas != null && swipeRecetas.isRefreshing()) {
                    swipeRecetas.setRefreshing(false);
                }
                refrescandoRecetas = false;
            }
        });
    }

    /**
     * Normaliza el nombre de usuario para comparaciones (quita {@code @}, trim, minúsculas).
     * @param nombre Nombre de usuario original
     * @return Nombre normalizado
     */
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

    /**
     * Muestra las recetas del usuario en el RecyclerView o el estado vacío si no hay ninguna.
     * @param misRecetas Lista de recetas filtradas del usuario
     */
    private void mostrarRecetasUsuario(List<Receta> misRecetas) {
        if (misRecetas == null || misRecetas.isEmpty()) {
            if (adaptadorMisRecetas != null) {
                adaptadorMisRecetas.actualizarDatos(new ArrayList<>());
            }
            mostrarEstadoVacio();
            return;
        }

        contenidoVacio.setVisibility(View.GONE);
        if (adaptadorMisRecetas == null) {
            adaptadorMisRecetas = new AdaptadorMisRecetas(misRecetas);
            rvMisRecetas.setAdapter(adaptadorMisRecetas);
        } else {
            adaptadorMisRecetas.actualizarDatos(misRecetas);
        }
    }

    // ======================== INTERFAZ DE COMUNICACIÓN ========================

    /**
     * Interfaz implementada por la actividad contenedora para recibir actualizaciones
     * de la foto de perfil y reflejarlas en el icono de la barra de navegación inferior.
     */
    public interface OnFotoPerfilCargadaListener {
        void onFotoPerfilCargada(String url);
    }

    /**
     * Interfaz implementada por la actividad contenedora para propagar
     * cambios de receta (crear/editar/eliminar) al fragmento de inicio.
     */
    public interface OnRecetaCambiadaListener {
        void onRecetaCambiada();
    }

    // ======================== MÉTODOS DE UI ========================

    /**
     * Actualiza las vistas con los datos del usuario: nombre, foto de perfil e icono de nav.
     * @param usuario Datos del usuario obtenidos de la API
     */
    private void actualizarUI(Usuario usuario) {
        if (usuario != null) {
            nombreUsuario.setText(getString(R.string.recipe_user_format, usuario.getNombreUsuario()));

            String urlFoto = usuario.getUrlFotoPerfil();
            sessionManager.saveUserPhoto(urlFoto);
            if (getActivity() instanceof OnFotoPerfilCargadaListener) {
                ((OnFotoPerfilCargadaListener) getActivity()).onFotoPerfilCargada(urlFoto);
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

            contenidoVacio.setVisibility(View.GONE);
        } else {
            mostrarEstadoVacio();
        }
    }

    /**
     * Muestra el estado vacío (sin datos de usuario disponibles).
     */
    private void mostrarEstadoVacio() {
        contenidoVacio.setVisibility(View.VISIBLE);
        Log.d(TAG, "No hay datos de usuario disponibles");
    }

    // ======================== ACCIONES DE USUARIO ========================

    /**
     * Abre la pantalla de edición de perfil si la sesión es válida.
     */
    private void abrirEditarPerfil() {
        if (!sessionManager.isSessionValid()) {
            Toast.makeText(requireContext(), R.string.error_session_not_available,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        lastRefreshAt = 0L;
        Intent intent = new Intent(requireContext(), VentanaEditarPerfil.class);
        editarPerfilLauncher.launch(intent);
    }

    /**
     * Muestra un diálogo de confirmación antes de cerrar sesión.
     */
    private void mostrarConfirmacionCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_confirmation_title)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.logout_confirm, (dialog, which) -> cerrarSesion())
                .setNegativeButton(R.string.logout_cancel, null)
                .show();
    }

    /**
     * Cierra la sesión del usuario y redirige al login.
     */
    private void cerrarSesion() {
        UsuarioService usuarioService = ClienteRetrofit.obtenerInstancia(requireContext())
                .create(UsuarioService.class);
        usuarioService.logout().enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<okhttp3.ResponseBody> call,
                    @NonNull retrofit2.Response<okhttp3.ResponseBody> response) {
                limpiarSesionYRedirigir();
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<okhttp3.ResponseBody> call,
                    @NonNull Throwable t) {
                limpiarSesionYRedirigir();
            }
        });
    }

    private void limpiarSesionYRedirigir() {
        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // ======================== ADAPTADOR INTERNO ========================

    /**
     * Adaptador interno para el RecyclerView de recetas del usuario.
     */
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
                    recetaLauncher.launch(RecetaActivity.crearIntent(requireContext(), r)));

            holder.tvTitulo.setText(r.getTitulo() != null ? r.getTitulo() : getString(R.string.recipe_no_title));
            holder.tvFecha.setText(r.getFechaCreacion() != null ? formatFecha(r.getFechaCreacion()) : getString(R.string.recipe_recent));
            
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
            String tiempo = receta.getTiempoPreparacion() > 0 ? formatTiempo(receta.getTiempoPreparacion()) : null;
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
            int color = obtenerColorSemaforo(semaforo);
            if (color != 0) {
                holder.semaforoDot.setVisibility(View.VISIBLE);
                holder.semaforoDot.getBackground().mutate().setColorFilter(color,
                        android.graphics.PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.semaforoDot.setVisibility(View.GONE);
            }
        }
    }

    private int obtenerColorSemaforo(String semaforo) {
        if (semaforo == null) return 0;
        switch (semaforo.toLowerCase()) {
            case "rojo":        return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            case "naranja":     return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark);
            case "amarillo":    return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light);
            case "verde_claro": return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_green_light);
            case "verde_oscuro":return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark);
            default:            return 0;
        }
    }

    private String formatFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "";
        String[] formatos = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss"
        };
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        for (String formato : formatos) {
            try {
                Date date = new SimpleDateFormat(formato, Locale.getDefault()).parse(fechaOriginal);
                if (date != null) return formatoSalida.format(date);
            } catch (ParseException ignored) {}
        }
        return fechaOriginal.length() >= 10 ? fechaOriginal.substring(0, 10) : fechaOriginal;
    }

    private String formatTiempo(int minutos) {
        if (minutos <= 0) return getString(R.string.recipe_time_not_available);
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
}
