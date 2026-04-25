package com.example.refrimancia.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.modelo.Receta;
import com.example.refrimancia.modelo.RespuestaPaginada;
import com.example.refrimancia.modelo.RespuestaUnica;
import com.example.refrimancia.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioFragment extends Fragment {

    private static final String TAG = "UsuarioFragment";

    private ImageView imagenPerfil;
    private TextView nombreUsuario;
    private Button btnEditarPerfil;
    private RecyclerView rvMisRecetas;
    private View contenidoVacio;
    private SessionManager sessionManager;

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

    private void inicializarVistas(View vista) {
        sessionManager = new SessionManager(requireContext());
        imagenPerfil = vista.findViewById(R.id.profile_image);
        nombreUsuario = vista.findViewById(R.id.nombre_usuario);
        btnEditarPerfil = vista.findViewById(R.id.btn_editar_perfil);
        rvMisRecetas = vista.findViewById(R.id.rv_recetas_usuario);
        contenidoVacio = vista.findViewById(R.id.empty_state);

        rvMisRecetas.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void cargarDatosUsuario() {
        if (sessionManager.fetchAuthToken() == null) {
            mostrarEstadoVacio();
            return;
        }
        obtenerPerfilUsuario();
    }

    private void obtenerPerfilUsuario() {
        UsuarioService usuarioService = ClienteRetrofit.obtenerInstancia(requireContext()).create(UsuarioService.class);
        usuarioService.obtenerPerfil().enqueue(new Callback<RespuestaUnica<Usuario>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Usuario>> call, Response<RespuestaUnica<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Usuario usuario = response.body().getData();
                    actualizarUI(usuario);
                    cargarRecetasUsuarioLogueado(usuario.getIdUsuario());
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
            cargarRecetasUsuarioLogueado(idUsuario);
        } else {
            mostrarEstadoVacio();
        }
    }

    private void cargarRecetasUsuarioLogueado(int idUsuario) {
        RecetaService recetaService = ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        recetaService.obtenerRecetas().enqueue(new Callback<RespuestaPaginada<Receta>>() {
            @Override
            public void onResponse(Call<RespuestaPaginada<Receta>> call, Response<RespuestaPaginada<Receta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Receta> todasRecetas = response.body().getData();
                    List<Receta> misRecetas = new ArrayList<>();
                    if (todasRecetas != null) {
                        for (Receta r : todasRecetas) {
                            if (r.getIdUsuario() == idUsuario) {
                                misRecetas.add(r);
                            }
                        }
                    }

                    if (misRecetas.isEmpty()) {
                        mostrarEstadoVacio();
                    } else {
                        contenidoVacio.setVisibility(View.GONE);
                        rvMisRecetas.setAdapter(new AdaptadorMisRecetas(misRecetas));
                    }
                } else {
                    mostrarEstadoVacio();
                }
            }

            @Override
            public void onFailure(Call<RespuestaPaginada<Receta>> call, Throwable t) {
                Log.e(TAG, "Error al cargar recetas API: " + t.getMessage());
                mostrarEstadoVacio();
            }
        });
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

    private class AdaptadorMisRecetas extends RecyclerView.Adapter<AdaptadorMisRecetas.MiRecetaViewHolder> {
        private final List<Receta> recetas;

        public AdaptadorMisRecetas(List<Receta> recetas) {
            this.recetas = recetas;
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
            holder.ivImagen.setImageResource(R.drawable.ic_launcher_background);
        }

        @Override
        public int getItemCount() {
            return recetas.size();
        }

        class MiRecetaViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImagen;
            TextView tvTitulo;
            TextView tvFecha;

            MiRecetaViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImagen = itemView.findViewById(R.id.iv_receta_usuario);
                tvTitulo = itemView.findViewById(R.id.tv_titulo_receta_usuario);
                tvFecha = itemView.findViewById(R.id.tv_fecha_receta_usuario);
            }
        }
    }
}

