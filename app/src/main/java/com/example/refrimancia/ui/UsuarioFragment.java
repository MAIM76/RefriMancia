package com.example.refrimancia.ui;

import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.api.UsuarioService;
import com.example.refrimancia.modelo.AuthToken;
import com.example.refrimancia.modelo.LoginRequest;
import com.example.refrimancia.modelo.Receta;
import com.example.refrimancia.modelo.RespuestaPaginada;
import com.example.refrimancia.modelo.RespuestaUnica;
import com.example.refrimancia.modelo.Usuario;
import com.example.refrimancia.utils.DatosEjemplo;

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
        imagenPerfil = vista.findViewById(R.id.profile_image);
        nombreUsuario = vista.findViewById(R.id.nombre_usuario);
        btnEditarPerfil = vista.findViewById(R.id.btn_editar_perfil);
        rvMisRecetas = vista.findViewById(R.id.rv_recetas_usuario);
        contenidoVacio = vista.findViewById(R.id.empty_state);

        rvMisRecetas.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void cargarDatosUsuario() {
        DatosEjemplo.loginAutomaticoTemporal(exito -> {
            if (exito) {
                obtenerPerfilUsuario();
            } else {
                mostrarEstadoVacio();
            }
        });
    }

    private void obtenerPerfilUsuario() {
        UsuarioService usuarioService = ClienteRetrofit.obtenerInstancia().create(UsuarioService.class);
        usuarioService.obtenerPerfil().enqueue(new Callback<RespuestaUnica<Usuario>>() {
            @Override
            public void onResponse(Call<RespuestaUnica<Usuario>> call, Response<RespuestaUnica<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Usuario usuario = response.body().getData();
                    DatosEjemplo.usuarioActual = usuario;
                    actualizarUI(usuario);
                    cargarRecetasUsuarioLogueado(usuario.getIdUsuario());
                } else {
                    // Fallback to DatosEjemplo if the endpoint fails
                    if (DatosEjemplo.usuarioActual != null) {
                        actualizarUI(DatosEjemplo.usuarioActual);
                        cargarRecetasUsuarioLogueado(DatosEjemplo.usuarioActual.getIdUsuario());
                    } else {
                        mostrarEstadoVacio();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaUnica<Usuario>> call, Throwable t) {
                Log.e(TAG, "Error al cargar perfil API: " + t.getMessage());
                // Fallback
                if (DatosEjemplo.usuarioActual != null) {
                    actualizarUI(DatosEjemplo.usuarioActual);
                    cargarRecetasUsuarioLogueado(DatosEjemplo.usuarioActual.getIdUsuario());
                } else {
                    mostrarEstadoVacio();
                }
            }
        });
    }

    private void cargarRecetasUsuarioLogueado(int idUsuario) {
        RecetaService recetaService = ClienteRetrofit.obtenerInstancia().create(RecetaService.class);
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
                        AdaptadorMisRecetas adapter = new AdaptadorMisRecetas(misRecetas);
                        rvMisRecetas.setAdapter(adapter);
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

    // Adaptador interno para Mis Recetas
    private class AdaptadorMisRecetas extends RecyclerView.Adapter<AdaptadorMisRecetas.MiRecetaViewHolder> {
        private List<Receta> recetas;

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

            public MiRecetaViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImagen = itemView.findViewById(R.id.iv_receta_usuario);
                tvTitulo = itemView.findViewById(R.id.tv_titulo_receta_usuario);
                tvFecha = itemView.findViewById(R.id.tv_fecha_receta_usuario);
            }
        }
    }
}
