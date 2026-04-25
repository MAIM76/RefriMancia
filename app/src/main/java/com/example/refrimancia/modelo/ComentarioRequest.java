package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class ComentarioRequest {
    @SerializedName("id_receta")
    private final int idReceta;

    @SerializedName("mensaje")
    private final String mensaje;

    public ComentarioRequest(int idReceta, String mensaje) {
        this.idReceta = idReceta;
        this.mensaje = mensaje;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public String getMensaje() {
        return mensaje;
    }
}

