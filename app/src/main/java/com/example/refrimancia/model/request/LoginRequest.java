package com.example.refrimancia.model.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    // ======================== ATRIBUTOS ========================

    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("contrasena")
    private String contrasena;

    // ======================== CONSTRUCTORES ========================

    public LoginRequest() {
    }

    public LoginRequest(String correoElectronico, String contrasena) {
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
    }

    // ======================== GETTERS Y SETTERS ========================
    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
