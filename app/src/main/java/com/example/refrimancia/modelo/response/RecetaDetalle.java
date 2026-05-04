package com.example.refrimancia.modelo.response;

import com.example.refrimancia.modelo.entidad.Receta;
import com.google.gson.annotations.SerializedName;

public class RecetaDetalle {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Receta data;

    public String getStatus() { return status; }
    public Receta getData() { return data; }
}

