package com.example.refrimancia.model.request;

import com.google.gson.annotations.SerializedName;

public class ComentarioRequest {

    // ======================== ATRIBUTOS ========================

    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("mensaje")
    private String mensaje;

    // ======================== CONSTRUCTORES ========================

    public ComentarioRequest() {
    }

    public ComentarioRequest(int idReceta, String mensaje) {
        this.idReceta = idReceta;
        this.mensaje = mensaje;
    }

    // ======================== GETTERS Y SETTERS ========================

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
