package com.example.refrimancia.ui;

import android.content.Intent;
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
import com.example.refrimancia.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.modelo.entidad.Receta;
import com.example.refrimancia.modelo.entidad.Usuario;
import com.example.refrimancia.modelo.response.RespuestaPaginada;
import com.example.refrimancia.modelo.response.RespuestaUnica;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioFragment extends Fragment {

    private static final String TAG = "UsuarioFragment";
    private static final int MAX_PAGES = 50;
    private static final long REFRESH_INTERVAL_MS = 60_000L;

    private ImageView imagenPerfil;
    private TextView nombreUsuario;
    private Button btnEditarPerfil;
    private Button btnCerrarSesion;
    private RecyclerView rvMisRecetas;
    private View contenidoVacio;
    private SessionManager sessionManager;
    private AdaptadorMisRecetas adaptadorMisRecetas;
    private SwipeRefreshLayout swipeRecetas;
    private boolean perfilCargado = false;
    private boolean refrescandoRecetas = false;
    private boolean primeraCarga = true;
    private long lastRefreshAt = 0L;

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
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        if (swipeRecetas != null) {
            swipeRecetas.setOnRefreshListener(() -> refrescarRecetas(true));
        }
    }

    private void cargarDatosUsuario() {
        if (sessionManager.fetchAuthToken() == null) {
            mostrarEstadoVacio();
            return;
        }
        if (!perfilCargado) {
            obtenerPerfilUsuario();
        } else if (!refrescandoRecetas) {
            refrescarRecetas(false);
        }
    }

    private void obtenerPerfilUsuario() {
        UsuarioService usuarioService = ClienteRetrofit.obtenerInstancia(requireContext()).create(UsuarioService.class);
        usuarioService.obtenerPerfil().enqueue(new Callback<RespuestaUnica<Usuario>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Usuario>> call, Response<RespuestaUnica<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
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
                Log.e(TAG, "Error al cargar perfil API: " + t.getMessage());
                mostrarDesdeSesion();
            }
        });
    }

    private void mostrarDesdeSesion() {
        int idUsuario = sessionManager.fetchUserId();
        String nombre = sessionManager.fetchUserName();

        if (idUsuario > 0) {
            nombreUsuario.setText("@" + nombre);
            imagenPerfil.setImageResource(R.drawable.bg_placeholder_circular);
            contenidoVacio.setVisibility(View.GONE);
            perfilCargado = true;
            primeraCarga = false;
            cargarRecetasUsuarioLogueado(idUsuario, nombre);
        } else {
            mostrarEstadoVacio();
        }
    }

    private void refrescarRecetas() {
        refrescarRecetas(true);
    }

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
        cargarRecetasUsuarioLogueado(idUsuario, nombre);
    }

    private void cargarRecetasUsuarioLogueado(int idUsuario, String nombreUsuario) {
        RecetaService recetaService = ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        contenidoVacio.setVisibility(View.GONE);
        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, 1, new ArrayList<>());
    }

    private void cargarPaginaRecetas(RecetaService recetaService, int idUsuario, String nombreUsuario, int pagina, List<Receta> acumuladas) {
        recetaService.obtenerRecetas(pagina).enqueue(new Callback<RespuestaPaginada<Receta>>() {
            @Override
            public void onResponse(Call<RespuestaPaginada<Receta>> call, Response<RespuestaPaginada<Receta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RespuestaPaginada<Receta> cuerpo = response.body();
                    List<Receta> todasRecetas = cuerpo.getData();
                    String nombreNormalizado = normalizarUsuario(nombreUsuario);
                    if (todasRecetas != null) {
                        for (Receta r : todasRecetas) {
                            boolean coincideId = r.getIdUsuario() == idUsuario;
                            boolean coincideNombre = normalizarUsuario(r.getNombreUsuario()).equals(nombreNormalizado);
                            if (coincideId || coincideNombre) {
                                acumuladas.add(r);
                            }
                        }
                    }

                    Integer totalPaginas = cuerpo.getTotalPages();
                    boolean hayDatos = todasRecetas != null && !todasRecetas.isEmpty();
                    if (totalPaginas != null) {
                        if (pagina < totalPaginas) {
                            cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, acumuladas);
                        } else {
                            mostrarRecetasUsuario(acumuladas);
                        }
                    } else if (hayDatos && pagina < MAX_PAGES) {
                        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, acumuladas);
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

    private void mostrarRecetasUsuario(List<Receta> misRecetas) {
        if (misRecetas == null || misRecetas.isEmpty()) {
            if (adaptadorMisRecetas == null || adaptadorMisRecetas.getItemCount() == 0) {
                mostrarEstadoVacio();
            } else {
                contenidoVacio.setVisibility(View.GONE);
            }
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

    private void actualizarUI(Usuario usuario) {
        if (usuario != null) {
            nombreUsuario.setText("@" + usuario.getNombreUsuario());

            if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
                Glide.with(this)
                        .load(usuario.getUrlFotoPerfil())
                        .placeholder(R.drawable.bg_placeholder_circular)
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

    private void mostrarEstadoVacio() {
        contenidoVacio.setVisibility(View.VISIBLE);
        Log.d(TAG, "No hay datos de usuario disponibles");
    }

    private void abrirEditarPerfil() {
        int idUsuario = sessionManager.fetchUserId();
        String token = sessionManager.fetchAuthToken();

        if (idUsuario <= 0 || token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo obtener la sesión del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), com.example.refrimancia.VentanaEditarPerfil.class);
        intent.putExtra("ID_USUARIO", idUsuario);
        intent.putExtra("TOKEN", token);
        startActivity(intent);
    }

    private void cerrarSesion() {
        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), com.example.refrimancia.LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private class AdaptadorMisRecetas extends RecyclerView.Adapter<AdaptadorMisRecetas.MiRecetaViewHolder> {
        private final List<Receta> recetas;

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
            holder.tvTitulo.setText(r.getTitulo());
            holder.tvFecha.setText(r.getFechaCreacion() != null ? r.getFechaCreacion() : "Reciente");
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
                        .centerCrop()
                        .into(holder.ivImagen);
            } else {
                holder.ivImagen.setImageResource(R.drawable.bg_placeholder_circular);
            }
        }

        private String construirMetaReceta(Receta receta) {
            List<String> partes = new ArrayList<>();
            if (receta.getCategoria() != null && !receta.getCategoria().isEmpty()) {
                partes.add(receta.getCategoria());
            }
            if (receta.getTiempoPreparacion() > 0) {
                partes.add(formatTiempo(receta.getTiempoPreparacion()));
            }
            if (receta.getSemaforo() != null && !receta.getSemaforo().isEmpty()) {
                partes.add("Semaforo: " + receta.getSemaforo());
            }
            if (partes.isEmpty()) {
                return "";
            }
            return android.text.TextUtils.join(" | ", partes);
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

            MiRecetaViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImagen = itemView.findViewById(R.id.iv_receta_usuario);
                tvTitulo = itemView.findViewById(R.id.tv_titulo_receta_usuario);
                tvFecha = itemView.findViewById(R.id.tv_fecha_receta_usuario);
                tvMeta = itemView.findViewById(R.id.tv_meta_receta_usuario);
            }
        }
    }

    private String formatTiempo(int minutos) {
        if (minutos <= 0) return "0 min";
        int horas = minutos / 60;
        int minRestantes = minutos % 60;
        if (horas > 0) {
            if (minRestantes > 0) {
                return horas + " h " + minRestantes + " min";
            } else {
                return horas + " h";
            }
        }
        return minutos + " min";
    }
}


