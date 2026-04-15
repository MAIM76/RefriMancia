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

    // ID de la receta en la que se escribió el comentario
    @SerializedName("id_receta")
    private int idReceta;

    // Constructor vacío (necesario para Gson)
    public Comentario() {}

    public int getIdComentario() { return idComentario; }
    public void setIdComentario(int idComentario) { this.idComentario = idComentario; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getFechaComentario() { return fechaComentario; }
    public void setFechaComentario(String fechaComentario) { this.fechaComentario = fechaComentario; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
}
