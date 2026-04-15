package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Valoracion {
    // Identificador único de la valoración
    @SerializedName("id_valoracion")
    private int idValoracion;

    // Puntuación numérica (generalmente de 1 a 5)
    @SerializedName("puntuacion")
    private int puntuacion;

    // Fecha en que se realizó la valoración
    @SerializedName("fecha_valoracion")
    private String fechaValoracion;

    // ID del usuario que realizó la valoración
    @SerializedName("id_usuario")
    private int idUsuario;

    // ID de la receta que fue valorada
    @SerializedName("id_receta")
    private int idReceta;

    // Constructor vacío (necesario para Gson)
    public Valoracion() {}

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
