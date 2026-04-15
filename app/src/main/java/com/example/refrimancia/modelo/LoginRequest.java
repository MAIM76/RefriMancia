package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    // Email del usuario para autenticación
    @SerializedName("correo_electronico")
    private String correoElectronico;

    // Contraseña del usuario
    @SerializedName("contrasena")
    private String contrasena;

    // Constructor con parámetros
    public LoginRequest(String correoElectronico, String contrasena) {
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
    }

    // ============ GETTERS Y SETTERS ============
    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
