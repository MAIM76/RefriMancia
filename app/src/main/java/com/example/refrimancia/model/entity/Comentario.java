package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Entidad que representa un comentario en una receta.
 * Contiene información del autor, contenido, fecha y metadatos asociados.
 */
public class Comentario {
    // ======================== ATRIBUTOS PRINCIPALES ========================
    
    /** Identificador único del comentario */
    @SerializedName("id_comentario")
    private int idComentario;

    /** Identificador de la receta a la que pertenece el comentario */
    @SerializedName("id_receta")
    private int idReceta;

    /** Identificador del usuario autor del comentario */
    @SerializedName("id_usuario")
    private int idUsuario;
    
    // ======================== ATRIBUTOS DE CONTENIDO ========================
    
    /** Texto del comentario */
    @SerializedName("mensaje")
    private String texto;
    
    // ======================== ATRIBUTOS DE METADATOS ========================
    
    /** Fecha de creación del comentario */
    @SerializedName("fecha_creacion")
    private String fechaCreacion;

    /** Fecha del comentario (campo alternativo) */
    @SerializedName("fecha_comentario")
    private String fechaComentario;

    /** Nombre del usuario autor del comentario */
    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    /** URL de la foto de perfil del autor */
    @SerializedName(value = "url_foto_perfil", alternate = {"imagen_perfil"})
    private String urlFotoPerfil;

    // ======================== CONSTRUCTORES ========================
    
    /**
     * Constructor vacío requerido para Gson/JSON parsing.
     */
    public Comentario() {
    }

    /**
     * Constructor para comentarios con información básica.
     * @param idComentario ID del comentario
     * @param idReceta ID de la receta
     * @param idUsuario ID del usuario autor
     * @param texto Contenido del comentario
     * @param fechaCreacion Fecha de creación
     */
    public Comentario(int idComentario, int idReceta, int idUsuario, String texto,
            String fechaCreacion) {
        this.idComentario = idComentario;
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.texto = texto;
        this.fechaCreacion = fechaCreacion;
    }

    // ======================== GETTERS Y SETTERS BÁSICOS ========================
    
    /** @return ID único del comentario */
    public int getIdComentario() { return idComentario; }
    
    /** @param idComentario Nuevo ID del comentario */
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }

    /** @return ID de la receta asociada */
    public int getIdReceta() { return idReceta; }
    
    /** @param idReceta Nuevo ID de la receta */
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    /** @return ID del usuario autor */
    public int getIdUsuario() { return idUsuario; }
    
    /** @param idUsuario Nuevo ID del usuario */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /** @return Nombre del usuario autor */
    public String getNombreUsuario() { return nombreUsuario; }
    
    /** @param nombreUsuario Nuevo nombre del usuario */
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    /** @return URL de la foto de perfil */
    public String getUrlFotoPerfil() { return urlFotoPerfil; }
    
    /** @param urlFotoPerfil Nueva URL de foto de perfil */
    public void setUrlFotoPerfil(String urlFotoPerfil) { this.urlFotoPerfil = urlFotoPerfil; }
    
    // ======================== GETTERS Y SETTERS DE CONTENIDO ========================

    /** @return Texto del comentario */
    public String getTexto() { return texto != null ? texto : ""; }

    /** @param texto Nuevo texto del comentario */
    public void setTexto(String texto) { this.texto = texto; }

    /** @return Fecha del comentario (alternativa) */
    public String getFechaComentario() { return fechaComentario; }
    
    /** @param fechaComentario Nueva fecha del comentario */
    public void setFechaComentario(String fechaComentario) { this.fechaComentario = fechaComentario; }

    /** @return Fecha de creación del comentario */
    public String getFechaCreacion() { return fechaCreacion; }
    
    /** @param fechaCreacion Nueva fecha de creación */
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // ======================== MÉTODOS UTILITARIOS ========================
    
    /**
     * Obtiene la fecha normalizada con fallback entre campos.
     * Prioriza 'fechaCreacion' sobre 'fechaComentario'.
     * @return Fecha normalizada o cadena vacía si ambas son nulas
     */
    public String getFechaNormalizada() {
        if (fechaCreacion != null && !fechaCreacion.trim().isEmpty()) {
            return fechaCreacion;
        }
        return fechaComentario != null ? fechaComentario : "";
    }
}

