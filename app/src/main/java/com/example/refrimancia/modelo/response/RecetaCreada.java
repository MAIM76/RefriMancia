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
    public void setStatus(String status) { this.status = status; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
