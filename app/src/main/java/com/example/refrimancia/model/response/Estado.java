package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class Estado {

    // ======================== ATRIBUTOS ========================

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // ======================== GETTERS ========================

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
