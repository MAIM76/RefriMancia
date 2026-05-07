package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

public class Registro {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("exito")
    private boolean exito;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("id")
    private int id;

    @SerializedName("foto")
    private String foto;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
