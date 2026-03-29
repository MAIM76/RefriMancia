package com.example.refrimancia;

public class LoginRequest {
    private String correo_electronico;
    private String contrasena;

    public LoginRequest(String correo_electronico, String contrasena) {
        this.correo_electronico = correo_electronico;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getCorreo_electronico() { return correo_electronico; }
    public void setCorreo_electronico(String correo_electronico) { this.correo_electronico = correo_electronico; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
