package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

public class RecetaCreada {
    @SerializedName("status")
    private String status;

    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("foto")
    private String foto;

    public String getStatus() { return status; }
    public int getIdReceta() { return idReceta; }
    public String getFoto() { return foto; }
}

