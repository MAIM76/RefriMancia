package com.example.refrimancia.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refrimancia.R;
import com.example.refrimancia.modelo.Receta;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorReceta extends RecyclerView.Adapter<AdaptadorReceta.VistaReceta> {

    // Lista de recetas a mostrar en el RecyclerView
    private List<Receta> listaRecetas;
    // Copia de la lista completa para realizar búsquedas sin perder datos
    private final List<Receta> listaRecetasCompleta;

    public interface EscuchadorClicReceta {
        void alHacerClicEnReceta(Receta receta);
    }

    private EscuchadorClicReceta escuchador;

    public AdaptadorReceta(List<Receta> listaRecetas) {
        this.listaRecetas = new ArrayList<>(listaRecetas);
        this.listaRecetasCompleta = new ArrayList<>(listaRecetas);
    }

    public void setEscuchadorClicReceta(EscuchadorClicReceta escuchador) {
        this.escuchador = escuchador;
    }

    public void actualizarDatos(List<Receta> nuevasRecetas) {
        this.listaRecetas.clear();
        this.listaRecetas.addAll(nuevasRecetas);
        this.listaRecetasCompleta.clear();
        this.listaRecetasCompleta.addAll(nuevasRecetas);
        notifyDataSetChanged();
    }

    public void filtrar(String consulta) {
        listaRecetas.clear();
        if (consulta == null || consulta.trim().isEmpty()) {
            listaRecetas.addAll(listaRecetasCompleta);
        } else {
            String consultaMinusculas = consulta.toLowerCase().trim();
            for (Receta receta : listaRecetasCompleta) {
                if (receta.getTituloReceta().toLowerCase().contains(consultaMinusculas)
                        || receta.getDescripcion().toLowerCase().contains(consultaMinusculas)) {
                    listaRecetas.add(receta);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VistaReceta onCreateViewHolder(@NonNull ViewGroup padre, int tipoDVista) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.elemento_tarjeta_receta, padre, false);
        return new VistaReceta(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull VistaReceta holder, int posicion) {
        Receta receta = listaRecetas.get(posicion);
        holder.textViewTitulo.setText(receta.getTituloReceta() != null ? receta.getTituloReceta() : "Sin título");
        holder.textViewDescripcion.setText(receta.getDescripcion() != null ? receta.getDescripcion() : "Sin descripción");

        String tiempoText = receta.getTiempoPreparacion() > 0 ? "Tiempo de prep: " + receta.getTiempoPreparacion() + " min" : "Tiempo de prep: N/A";
        holder.textViewTiempo.setText(tiempoText);

        // Ocultar imagen si no hay URL válida o usar un placeholder (pendiente implementar Glide/Picasso)
        if (receta.getImagenReceta() == null || receta.getImagenReceta().isEmpty() || receta.getImagenReceta().equals("url_imagen_aqui")) {
            holder.imagenReceta.setImageDrawable(null);
        } else {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(receta.getImagenReceta())
                .into(holder.imagenReceta);
        }

        holder.itemView.setOnClickListener(v -> {
            if (escuchador != null) escuchador.alHacerClicEnReceta(receta);
        });
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public static class VistaReceta extends RecyclerView.ViewHolder {
        ImageView imagenReceta;
        TextView textViewTitulo;
        TextView textViewDescripcion;
        TextView textViewTiempo;

        public VistaReceta(@NonNull View itemVista) {
            super(itemVista);
            imagenReceta = itemVista.findViewById(R.id.recipe_image);
            textViewTitulo = itemVista.findViewById(R.id.recipe_title);
            textViewDescripcion = itemVista.findViewById(R.id.recipe_description);
            textViewTiempo = itemVista.findViewById(R.id.recipe_time);
        }
    }
}
