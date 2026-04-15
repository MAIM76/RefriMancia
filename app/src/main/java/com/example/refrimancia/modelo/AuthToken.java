package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class AuthToken {
    // Token JWT para autenticación en solicitudes posteriores
    @SerializedName("token")
    private String token;

    // Información del usuario autenticado
    @SerializedName("usuario")
    private Usuario usuario;

    // Constructor vacío (necesario para Gson)
    public AuthToken() {}

    // ============ GETTERS Y SETTERS ============
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
