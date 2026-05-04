package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

public class RespuestaUnica<T> {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private T data;

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}


