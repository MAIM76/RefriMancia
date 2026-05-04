package com.example.refrimancia.modelo.request;

import com.google.gson.annotations.SerializedName;

public class CorreoRequest {
    @SerializedName("correo_electronico")
    private String correoElectronico;

    public CorreoRequest(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getCorreoElectronico() {return correoElectronico;}
    public void setCorreoElectronico(String correoElectronico) {this.correoElectronico = correoElectronico;}
}
