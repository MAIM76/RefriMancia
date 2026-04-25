package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class ValoracionRequest {
    @SerializedName("id_receta")
    private final int idReceta;

    @SerializedName("puntuacion")
    private final int puntuacion;

    public ValoracionRequest(int idReceta, int puntuacion) {
        this.idReceta = idReceta;
        this.puntuacion = puntuacion;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public int getPuntuacion() {
        return puntuacion;
    }
}

