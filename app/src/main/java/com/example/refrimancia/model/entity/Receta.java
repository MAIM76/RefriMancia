package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Entidad que representa una receta en la aplicación RefriMancia.
 * Contiene toda la información necesaria para mostrar, crear y gestionar recetas
 * incluyendo ingredientes, instrucciones, valoraciones y metadatos.
 */
public class Receta implements Serializable {
    // ======================== ATRIBUTOS PRINCIPALES ========================
    
    /** Identificador único de la receta */
    @SerializedName("id_receta")
    private int idReceta;

    /** Identificador del usuario que creó la receta */
    @SerializedName("id_usuario")
    private int idUsuario;

    /** Nombre del usuario autor de la receta */
    @SerializedName(value = "nombre_usuario", alternate = {"autor"})
    private String nombreUsuario;

    /** URL de la imagen de la receta */
    @SerializedName(value = "imagen_receta", alternate = {"imagen"})
    private String imagenUrl;

    /** Fecha de publicación de la receta */
    @SerializedName("fecha_publicacion")
    private String fechaCreacion;
    
    // ======================== ATRIBUTOS DE CONTENIDO ========================
    
    /** Título de la receta */
    @SerializedName("titulo_receta")
    private String titulo;

    /** Descripción detallada de la receta */
    @SerializedName("descripcion")
    private String descripcion;

    /** Lista de ingredientes necesarios */
    @SerializedName("ingredientes")
    private String ingredientes;

    /** Instrucciones o pasos para preparar la receta */
    @SerializedName(value = "instrucciones", alternate = {"pasos"})
    private String pasos;
    
    // ======================== ATRIBUTOS DE METADATOS ========================
    
    /** Nivel de dificultad de la receta */
    @SerializedName("dificultad")
    private String dificultad;

    /** Categoría o tipo de receta */
    @SerializedName("tipo_receta")
    private String categoria;

    /** Tiempo de preparación en minutos */
    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    /** Número de porciones que rinde la receta */
    @SerializedName("porciones")
    private int porciones;

    /** Indica si la receta está aprobada por administradores */
    @SerializedName("aprobada")
    private Boolean aprobada;
    
    // ======================== ATRIBUTOS DE VALORACIÓN ========================
    
    /** Puntuación media de la receta (como String) */
    @SerializedName("media_puntuacion")
    private String mediaPuntuacion;
    
    // ======================== ATRIBUTOS ADICIONALES ========================
    
    /** Tipo de consumo habitual de la receta */
    @SerializedName("consumo_habitual")
    private String consumoHabitual;

    /** Clasificación de semáforo nutricional */
    @SerializedName("semaforo")
    private String semaforo;

    /** Lista de comentarios asociados a la receta */
    @SerializedName("comentarios")
    private java.util.List<Comentario> comentarios;

    // ======================== CONSTRUCTORES ========================
    
    /**
     * Constructor vacío requerido para Gson/JSON parsing.
     */
    public Receta() {
    }

