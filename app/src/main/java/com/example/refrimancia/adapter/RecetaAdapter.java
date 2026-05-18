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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.refrimancia.R;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ValoracionService;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.response.ValoracionReceta;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        final List<Receta> listaAnterior = new ArrayList<>(this.listaRecetas);
        final List<Receta> listaNueva = nuevasRecetas != null ? nuevasRecetas : new ArrayList<>();
        this.listaRecetas.clear();
        this.listaRecetas.addAll(listaNueva);
        this.listaRecetasCompleta.clear();
        this.listaRecetasCompleta.addAll(listaNueva);
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return listaAnterior.size(); }
            @Override public int getNewListSize() { return listaNueva.size(); }
            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return listaAnterior.get(oldPos).getIdReceta() == listaNueva.get(newPos).getIdReceta();
            }
            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                Receta a = listaAnterior.get(oldPos);
                Receta b = listaNueva.get(newPos);
                return java.util.Objects.equals(a.getTitulo(), b.getTitulo())
                        && java.util.Objects.equals(a.getMediaPuntuacion(), b.getMediaPuntuacion())
                        && java.util.Objects.equals(a.getImagenUrl(), b.getImagenUrl())
                        && java.util.Objects.equals(a.getCategoria(), b.getCategoria())
                        && java.util.Objects.equals(a.getSemaforo(), b.getSemaforo())
                        && java.util.Objects.equals(a.getConsumoHabitual(), b.getConsumoHabitual());
            }
        }).dispatchUpdatesTo(this);
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
        ValoracionService valoracionService =
                ClienteRetrofit.obtenerInstancia(contexto).create(ValoracionService.class);
        valoracionService.obtenerValoracionesPorReceta(idReceta).enqueue(new Callback<ValoracionReceta>() {
            @Override
            public void onResponse(@NonNull Call<ValoracionReceta> call,
                    @NonNull Response<ValoracionReceta> response) {
                int posicion = obtenerPosicionReceta(idReceta);
                if (posicion == -1) return;
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    float nuevaMedia = response.body().getData().getNotaMedia();
                    listaRecetas.get(posicion).setMediaPuntuacion(
                            String.valueOf(nuevaMedia));
                    if (posicion < listaRecetasCompleta.size()) {
                        int posCompleta = -1;
                        for (int i = 0; i < listaRecetasCompleta.size(); i++) {
                            if (listaRecetasCompleta.get(i).getIdReceta() == idReceta) {
                                posCompleta = i;
                                break;
                            }
                        }
                        if (posCompleta != -1) {
                            listaRecetasCompleta.get(posCompleta).setMediaPuntuacion(
                                    String.valueOf(nuevaMedia));
                        }
                    }
                }
                notifyItemChanged(posicion);
            }

            @Override
            public void onFailure(@NonNull Call<ValoracionReceta> call, @NonNull Throwable t) {
                int posicion = obtenerPosicionReceta(idReceta);
                if (posicion != -1) notifyItemChanged(posicion);
            }
        });
    }

    /**
     * Configura la información básica de la receta (título, tiempo, dificultad, categoría).
     * @param holder ViewHolder de la receta
     * @param receta Receta a configurar
     */
    private void configurarInformacionBasica(RecetaViewHolder holder, Receta receta) {
        try {
            // Configurar título
            if (holder.textoTitulo != null) {
                holder.textoTitulo.setText(receta.getTitulo() != null ? receta.getTitulo() : contexto.getString(R.string.recipe_no_title));
            }

            // Configurar fecha de creación
            if (holder.textoFechaPublicacion != null) {
                holder.textoFechaPublicacion.setText(formatFecha(receta.getFechaCreacion()));
            }

            // Configurar tipo de comida | tiempo (fila 2)
            if (holder.textoTipoTiempo != null) {
                String rawCategoria = receta.getCategoria();
                boolean categoriaVacia = rawCategoria == null || rawCategoria.trim().isEmpty()
                        || rawCategoria.equalsIgnoreCase("No disponible");
                String categoria = categoriaVacia
                        ? contexto.getString(R.string.recipe_calculating)
                        : rawCategoria;
                String tiempo = formatTiempo(receta.getTiempoPreparacion());
                holder.textoTipoTiempo.setText(categoria + "  •  " + tiempo);
            }

            // Configurar consumo habitual (fila 3) – siempre visible con estado
            if (holder.textoConsumoHabitual != null) {
                String consumo = receta.getConsumoHabitual();
                boolean consumoVacio = consumo == null || consumo.trim().isEmpty()
                        || consumo.equalsIgnoreCase("No disponible");
                holder.textoConsumoHabitual.setText(consumoVacio
                        ? contexto.getString(R.string.recipe_calculating)
                        : consumo);
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
            holder.textoNombreUsuario.setText(contexto.getString(R.string.recipe_username_format,
                    receta.getNombreUsuario()));
        } else {
            holder.textoNombreUsuario.setText(contexto.getString(R.string.recipe_user_id_format,
                    receta.getIdUsuario()));
        }

        // Configurar semáforo nutricional
        int colorSemaforo = obtenerColorSemaforo(receta.getSemaforo());
        if (colorSemaforo != 0) {
            holder.indicadorSemaforo.setVisibility(View.VISIBLE);
            GradientDrawable fondo = (GradientDrawable) holder.indicadorSemaforo.getBackground().mutate();
            fondo.setColor(colorSemaforo);
        } else {
            holder.indicadorSemaforo.setVisibility(View.GONE);
        }

        // Configurar valoración media
        Float media = receta.getMediaPuntuacionFloat();
        if (holder.barraValoracion != null && holder.textoValoracion != null) {
            if (media != null && media > 0) {
                holder.barraValoracion.setRating(media);
                holder.textoValoracion.setText(
                        String.format(java.util.Locale.getDefault(), "%.1f", media));
                holder.barraValoracion.setVisibility(View.VISIBLE);
                holder.textoValoracion.setVisibility(View.VISIBLE);
            } else {
                holder.barraValoracion.setRating(0);
                holder.textoValoracion.setText("");
                holder.barraValoracion.setVisibility(View.INVISIBLE);
                holder.textoValoracion.setVisibility(View.INVISIBLE);
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
        if (holder.botonComentarios != null) {
            holder.botonComentarios.setOnClickListener(v -> {
                if (comentarioListener != null) {
                    comentarioListener.onComentarioClick(receta);
                }
            });
        }

        // Listener para botón de valoración
        if (holder.botonValorar != null) {
            holder.botonValorar.setOnClickListener(v -> {
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

    private String formatFecha(String fechaCreacion) {
        if (fechaCreacion == null || fechaCreacion.isEmpty()) return "";
        String[] formatos = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss"
        };
        java.text.SimpleDateFormat salida = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        for (String fmt : formatos) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(fmt, java.util.Locale.getDefault());
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date fecha = sdf.parse(fechaCreacion);
                if (fecha != null) return salida.format(fecha);
            } catch (java.text.ParseException ignored) {}
        }
        return fechaCreacion.length() >= 10 ? fechaCreacion.substring(0, 10) : fechaCreacion;
    }

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
        }
        return contexto.getString(R.string.time_minutes_format, minutos);
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
        TextView textoTitulo;
        TextView textoFechaPublicacion;
        TextView textoTipoTiempo;
        TextView textoConsumoHabitual;
        ImageView imagenReceta;

        // Vistas de información adicional
        TextView textoNombreUsuario;
        View indicadorSemaforo;
        ImageView botonValorar;
        ImageView botonComentarios;
        RatingBar barraValoracion;
        TextView textoValoracion;

        /**
         * Constructor del ViewHolder.
         * @param itemView Vista del item de la receta
         */
        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar vistas de información básica
            textoTitulo = itemView.findViewById(R.id.texto_titulo);
            textoFechaPublicacion = itemView.findViewById(R.id.texto_fecha_publicacion);
            textoTipoTiempo = itemView.findViewById(R.id.texto_tipo_tiempo);
            textoConsumoHabitual = itemView.findViewById(R.id.texto_consumo_habitual);
            imagenReceta = itemView.findViewById(R.id.imagen_receta);

            // Inicializar vistas de información adicional
            textoNombreUsuario = itemView.findViewById(R.id.texto_nombre_usuario);
            indicadorSemaforo = itemView.findViewById(R.id.indicador_semaforo);
            botonValorar = itemView.findViewById(R.id.boton_valorar);
            botonComentarios = itemView.findViewById(R.id.boton_comentarios);
            barraValoracion = itemView.findViewById(R.id.barra_valoracion);
            textoValoracion = itemView.findViewById(R.id.texto_valoracion);

            // Validar que todas las vistas se encontraron correctamente
            if (textoTitulo == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista texto_titulo");
            }
            if (textoTipoTiempo == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista texto_tipo_tiempo");
            }
            if (imagenReceta == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista imagen_receta");
            }
            if (textoNombreUsuario == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista texto_nombre_usuario");
            }
            if (indicadorSemaforo == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista indicador_semaforo");
            }
            if (botonValorar == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista boton_valorar");
            }
            if (botonComentarios == null) {
                Log.e("RecetaViewHolder", "No se encontró la vista boton_comentarios");
            }
        }
    }
}
