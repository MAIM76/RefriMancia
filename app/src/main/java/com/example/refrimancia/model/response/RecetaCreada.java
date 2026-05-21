package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class RecetaCreada {

    // ======================== ATRIBUTOS ========================

    @SerializedName("status")
    private String status;

    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("foto")
    private String foto;

    // ======================== GETTERS Y SETTERS ========================

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
