package com.example.refrimancia.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.refrimancia.R;
import com.example.refrimancia.model.entity.Receta;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar una lista de recetas en un RecyclerView.
 * Soporta filtrado, carga de valoraciones asíncrona y múltiples acciones por receta.
 */
public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    // ======================== VARIABLES DE INSTANCIA ========================
    
    // Lista de recetas a mostrar en el RecyclerView
    private List<Receta> listaRecetas;
    // Copia de la lista completa para realizar búsquedas sin perder datos
    private final List<Receta> listaRecetasCompleta;
    // Contexto para acceder a recursos
    private Context contexto;
    // Listeners para eventos de clic
    private OnRecetaClickListener listener;
    private OnValoracionClickListener valoracionListener;
    private OnComentarioClickListener comentarioListener;

    // ======================== INTERFACES PARA EVENTOS ========================
    
    /**
     * Interfaz para manejar clics en una receta.
     */
    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta);
    }

    /**
     * Interfaz para manejar clics en el botón de comentarios.
     */
    public interface OnComentarioClickListener {
        void onComentarioClick(Receta receta);
    }

    /**
     * Interfaz para manejar clics en el botón de valoración.
     */
    public interface OnValoracionClickListener {
        void onValoracionClick(Receta receta);
    }

    // ======================== CONSTRUCTOR Y CONFIGURACIÓN ========================
    
    /**
     * Constructor del adaptador.
     * @param listaRecetas Lista inicial de recetas
     * @param contexto Contexto de la aplicación
     * @param listener Listener para clics en recetas
     */
    public RecetaAdapter(List<Receta> listaRecetas, Context contexto, OnRecetaClickListener listener) {
        // Validaciones nulas para prevenir crashes
        if (listaRecetas == null) {
            this.listaRecetas = new ArrayList<>();
        } else {
            this.listaRecetas = listaRecetas;
        }
        
        if (contexto == null) {
            throw new IllegalArgumentException("El contexto no puede ser nulo");
        }
        
        this.contexto = contexto;
        this.listener = listener;
        this.listaRecetasCompleta = new ArrayList<>(this.listaRecetas);
    }

    /**
     * Establece el listener para clics en comentarios.
     * @param comentarioListener Listener de comentarios
     */
    public void setOnComentarioClickListener(OnComentarioClickListener comentarioListener) {
        this.comentarioListener = comentarioListener;
    }

    /**
     * Establece el listener para clics en valoraciones.
     * @param valoracionListener Listener de valoraciones
     */
    public void setOnValoracionClickListener(OnValoracionClickListener valoracionListener) {
        this.valoracionListener = valoracionListener;
    }

    /**
     * Devuelve una copia de la lista completa de recetas.
     * @return Lista completa de recetas
     */
    public List<Receta> getListaRecetasCompleta() {
        return this.listaRecetasCompleta;
    }

    // ======================== MÉTODOS DE MANIPULACIÓN DE DATOS ========================
    
    /**
     * Actualiza todos los datos del adaptador.
     * @param nuevasRecetas Nueva lista de recetas
     */
    public void actualizarDatos(List<Receta> nuevasRecetas) {
        this.listaRecetas.clear();
        this.listaRecetas.addAll(nuevasRecetas);
        this.listaRecetasCompleta.clear();
        this.listaRecetasCompleta.addAll(nuevasRecetas);
        notifyDataSetChanged();
    }

    /**
     * Agrega nuevas recetas a la lista existente.
     * @param nuevasRecetas Lista de recetas a agregar
     */
    public void agregarDatos(List<Receta> nuevasRecetas) {
        int posicionInicial = this.listaRecetas.size();
        this.listaRecetas.addAll(nuevasRecetas);
        this.listaRecetasCompleta.addAll(nuevasRecetas);
        notifyItemRangeInserted(posicionInicial, nuevasRecetas.size());
    }

    /**
     * Filtra las recetas según una consulta de búsqueda.
     * Busca en título, descripción e ingredientes.
     * @param consulta Texto a buscar
     */
    public void filtrar(String consulta) {
        listaRecetas.clear();
        if (consulta == null || consulta.trim().isEmpty()) {
            listaRecetas.addAll(listaRecetasCompleta);
        } else {
            String consultaMinusculas = consulta.toLowerCase().trim();
            for (Receta receta : listaRecetasCompleta) {
                String titulo = receta.getTitulo() != null ? receta.getTitulo().toLowerCase() : "";
                String descripcion = receta.getDescripcion() != null ? 
                        receta.getDescripcion().toLowerCase() : "";
                String ingredientes = receta.getIngredientes() != null ? 
                        receta.getIngredientes().toLowerCase() : "";
                if (titulo.contains(consultaMinusculas)
                        || descripcion.contains(consultaMinusculas)
                        || ingredientes.contains(consultaMinusculas)) {
                    listaRecetas.add(receta);
                }
            }
        }
        notifyDataSetChanged();
    }

    // ======================== MÉTODOS DEL RECYCLERVIEW ========================
    
    /**
     * Crea una nueva vista para una receta.
     * @param padre ViewGroup padre
     * @param tipoDVista Tipo de vista
     * @return Nuevo RecetaViewHolder
     */
    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipoDVista) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.elemento_tarjeta_receta, padre, false);
        return new RecetaViewHolder(vista);
    }

    /**
     * Vincula los datos de una receta a la vista.
     * @param holder ViewHolder que contiene las vistas
     * @param posicion Posición de la receta en la lista
     */
    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int posicion) {
        // Validar posición para prevenir IndexOutOfBoundsException
        if (posicion < 0 || posicion >= listaRecetas.size()) {
            Log.e("RecetaAdapter", "Posición inválida: " + posicion + ", tamaño: " + listaRecetas.size());
            return;
        }
        
        Receta receta = listaRecetas.get(posicion);
        if (receta == null) {
            Log.e("RecetaAdapter", "Receta nula en posición: " + posicion);
            return;
        }

        try {
            // Configurar información básica de la receta
            configurarInformacionBasica(holder, receta);

            // Configurar imagen de la receta
            configurarImagen(holder, receta);

            // Configurar información adicional
            configurarInformacionAdicional(holder, receta);

            // Configurar listeners de clic
            configurarListeners(holder, receta);
        } catch (Exception e) {
            Log.e("RecetaAdapter", "Error al configurar receta en posición " + posicion, e);
        }
    }

    // ======================== MÉTODOS AUXILIARES ========================
    
    /**
     * Refresca la valoración de una receta específica.
     * @param idReceta ID de la receta a refrescar
     */
    public void refrescarValoracionReceta(int idReceta) {
        int posicion = obtenerPosicionReceta(idReceta);
        if (posicion != -1) {
            notifyItemChanged(posicion);
        }
    }

    /**
     * Configura la información básica de la receta (título, tiempo, dificultad, categoría).
     * @param holder ViewHolder de la receta
     * @param receta Receta a configurar
     */
    private void configurarInformacionBasica(RecetaViewHolder holder, Receta receta) {
        try {
            // Configurar título
            if (holder.tituloReceta != null) {
                holder.tituloReceta.setText(receta.getTitulo() != null ? receta.getTitulo() : contexto.getString(R.string.recipe_no_title));
            }

            // Configurar tiempo de preparación
            if (holder.tiempoPrep != null) {
                holder.tiempoPrep.setText(formatTiempo(receta.getTiempoPreparacion()));
            }

            // Configurar categoría y consumo habitual
            if (holder.dificultadCategoria != null) {
                String categoria = receta.getCategoria() != null ? receta.getCategoria() : contexto.getString(R.string.recipe_no_category);
                String consumo = receta.getConsumoHabitual() != null ? receta.getConsumoHabitual() : "";
                String texto = consumo.isEmpty() ? categoria : categoria + " • " + consumo;
                holder.dificultadCategoria.setText(texto);
            }
        } catch (Exception e) {
            Log.e("RecetaAdapter", "Error en configurarInformacionBasica", e);
        }
    }

    /**
     * Configura la imagen de la receta usando Glide.
     * @param holder ViewHolder de la receta
     * @param receta Receta a configurar
     */
    private void configurarImagen(RecetaViewHolder holder, Receta receta) {
        if (receta.getImagenUrl() == null || receta.getImagenUrl().isEmpty() || 
                receta.getImagenUrl().equals("url_imagen_aqui")) {
            // Mostrar imagen por defecto si no hay URL válida
            holder.imagenReceta.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            // Cargar la imagen desde la URL con Glide y manejo de errores
            Glide.with(holder.itemView.getContext())
                    .load(receta.getImagenUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .fallback(android.R.drawable.ic_menu_gallery)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, 
                                Object model, 
                                com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, 
                                boolean isFirstResource) {
                            Log.e("RecetaAdapter", "Error al cargar imagen: " + receta.getImagenUrl(), e);
                            return false; // Permitir que Glide maneje el error
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, 
                                Object model, 
                                com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, 
                                com.bumptech.glide.load.DataSource dataSource, 
                                boolean isFirstResource) {
                            return false; // Permitir que Glide maneje el recurso
                        }
                    })
                    .into(holder.imagenReceta);
        }
    }

    /**
     * Configura la información adicional de la receta (usuario, categoría, semáforo, consumo).
     * @param holder ViewHolder de la receta
     * @param receta Receta a configurar
     */
    private void configurarInformacionAdicional(RecetaViewHolder holder, Receta receta) {
        // Configurar nombre de usuario
        if (receta.getNombreUsuario() != null && !receta.getNombreUsuario().isEmpty()) {
            holder.nombreUsuario.setText(contexto.getString(R.string.recipe_username_format,
                    receta.getNombreUsuario()));
        } else {
            holder.nombreUsuario.setText(contexto.getString(R.string.recipe_user_id_format,
                    receta.getIdUsuario()));
        }

        // Configurar semáforo nutricional
        int colorSemaforo = obtenerColorSemaforo(receta.getSemaforo());
        if (colorSemaforo != 0) {
            holder.semaforoDot.setVisibility(View.VISIBLE);
            GradientDrawable fondo = (GradientDrawable) holder.semaforoDot.getBackground().mutate();
            fondo.setColor(colorSemaforo);
        } else {
            holder.semaforoDot.setVisibility(View.GONE);
        }

        // Configurar valoración media
        Float media = receta.getMediaPuntuacionFloat();
        if (holder.rbMediaPuntuacion != null && holder.tvMediaPuntuacion != null) {
            if (media != null && media > 0) {
                holder.rbMediaPuntuacion.setRating(media);
                holder.tvMediaPuntuacion.setText(
                        String.format(java.util.Locale.getDefault(), "%.1f", media));
                holder.rbMediaPuntuacion.setVisibility(View.VISIBLE);
                holder.tvMediaPuntuacion.setVisibility(View.VISIBLE);
            } else {
                holder.rbMediaPuntuacion.setRating(0);
                holder.tvMediaPuntuacion.setText("");
                holder.rbMediaPuntuacion.setVisibility(View.INVISIBLE);
                holder.tvMediaPuntuacion.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Configura los listeners de clic para la receta y sus botones.
     * @param holder ViewHolder de la receta
     * @param receta Receta a configurar
     */
    private void configurarListeners(RecetaViewHolder holder, Receta receta) {
        // Listener para clic en toda la tarjeta
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecetaClick(receta);
            }
        });

        // Listener para botón de comentarios
        if (holder.btnComentarios != null) {
            holder.btnComentarios.setOnClickListener(v -> {
                if (comentarioListener != null) {
                    comentarioListener.onComentarioClick(receta);
                }
            });
        }

        // Listener para botón de valoración
        if (holder.btnResena != null) {
            holder.btnResena.setOnClickListener(v -> {
                if (valoracionListener != null) {
                    valoracionListener.onValoracionClick(receta);
                }
            });
        }
    }

    // ======================== MÉTODOS UTILITARIOS ========================
    
    /**
     * Obtiene la posición de una receta en la lista actual.
     * @param idReceta ID de la receta a buscar
     * @return Posición en la lista o -1 si no se encuentra
     */
    private int obtenerPosicionReceta(int idReceta) {
        for (int i = 0; i < listaRecetas.size(); i++) {
            if (listaRecetas.get(i).getIdReceta() == idReceta) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Formatea el tiempo de preparación en minutos a un formato legible.
     * @param minutos Tiempo en minutos
     * @return Cadena formateada (ej: "1 h 30 min", "45 min")
     */
    private String formatTiempo(int minutos) {
        if (minutos <= 0) return contexto.getString(R.string.recipe_time_not_available);
        int horas = minutos / 60;
        int minRestantes = minutos % 60;
        if (horas > 0) {
            if (minRestantes > 0) {
                return contexto.getString(R.string.time_hours_minutes_format, horas, minRestantes);
            } else {
                return contexto.getString(R.string.time_hours_format, horas);
            }
        } else {
            return contexto.getString(R.string.time_minutes_format, minutos);
        }
    }

    /**
     * Obtiene el color correspondiente al semáforo nutricional.
     * @param semaforo Valor del semáforo (rojo, naranja, amarillo, verde_claro, verde_oscuro)
     * @return Color del recurso o 0 si no es válido
     */
    private int obtenerColorSemaforo(String semaforo) {
        if (semaforo == null) {
            return 0;
        }
        switch (semaforo.toLowerCase()) {
            case "rojo":
                return ContextCompat.getColor(contexto, android.R.color.holo_red_dark);
            case "naranja":
                return ContextCompat.getColor(contexto, android.R.color.holo_orange_dark);
            case "amarillo":
                return ContextCompat.getColor(contexto, android.R.color.holo_orange_light);
            case "verde_claro":
                return ContextCompat.getColor(contexto, android.R.color.holo_green_light);
            case "verde_oscuro":
                return ContextCompat.getColor(contexto, android.R.color.holo_green_dark);
            default:
                return 0;
        }
    }

    // ======================== MÉTODOS OBLIGATORIOS DEL ADAPTADOR ========================
    
    /**
     * Devuelve el número total de recetas en la lista.
     * @return Número de recetas
     */
    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    // ======================== VIEWHOLDER ========================
    
    /**
     * ViewHolder que contiene las vistas para una receta individual.
     */
    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        // Vistas de información básica
        TextView tituloReceta;
        TextView tiempoPrep;
        TextView dificultadCategoria;
        ImageView imagenReceta;

        // Vistas de información adicional
        TextView nombreUsuario;
        View semaforoDot;
        ImageView btnResena;
        ImageView btnComentarios;
        RatingBar rbMediaPuntuacion;
        TextView tvMediaPuntuacion;

        /**
         * Constructor del ViewHolder.
         * @param itemView Vista del item de la receta
         */
        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar vistas de información básica
            tituloReceta = itemView.findViewById(R.id.recipe_title);
            tiempoPrep = itemView.findViewById(R.id.recipe_time);
            dificultadCategoria = itemView.findViewById(R.id.recipe_dificultad_categoria);
            imagenReceta = itemView.findViewById(R.id.iv_imagen_receta);

            // Inicializar vistas de información adicional
            nombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
            semaforoDot = itemView.findViewById(R.id.recipe_semaforo_dot);
            btnResena = itemView.findViewById(R.id.btn_megusta);
            btnComentarios = itemView.findViewById(R.id.btn_comentarios);
            rbMediaPuntuacion = itemView.findViewById(R.id.rb_media_puntuacion);
            tvMediaPuntuacion = itemView.findViewById(R.id.tv_media_puntuacion);

            // Validar que todas las vistas se encontraron correctamente
            if (tituloReceta == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista recipe_title");
            }
            if (tiempoPrep == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista recipe_time");
            }
            if (dificultadCategoria == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista recipe_dificultad_categoria");
            }
            if (imagenReceta == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista iv_imagen_receta");
            }
            if (nombreUsuario == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista tv_nombre_usuario");
            }
            if (semaforoDot == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista recipe_semaforo_dot");
            }
            if (btnResena == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista btn_megusta");
            }
            if (btnComentarios == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista btn_comentarios");
            }
        }
    }
}
