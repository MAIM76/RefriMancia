package com.example.refrimancia.model.request;

import com.google.gson.annotations.SerializedName;

public class ValoracionRequest {

    // ======================== ATRIBUTOS ========================

    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("puntuacion")
    private int puntuacion;

    // ======================== CONSTRUCTORES ========================

    public ValoracionRequest() {
    }

    public ValoracionRequest(int idReceta, int puntuacion) {
        this.idReceta = idReceta;
        this.puntuacion = puntuacion;
    }

    // ======================== GETTERS Y SETTERS ========================

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
}
