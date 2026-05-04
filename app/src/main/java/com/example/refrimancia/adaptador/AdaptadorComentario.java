package com.example.refrimancia.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.modelo.entidad.Comentario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorComentario extends RecyclerView.Adapter<AdaptadorComentario.ComentarioViewHolder> {

    private List<Comentario> comentarios;

    public AdaptadorComentario(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);

        // Use nombreUsuario if it exists, otherwise use ID
        if (comentario.getNombreUsuario() != null) {
            holder.tvUsuario.setText(String.format("@%s", comentario.getNombreUsuario()));
        } else {
            holder.tvUsuario.setText(String.format(Locale.getDefault(), "@usuario_%d", comentario.getIdUsuario()));
        }

        String contenido = comentario.getTexto();
        holder.tvContenido.setText(contenido);

        String fecha = formatFecha(comentario.getFechaNormalizada());
        holder.tvFecha.setText(fecha);

        if (comentario.getUrlFotoPerfil() != null && !comentario.getUrlFotoPerfil().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(comentario.getUrlFotoPerfil())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(holder.ivUsuario);
        } else {
            holder.ivUsuario.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private String formatFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "";
        try {
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = formatoOriginal.parse(fechaOriginal);
            if (date != null) {
                SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return formatoNuevo.format(date);
            }
        } catch (ParseException e) {
            // Intenta otro formato posible si falla el primero
            try {
                SimpleDateFormat formatoOriginal2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date date2 = formatoOriginal2.parse(fechaOriginal);
                if (date2 != null) {
                    SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    return formatoNuevo.format(date2);
                }
            } catch (ParseException ex) {
                // Si ambos fallan, formatea solo la fecha (recorta la hora)
                if (fechaOriginal.length() >= 10) {
                     return fechaOriginal.substring(0, 10); // "yyyy-MM-dd" fallback
                }
            }
        }
        return fechaOriginal;
    }

    @Override
    public int getItemCount() {
        return comentarios == null ? 0 : comentarios.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsuario, tvFecha, tvContenido;
        public ImageView ivUsuario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_comentario);
            tvFecha = itemView.findViewById(R.id.tv_fecha_comentario);
            tvContenido = itemView.findViewById(R.id.tv_contenido_comentario);
            ivUsuario = itemView.findViewById(R.id.iv_usuario_comentario);
        }
    }
}
