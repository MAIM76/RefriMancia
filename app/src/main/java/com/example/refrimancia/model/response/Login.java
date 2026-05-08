package com.example.refrimancia.model.response;

import com.example.refrimancia.model.entity.Usuario;
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

    public String getImagenPerfil() {
        return data != null ? data.getUrlFotoPerfil() : null;
    }

    public String getNombreCompleto() {
        return data != null ? data.getNombreCompleto() : null;
    }

    public String getCorreoElectronico() {
        return data != null ? data.getCorreoElectronico() : null;
    }

    public String getFechaNac() {
        return data != null ? data.getFechaNac() : null;
    }
}
