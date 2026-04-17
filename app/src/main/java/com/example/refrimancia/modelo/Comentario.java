package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Comentario {
    // Identificador único del comentario
    @SerializedName("id_comentario")
    private int idComentario;

    // Contenido del mensaje del comentario
    @SerializedName("mensaje")
    private String mensaje;

    // Fecha en que se escribió el comentario
    @SerializedName("fecha_comentario")
    private String fechaComentario;

    // ID del usuario que escribió el comentario
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("fecha_creacion")
    private String fechaCreacion;

    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName(value="url_foto_perfil", alternate={"imagen_perfil"})
    private String urlFotoPerfil;

    public Comentario() {
    }

    public Comentario(int idComentario, int idReceta, int idUsuario, String contenido, String fechaCreacion) {
        this.idComentario = idComentario;
        this.idReceta = idReceta;
        this.idUsuario = idUsuario;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdComentario() { return idComentario; }
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getFechaComentario() { return fechaComentario; }
    public void setFechaComentario(String fechaComentario) { this.fechaComentario = fechaComentario; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }
}
