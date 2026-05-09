package com.example.refrimancia.ui.pablo;

import android.content.Intent;
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
import com.example.refrimancia.ui.CreateRecipeActivity; // Corregido el import
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup contenedor, @Nullable Bundle estadoGuardado) {
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
        btnCerrarSesion.setOnClickListener(v -> mostrarConfirmacionCerrarSesion());
        if (swipeRecetas != null) {
            swipeRecetas.setOnRefreshListener(() -> refrescarRecetas(true));
        }
    }

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
                mostrarDesdeSesion();
            }
        });
    }

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

    private void refrescarRecetas(boolean forzar) {
        if (primeraCarga) return;
        long ahora = SystemClock.elapsedRealtime();
        if (!forzar && (ahora - lastRefreshAt) < REFRESH_INTERVAL_MS) return;
        int idUsuario = sessionManager.fetchUserId();
        String nombre = sessionManager.fetchUserName();
        if (idUsuario <= 0) {
            if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
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
                            if (r.getIdUsuario() == idUsuario || normalizarUsuario(r.getNombreUsuario()).equals(nombreNormalizado)) {
                                acumuladas.add(r);
                            }
                        }
                    }
                    Integer totalPaginas = cuerpo.getTotalPages();
                    if (totalPaginas != null && pagina < totalPaginas) {
                        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, acumuladas);
                    } else if (todasRecetas != null && !todasRecetas.isEmpty() && pagina < MAX_PAGES) {
                        cargarPaginaRecetas(recetaService, idUsuario, nombreUsuario, pagina + 1, acumuladas);
                    } else {
                        mostrarRecetasUsuario(acumuladas);
                    }
                } else {
                    mostrarRecetasUsuario(acumuladas);
                }
                if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
                refrescandoRecetas = false;
            }
            @Override
            public void onFailure(Call<RespuestaPaginada<Receta>> call, Throwable t) {
                mostrarRecetasUsuario(acumuladas);
                if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
                refrescandoRecetas = false;
            }
        });
    }

    private String normalizarUsuario(String nombre) {
        if (nombre == null) return "";
        String limpio = nombre.trim();
        if (limpio.startsWith("@")) limpio = limpio.substring(1);
        return limpio.toLowerCase();
    }

    private void mostrarRecetasUsuario(List<Receta> misRecetas) {
        if (misRecetas == null || misRecetas.isEmpty()) {
            if (adaptadorMisRecetas == null || adaptadorMisRecetas.getItemCount() == 0) mostrarEstadoVacio();
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

    public interface OnFotoPerfilCargadaListener {
        void onFotoPerfilCargada(String url);
    }

    private void actualizarUI(Usuario usuario) {
        if (usuario != null) {
            nombreUsuario.setText(getString(R.string.recipe_user_format, usuario.getNombreUsuario()));
            String urlFoto = usuario.getUrlFotoPerfil();
            sessionManager.saveUserPhoto(urlFoto);
            if (getActivity() instanceof OnFotoPerfilCargadaListener) {
                ((OnFotoPerfilCargadaListener) getActivity()).onFotoPerfilCargada(urlFoto);
            }
            if (urlFoto != null && !urlFoto.isEmpty()) {
                Glide.with(this).load(urlFoto).placeholder(R.drawable.bg_placeholder_circular).circleCrop().into(imagenPerfil);
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
    }

    private void abrirEditarPerfil() {
        if (!sessionManager.isSessionValid()) return;
        startActivity(new Intent(requireContext(), VentanaEditarPerfil.class));
    }

    private void mostrarConfirmacionCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_confirmation_title)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton(R.string.logout_confirm, (dialog, which) -> cerrarSesion())
                .setNegativeButton(R.string.logout_cancel, null)
                .show();
    }

    private void cerrarSesion() {
        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
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
            if (nuevasRecetas != null) this.recetas.addAll(nuevasRecetas);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MiRecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_receta_usuario, parent, false);
            return new MiRecetaViewHolder(vista);
        }

        @Override
        public void onBindViewHolder(@NonNull MiRecetaViewHolder holder, int position) {
            Receta r = recetas.get(position);
            holder.tvTitulo.setText(r.getTitulo());
            holder.tvFecha.setText(r.getFechaCreacion() != null ? formatFecha(r.getFechaCreacion()) : getString(R.string.recipe_recent));
            configurarSemaforo(holder, r);
            
            String meta = construirMetaReceta(r);
            holder.tvMeta.setText(meta);
            holder.tvMeta.setVisibility(meta.isEmpty() ? View.GONE : View.VISIBLE);

            if (r.getImagenUrl() != null && !r.getImagenUrl().isEmpty()) {
                Glide.with(holder.itemView).load(r.getImagenUrl()).centerCrop().into(holder.ivImagen);
            } else {
                holder.ivImagen.setImageResource(R.drawable.bg_placeholder_circular);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), CreateRecipeActivity.class);
                intent.putExtra("receta_editar", r);
                startActivity(intent);
            });
        }

        private String construirMetaReceta(Receta receta) {
            String categoria = receta.getCategoria();
            String tiempo = receta.getTiempoPreparacion() > 0 ? formatTiempo(receta.getTiempoPreparacion()) : null;
            if (categoria != null && !categoria.isEmpty() && tiempo != null) return categoria + " | " + tiempo;
            return (categoria != null) ? categoria : (tiempo != null ? tiempo : "");
        }

        @Override
        public int getItemCount() { return recetas.size(); }

        class MiRecetaViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImagen;
            TextView tvTitulo, tvFecha, tvMeta;
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
            int color = obtenerColorSemaforo(receta.getSemaforo());
            if (color != 0) {
                holder.semaforoDot.setVisibility(View.VISIBLE);
                holder.semaforoDot.getBackground().mutate().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.semaforoDot.setVisibility(View.GONE);
            }
        }
    }

    private int obtenerColorSemaforo(String semaforo) {
        if (semaforo == null) return 0;
        switch (semaforo.toLowerCase()) {
            case "rojo": return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark);
            case "naranja": return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark);
            case "amarillo": return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light);
            case "verde_claro": return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_green_light);
            case "verde_oscuro": return androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark);
            default: return 0;
        }
    }

    private String formatFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(fechaOriginal);
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) { return fechaOriginal; }
    }

    private String formatTiempo(int minutos) {
        int horas = minutos / 60;
        int minRestantes = minutos % 60;
        if (horas > 0) return horas + "h " + minRestantes + "m";
        return minutos + "m";
    }
}
