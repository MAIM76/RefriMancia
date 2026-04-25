package com.example.refrimancia.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.modelo.Receta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ValoracionService;
import com.example.refrimancia.modelo.RespuestaValoracionReceta;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaptadorReceta extends RecyclerView.Adapter<AdaptadorReceta.RecetaViewHolder> {

    // Lista de recetas a mostrar en el RecyclerView
    private List<Receta> listaRecetas;
    // Copia de la lista completa para realizar búsquedas sin perder datos
    private final List<Receta> listaRecetasCompleta;
    private final Map<Integer, Float> valoracionesCache = new HashMap<>();
    private Context contexto;
    private OnRecetaClickListener listener;
    private OnValoracionClickListener valoracionListener;
    private OnComentarioClickListener comentarioListener;

    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta);
    }

    public interface OnComentarioClickListener {
        void onComentarioClick(Receta receta);
    }

    public interface OnValoracionClickListener {
        void onValoracionClick(Receta receta);
    }

    public AdaptadorReceta(List<Receta> listaRecetas, Context contexto, OnRecetaClickListener listener) {
        this.listaRecetas = listaRecetas;
        this.contexto = contexto;
        this.listener = listener;
        this.listaRecetasCompleta = new ArrayList<>(listaRecetas);
    }

    public void setOnComentarioClickListener(OnComentarioClickListener comentarioListener) {
        this.comentarioListener = comentarioListener;
    }

    public void setOnValoracionClickListener(OnValoracionClickListener valoracionListener) {
        this.valoracionListener = valoracionListener;
    }

    public List<Receta> getListaRecetasCompleta() {
        return this.listaRecetasCompleta;
    }

    public void actualizarDatos(List<Receta> nuevasRecetas) {
        this.listaRecetas.clear();
        this.listaRecetas.addAll(nuevasRecetas);
        this.listaRecetasCompleta.clear();
        this.listaRecetasCompleta.addAll(nuevasRecetas);
        notifyDataSetChanged();
    }

    public void agregarDatos(List<Receta> nuevasRecetas) {
        int posicionInicial = this.listaRecetas.size();
        this.listaRecetas.addAll(nuevasRecetas);
        this.listaRecetasCompleta.addAll(nuevasRecetas);
        notifyItemRangeInserted(posicionInicial, nuevasRecetas.size());
    }

    public void filtrar(String consulta) {
        listaRecetas.clear();
        if (consulta == null || consulta.trim().isEmpty()) {
            listaRecetas.addAll(listaRecetasCompleta);
        } else {
            String consultaMinusculas = consulta.toLowerCase().trim();
            for (Receta receta : listaRecetasCompleta) {
                String titulo = receta.getTitulo() != null ? receta.getTitulo().toLowerCase() : "";
                String descripcion = receta.getDescripcion() != null ? receta.getDescripcion().toLowerCase() : "";
                String ingredientes = receta.getIngredientes() != null ? receta.getIngredientes().toLowerCase() : "";
                if (titulo.contains(consultaMinusculas)
                        || descripcion.contains(consultaMinusculas)
                        || ingredientes.contains(consultaMinusculas)) {
                    listaRecetas.add(receta);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipoDVista) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.elemento_tarjeta_receta, padre, false);
        return new RecetaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int posicion) {
        Receta receta = listaRecetas.get(posicion);

        // Configurar título
        holder.tituloReceta.setText(receta.getTitulo() != null ? receta.getTitulo() : "Sin título");

        // Configurar descripción (limitada)
        holder.descripcionReceta.setText(receta.getDescripcion() != null ? receta.getDescripcion() : "Sin descripción");

        // Configurar tiempo de preparación
        holder.tiempoPrep.setText(formatTiempo(receta.getTiempoPreparacion()));

        // Configurar valoración por defecto
        if (holder.valoracionReceta != null) {
            holder.valoracionReceta.setRating(0); // placeholder mientras carga
            Integer idReceta = receta.getIdReceta();
            holder.valoracionReceta.setTag(idReceta);
            
            if (valoracionesCache.containsKey(idReceta)) {
                Float valorCacheado = valoracionesCache.get(idReceta);
                if (valorCacheado != null) {
                    holder.valoracionReceta.setRating(valorCacheado);
                }
            } else {
                solicitarValoracionReceta(idReceta, valoracion -> {
                    if (idReceta.equals(holder.valoracionReceta.getTag())) {
                        holder.valoracionReceta.setRating(valoracion);
                    }
                });
            }
        }

        // Cargar imagen usando Glide
        if (receta.getImagenUrl() == null || receta.getImagenUrl().isEmpty() || receta.getImagenUrl().equals("url_imagen_aqui")) {
            // Mostrar imagen por defecto si no hay URL
            holder.imagenReceta.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            // Cargar la imagen desde la URL
            Glide.with(holder.itemView.getContext())
                    .load(receta.getImagenUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery) // Imagen mientras carga
                    .into(holder.imagenReceta);
        }

        if (receta.getNombreUsuario() != null && !receta.getNombreUsuario().isEmpty()) {
            holder.nombreUsuario.setText(contexto.getString(R.string.recipe_username_format, receta.getNombreUsuario()));
        } else {
            holder.nombreUsuario.setText(contexto.getString(R.string.recipe_user_id_format, receta.getIdUsuario()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecetaClick(receta);
            }
        });

        if (holder.btnComentarios != null) {
            holder.btnComentarios.setOnClickListener(v -> {
                if (comentarioListener != null) {
                    comentarioListener.onComentarioClick(receta);
                }
            });
        }

        if (holder.btnResena != null) {
            holder.btnResena.setOnClickListener(v -> {
                if (valoracionListener != null) {
                    valoracionListener.onValoracionClick(receta);
                }
            });
        }
    }

    public void refrescarValoracionReceta(int idReceta) {
        solicitarValoracionReceta(idReceta, valoracion -> {
            int posicion = obtenerPosicionReceta(idReceta);
            if (posicion != -1) {
                notifyItemChanged(posicion);
            }
        });
    }

    private interface OnValoracionCargadaListener {
        void onValoracionCargada(float valoracion);
    }

    private void solicitarValoracionReceta(int idReceta, OnValoracionCargadaListener listenerValoracion) {
        ValoracionService valoracionService = ClienteRetrofit.obtenerInstancia(contexto).create(ValoracionService.class);
        valoracionService.obtenerValoracionesPorReceta(idReceta).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaValoracionReceta> call, Response<RespuestaValoracionReceta> response) {
                float valoracionFinal = 0f;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    valoracionFinal = response.body().getData().getNotaMedia();
                }
                valoracionesCache.put(idReceta, valoracionFinal);
                listenerValoracion.onValoracionCargada(valoracionFinal);
            }

            @Override
            public void onFailure(Call<RespuestaValoracionReceta> call, Throwable t) {
                // Se mantiene el valor actual si falla la consulta.
            }
        });
    }

    private int obtenerPosicionReceta(int idReceta) {
        for (int i = 0; i < listaRecetas.size(); i++) {
            if (listaRecetas.get(i).getIdReceta() == idReceta) {
                return i;
            }
        }
        return -1;
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
        } else {
            return minutos + " min";
        }
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView tituloReceta;
        TextView descripcionReceta;
        TextView tiempoPrep;
        RatingBar valoracionReceta;
        ImageView imagenReceta;
        TextView nombreUsuario;
        ImageView btnResena;
        ImageView btnComentarios;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloReceta = itemView.findViewById(R.id.recipe_title);
            descripcionReceta = itemView.findViewById(R.id.recipe_description);
            tiempoPrep = itemView.findViewById(R.id.recipe_time);
            valoracionReceta = itemView.findViewById(R.id.recipe_rating);
            imagenReceta = itemView.findViewById(R.id.iv_imagen_receta);
            nombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
            btnResena = itemView.findViewById(R.id.btn_megusta);
            btnComentarios = itemView.findViewById(R.id.btn_comentarios);
        }
    }
}
