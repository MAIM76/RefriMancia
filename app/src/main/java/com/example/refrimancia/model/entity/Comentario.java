package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;

public class Comentario {
    // ======================== ATRIBUTOS PRINCIPALES ========================
    
    @SerializedName("id_comentario")
    private int idComentario;

    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("id_usuario")
    private int idUsuario;
    
    // ======================== ATRIBUTOS DE CONTENIDO ========================
    
    @SerializedName("mensaje")
    private String texto;
    
    // ======================== ATRIBUTOS DE METADATOS ========================
    
    @SerializedName("fecha_creacion")
    private String fechaCreacion;

    @SerializedName("fecha_comentario")
    private String fechaComentario;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName(value = "url_foto_perfil", alternate = {"imagen_perfil"})
    private String urlFotoPerfil;

    // ======================== CONSTRUCTORES ========================
    
    public Comentario() {
    }

    public Comentario(int idComentario, int idReceta, int idUsuario, String texto,
            String fechaCreacion) {
        this.idComentario = idComentario;
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.texto = texto;
        this.fechaCreacion = fechaCreacion;
    }

    // ======================== GETTERS Y SETTERS BÁSICOS ========================
    
    public int getIdComentario() { return idComentario; }
    
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }

    public int getIdReceta() { return idReceta; }
    
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public int getIdUsuario() { return idUsuario; }
    
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getUrlFotoPerfil() { return urlFotoPerfil; }
    
    public void setUrlFotoPerfil(String urlFotoPerfil) { this.urlFotoPerfil = urlFotoPerfil; }
    
    // ======================== GETTERS Y SETTERS DE CONTENIDO ========================

    public String getTexto() { return texto != null ? texto : ""; }

    public void setTexto(String texto) { this.texto = texto; }

    public String getFechaComentario() { return fechaComentario; }
    
    public void setFechaComentario(String fechaComentario) { this.fechaComentario = fechaComentario; }

    public String getFechaCreacion() { return fechaCreacion; }
    
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // ======================== MÉTODOS UTILITARIOS ========================
    
    // Prioriza 'fechaCreacion' sobre 'fechaComentario'; devuelve "" si ambas son nulas
    public String getFechaNormalizada() {
        if (fechaCreacion != null && !fechaCreacion.trim().isEmpty()) {
            return fechaCreacion;
        }
        return fechaComentario != null ? fechaComentario : "";
    }
}

