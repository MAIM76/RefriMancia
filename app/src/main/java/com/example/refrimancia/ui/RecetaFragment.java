package com.example.refrimancia.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.refrimancia.adaptador.AdaptadorComentario;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ComentarioService;
import com.example.refrimancia.modelo.Comentario;
import com.example.refrimancia.modelo.Receta;
import com.example.refrimancia.modelo.RespuestaPaginada;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecetaFragment extends Fragment {
    private static final String ARG_RECETA = "receta_param";
    private Receta receta;
    private int idReceta;

    private AdaptadorComentario adaptadorComentario;

    public RecetaFragment() {}
    public static RecetaFragment newInstance(Receta receta) {
        RecetaFragment fragment = new RecetaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECETA, receta);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receta = (Receta) getArguments().getSerializable(ARG_RECETA);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.receta_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView ivImagen = view.findViewById(R.id.iv_detalle_imagen);
        TextView tvTitulo = view.findViewById(R.id.tv_detalle_titulo);
        // Using description for ingredients, as changed previously
        TextView tvDesc = view.findViewById(R.id.tv_detalle_desc);
        // Using steps for preparation
        TextView tvPasos = view.findViewById(R.id.tv_detalle_pasos);
        TextView tvAutor = view.findViewById(R.id.tv_detalle_autor);

        RecyclerView rvComentarios = view.findViewById(R.id.rv_comentarios_receta);
        rvComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComentarios.setNestedScrollingEnabled(false);
        adaptadorComentario = new AdaptadorComentario(new ArrayList<>());
        rvComentarios.setAdapter(adaptadorComentario);

        ImageButton btnVolver = view.findViewById(R.id.btn_volver_detalle);
        btnVolver.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        if (receta != null) {
            tvTitulo.setText(receta.getTitulo() != null ? receta.getTitulo() : "Receta sin título");
            tvDesc.setText(receta.getDescripcion() != null ? receta.getDescripcion() : "Sin descripción.");
            tvPasos.setText(receta.getPasos() != null ? receta.getPasos() : "Sin pasos documentados.");
            if (receta.getImagenUrl() != null && !receta.getImagenUrl().isEmpty()) {
                Glide.with(requireContext())
                        .load(receta.getImagenUrl())
                        .into(ivImagen);
            }
            idReceta = receta.getIdReceta();
            tvAutor.setText(receta.getNombreUsuario() != null ? "@" + receta.getNombreUsuario() : "@usuario_desconocido");
        }

        cargarComentarios(idReceta);
    }

    private void cargarComentarios(int recetaId) {
        ComentarioService comentarioService = ClienteRetrofit.obtenerInstancia().create(ComentarioService.class);
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
                    Toast.makeText(getContext(), "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Comentario>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error de red al cargar comentarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
