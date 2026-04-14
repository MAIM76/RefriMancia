package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class AuthToken {
    @SerializedName("token")
    private String token;

    @SerializedName("usuario")
    private Usuario usuario;

    public AuthToken() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