    /**
     * Constructor simplificado para crear recetas básicas.
     * @param titulo Título de la receta
     * @param descripcion Descripción de la receta
     */
    public Receta(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    /**
     * Constructor para recetas con información básica.
     * @param idReceta ID de la receta
     * @param titulo Título de la receta
     * @param descripcion Descripción de la receta
     * @param pasos Instrucciones de preparación
     * @param tiempoPreparacion Tiempo en minutos
     * @param idUsuario ID del autor
     * @param imagenUrl URL de la imagen
     * @param fechaCreacion Fecha de publicación
     */
    public Receta(int idReceta, String titulo, String descripcion, String pasos, int tiempoPreparacion, 
            int idUsuario, String imagenUrl, String fechaCreacion) {
        this.idReceta = idReceta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.pasos = pasos;
        this.tiempoPreparacion = tiempoPreparacion;
        this.idUsuario = idUsuario;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Constructor completo para recetas con toda la información.
     * @param idReceta ID de la receta
     * @param titulo Título de la receta
     * @param descripcion Descripción de la receta
     * @param pasos Instrucciones de preparación
     * @param tiempoPreparacion Tiempo en minutos
     * @param dificultad Nivel de dificultad
     * @param categoria Categoría de la receta
     * @param idUsuario ID del autor
     * @param imagenUrl URL de la imagen
     * @param fechaCreacion Fecha de publicación
     */
    public Receta(int idReceta, String titulo, String descripcion, String pasos, int tiempoPreparacion, 
            String dificultad, String categoria, int idUsuario, String imagenUrl, String fechaCreacion) {
        this.idReceta = idReceta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.pasos = pasos;
        this.tiempoPreparacion = tiempoPreparacion;
        this.dificultad = dificultad;
        this.categoria = categoria;
        this.idUsuario = idUsuario;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    // ======================== GETTERS ========================
    
    /** @return ID único de la receta */
    public int getIdReceta() { return idReceta; }
    
    /** @return ID del usuario autor */
    public int getIdUsuario() { return idUsuario; }
    
    /** @return Nombre del usuario autor */
    public String getNombreUsuario() { return nombreUsuario; }
    
    /** @return Título de la receta */
    public String getTitulo() { return titulo; }
    
    /** @return Descripción de la receta */
    public String getDescripcion() { return descripcion; }
    
    /** @return Ingredientes de la receta */
    public String getIngredientes() { return ingredientes; }
    
    /** @return Instrucciones de preparación */
    public String getPasos() { return pasos; }
    
    /** @return Tiempo de preparación en minutos */
    public int getTiempoPreparacion() { return tiempoPreparacion; }
    
    /** @return Número de porciones */
    public int getPorciones() { return porciones; }
    
    /** @return Nivel de dificultad */
    public String getDificultad() { return dificultad; }
    
    /** @return Categoría de la receta */
    public String getCategoria() { return categoria; }
    
    /** @return Fecha de creación */
    public String getFechaCreacion() { return fechaCreacion; }
    
    /** @return URL de la imagen */
    public String getImagenUrl() { return imagenUrl; }
    
    /** @return Estado de aprobación */
    public Boolean getAprobada() { return aprobada; }
    
    /** @return Puntuación media como String */
    public String getMediaPuntuacion() { return mediaPuntuacion; }
    
    /** @return Tipo de consumo habitual */
    public String getConsumoHabitual() { return consumoHabitual; }
    
    /** @return Clasificación de semáforo nutricional */
    public String getSemaforo() { return semaforo; }
    
    /** @return Lista de comentarios */
    public java.util.List<Comentario> getComentarios() { return comentarios; }
    
    /**
     * Obtiene la puntuación media como Float.
     * Convierte el String a Float de forma segura.
     * @return Puntuación media como Float o null si no es válido
     */
    public Float getMediaPuntuacionFloat() {
        if (mediaPuntuacion == null || mediaPuntuacion.trim().isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(mediaPuntuacion);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ======================== SETTERS ========================
    
    /** @param idReceta Nuevo ID de la receta */
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    
    /** @param idUsuario Nuevo ID del usuario autor */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    /** @param nombreUsuario Nuevo nombre del usuario autor */
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    /** @param titulo Nuevo título de la receta */
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    /** @param descripcion Nueva descripción de la receta */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    /** @param ingredientes Nuevos ingredientes de la receta */
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }
    
    /** @param pasos Nuevas instrucciones de preparación */
    public void setPasos(String pasos) { this.pasos = pasos; }
    
    /** @param tiempoPreparacion Nuevo tiempo de preparación en minutos */
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
    
    /** @param porciones Nuevo número de porciones */
    public void setPorciones(int porciones) { this.porciones = porciones; }

    /** @param dificultad Nuevo nivel de dificultad */
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    /** @param categoria Nueva categoría de la receta */
    public void setCategoria(String categoria) { this.categoria = categoria; }

    /** @param aprobada Nuevo estado de aprobación */
    public void setAprobada(Boolean aprobada) { this.aprobada = aprobada; }
    
    /** @param imagenUrl Nueva URL de la imagen */
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    /** @param fechaCreacion Nueva fecha de creación */
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    /** @param mediaPuntuacion Nueva puntuación media */
    public void setMediaPuntuacion(String mediaPuntuacion) { this.mediaPuntuacion = mediaPuntuacion; }
    
    /** @param consumoHabitual Nuevo tipo de consumo habitual */
    public void setConsumoHabitual(String consumoHabitual) { this.consumoHabitual = consumoHabitual; }
    
    /** @param semaforo Nueva clasificación de semáforo nutricional */
    public void setSemaforo(String semaforo) { this.semaforo = semaforo; }
    
    /** @param comentarios Nueva lista de comentarios */
    public void setComentarios(java.util.List<Comentario> comentarios) { this.comentarios = comentarios; }
}
