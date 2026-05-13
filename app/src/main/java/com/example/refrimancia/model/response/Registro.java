package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class Registro {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private int id;

    @SerializedName("foto")
    private String foto;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
