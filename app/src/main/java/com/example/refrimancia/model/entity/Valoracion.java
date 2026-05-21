package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;

public class Valoracion {

    // ======================== ATRIBUTOS ========================

    @SerializedName("id_valoracion")
    private int idValoracion;

    @SerializedName("puntuacion")
    private int puntuacion;

    @SerializedName("fecha_valoracion")
    private String fechaValoracion;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("id_receta")
    private int idReceta;

    // ======================== CONSTRUCTORES ========================

    public Valoracion() {}

    // ======================== GETTERS Y SETTERS ========================

    public int getIdValoracion() { return idValoracion; }
    public void setIdValoracion(int idValoracion) { this.idValoracion = idValoracion; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public String getFechaValoracion() { return fechaValoracion; }
    public void setFechaValoracion(String fechaValoracion) { this.fechaValoracion = fechaValoracion; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
}

