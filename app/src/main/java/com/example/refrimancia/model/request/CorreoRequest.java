package com.example.refrimancia.model.request;

import com.google.gson.annotations.SerializedName;

public class CorreoRequest {

    // ======================== ATRIBUTOS ========================

    @SerializedName("correo_electronico")
    private String correoElectronico;

    // ======================== CONSTRUCTORES ========================

    public CorreoRequest(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    // ======================== GETTERS Y SETTERS ========================

    public String getCorreoElectronico() {return correoElectronico;}
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}
