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

    private List<Receta> listaRecetas;
    private final List<Receta> listaRecetasCompleta; // Para filtrado

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
        holder.textViewTitulo.setText(receta.getTituloReceta());
        holder.textViewDescripcion.setText(receta.getDescripcion());

        // TODO: Cargar imagen desde URL con Glide si está disponible
        // Por ahora solo muestra placeholder gris
        holder.imagenReceta.setImageDrawable(null);

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

        public VistaReceta(@NonNull View itemVista) {
            super(itemVista);
            imagenReceta = itemVista.findViewById(R.id.recipe_image);
            textViewTitulo = itemVista.findViewById(R.id.recipe_title);
            textViewDescripcion = itemVista.findViewById(R.id.recipe_description);
        }
    }
}

