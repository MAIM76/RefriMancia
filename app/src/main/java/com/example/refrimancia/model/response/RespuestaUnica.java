package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class RespuestaUnica<T> {

    // ======================== ATRIBUTOS ========================

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private T data;

    // ======================== GETTERS ========================

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}


