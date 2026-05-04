package com.example.refrimancia.modelo.response;

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
    public String getMessage() { return message; }
    public int getId() { return id; }
    public String getFoto() { return foto; }
}
