package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Comentario {
    @SerializedName("id_comentario")
    private int idComentario;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("fecha_comentario")
    private String fechaComentario;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("id_receta")
    private int idReceta;

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
