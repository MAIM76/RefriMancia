package com.example.refrimancia.modelo.response;

import com.example.refrimancia.modelo.entidad.Usuario;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("data")
    private Usuario data;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Usuario getData() { return data; }
    public void setData(Usuario data) { this.data = data; }

    public int getIdUsuario() {
        return data != null ? data.getIdUsuario() : -1;
    }

    public String getNombreUsuario() {
        return data != null ? data.getNombreUsuario() : null;
    }
}
