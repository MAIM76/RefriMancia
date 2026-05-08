package com.example.refrimancia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.refrimancia.R;
import com.example.refrimancia.model.entity.Comentario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para mostrar una lista de comentarios en un RecyclerView.
 * Cada comentario muestra el nombre de usuario, el contenido, la fecha y la foto de perfil.
 */
public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    // Lista de comentarios a mostrar
    private List<Comentario> comentarios;

    /**
     * Constructor del adaptador.
     * @param comentarios Lista inicial de comentarios a mostrar
     */
    public ComentarioAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * Actualiza la lista de comentarios y notifica los cambios.
     * @param comentarios Nueva lista de comentarios
     */
    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        notifyDataSetChanged();
    }

    /**
     * Crea una nueva vista para un comentario.
     * @param parent ViewGroup padre
     * @param viewType Tipo de vista (no se usa en este caso)
     * @return Nuevo ComentarioViewHolder
     */
    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, 
                false);
        return new ComentarioViewHolder(view);
    }

    /**
     * Vincula los datos de un comentario a la vista.
     * @param holder ViewHolder que contiene las vistas
     * @param position Posición del comentario en la lista
     */
    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);

        // Configurar nombre de usuario con fallback a ID si no hay nombre
        if (comentario.getNombreUsuario() != null) {
            holder.tvUsuario.setText(holder.itemView.getContext().getString(R.string.recipe_username_format,
                    comentario.getNombreUsuario()));
        } else {
            holder.tvUsuario.setText(holder.itemView.getContext().getString(R.string.recipe_user_id_format,
                    comentario.getIdUsuario()));
        }

        // Configurar contenido del comentario
        String contenido = comentario.getTexto();
        holder.tvContenido.setText(contenido);

        // Configurar fecha formateada
        String fecha = formatFecha(comentario.getFechaNormalizada());
        holder.tvFecha.setText(fecha);

        // Cargar foto de perfil con Glide y manejo de errores
        if (comentario.getUrlFotoPerfil() != null && !comentario.getUrlFotoPerfil().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(comentario.getUrlFotoPerfil())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .fallback(android.R.drawable.ic_menu_gallery)
                    .circleCrop()
                    .into(holder.ivUsuario);
        } else {
            holder.ivUsuario.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    /**
     * Formatea la fecha del comentario a un formato legible.
     * Intenta múltiples formatos de entrada y proporciona fallback.
     * @param fechaOriginal Fecha en formato ISO o similar
     * @return Fecha formateada como "dd/MM/yyyy HH:mm" o la fecha original si falla
     */
    private String formatFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "";
        
        try {
            // Intentar formato completo con milisegundos
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = formatoOriginal.parse(fechaOriginal);
            if (date != null) {
                SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return formatoNuevo.format(date);
            }
        } catch (ParseException e) {
            // Intentar formato sin milisegundos
            try {
                SimpleDateFormat formatoOriginal2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date date2 = formatoOriginal2.parse(fechaOriginal);
                if (date2 != null) {
                    SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    return formatoNuevo.format(date2);
                }
            } catch (ParseException ex) {
                // Fallback: mostrar solo la fecha si ambos formatos fallan
                if (fechaOriginal.length() >= 10) {
                     return fechaOriginal.substring(0, 10); // "yyyy-MM-dd" fallback
                }
            }
        }
        return fechaOriginal;
    }

    /**
     * Devuelve el número total de comentarios.
     * @return Número de comentarios o 0 si la lista es nula
     */
    @Override
    public int getItemCount() {
        return comentarios == null ? 0 : comentarios.size();
    }

    /**
     * ViewHolder que contiene las vistas para un comentario individual.
     */
    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        // Vistas del comentario
        public TextView tvUsuario, tvFecha, tvContenido;
        public ImageView ivUsuario;

        /**
         * Constructor del ViewHolder.
         * @param itemView Vista del item del comentario
         */
        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar vistas
            tvUsuario = itemView.findViewById(R.id.tv_usuario_comentario);
            tvFecha = itemView.findViewById(R.id.tv_fecha_comentario);
            tvContenido = itemView.findViewById(R.id.tv_contenido_comentario);
            ivUsuario = itemView.findViewById(R.id.iv_usuario_comentario);
        }
    }
}
