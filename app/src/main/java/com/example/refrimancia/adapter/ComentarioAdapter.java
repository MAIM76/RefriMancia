package com.example.refrimancia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    public interface OnEditarListener {
        void onEditar(Comentario comentario);
    }

    public interface OnEliminarListener {
        void onEliminar(Comentario comentario);
    }

    // Lista de comentarios a mostrar
    private List<Comentario> comentarios;
    private int idUsuarioLogueado = -1;
    private OnEditarListener editarListener;
    private OnEliminarListener eliminarListener;

    public ComentarioAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public void setIdUsuarioLogueado(int id) {
        this.idUsuarioLogueado = id;
    }

    public void setOnEditarListener(OnEditarListener listener) {
        this.editarListener = listener;
    }

    public void setOnEliminarListener(OnEliminarListener listener) {
        this.eliminarListener = listener;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, 
                false);
        return new ComentarioViewHolder(view);
    }

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

        // Mostrar botones solo si el comentario pertenece al usuario logueado
        boolean esPropietario = idUsuarioLogueado > 0 && comentario.getIdUsuario() == idUsuarioLogueado;
        holder.llAcciones.setVisibility(esPropietario ? View.VISIBLE : View.GONE);
        if (esPropietario) {
            holder.btnEditar.setOnClickListener(v -> {
                if (editarListener != null) editarListener.onEditar(comentario);
            });
            holder.btnEliminar.setOnClickListener(v -> {
                if (eliminarListener != null) eliminarListener.onEliminar(comentario);
            });
        }

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

    @Override
    public int getItemCount() {
        return comentarios == null ? 0 : comentarios.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsuario, tvFecha, tvContenido;
        public ImageView ivUsuario;
        public LinearLayout llAcciones;
        public TextView btnEditar, btnEliminar;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_comentario);
            tvFecha = itemView.findViewById(R.id.tv_fecha_comentario);
            tvContenido = itemView.findViewById(R.id.tv_contenido_comentario);
            ivUsuario = itemView.findViewById(R.id.iv_usuario_comentario);
            llAcciones = itemView.findViewById(R.id.ll_acciones_comentario);
            btnEditar = itemView.findViewById(R.id.btn_editar_comentario);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_comentario);
        }
    }
}
