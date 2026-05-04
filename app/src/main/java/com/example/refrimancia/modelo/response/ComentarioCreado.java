package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

public class ComentarioCreado {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("id_comentario")
    private int idComentario;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public int getIdComentario() { return idComentario; }
}

