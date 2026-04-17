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

public class AdaptadorReceta extends RecyclerView.Adapter<AdaptadorReceta.RecetaViewHolder> {

    // Lista de recetas a mostrar en el RecyclerView
    private List<Receta> listaRecetas;
    // Copia de la lista completa para realizar búsquedas sin perder datos
    private final List<Receta> listaRecetasCompleta;
    private Context contexto;
    private OnRecetaClickListener listener;
    private OnComentarioClickListener comentarioListener;

    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta);
    }

    public interface OnComentarioClickListener {
        void onComentarioClick(Receta receta);
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
                if (receta.getTitulo().toLowerCase().contains(consultaMinusculas)
                        || receta.getDescripcion().toLowerCase().contains(consultaMinusculas)) {
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
        holder.tiempoPrep.setText(String.valueOf(receta.getTiempoPreparacion()) + " min");

        // Configurar valoración por defecto
        if (holder.valoracionReceta != null) {
            float ratingPlaceholder = 4.0f + (posicion % 2 == 0 ? 0.5f : -0.5f); // Valoración aleatoria visual de ejemplo
            holder.valoracionReceta.setRating(ratingPlaceholder);
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
            holder.nombreUsuario.setText("@" + receta.getNombreUsuario());
        } else {
            holder.nombreUsuario.setText("@usuario" + receta.getIdUsuario());
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
        ImageView btnComentarios;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloReceta = itemView.findViewById(R.id.recipe_title);
            descripcionReceta = itemView.findViewById(R.id.recipe_description);
            tiempoPrep = itemView.findViewById(R.id.recipe_time);
            valoracionReceta = itemView.findViewById(R.id.recipe_rating);
            imagenReceta = itemView.findViewById(R.id.iv_imagen_receta);
            nombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
            btnComentarios = itemView.findViewById(R.id.btn_comentarios);
        }
    }
}
