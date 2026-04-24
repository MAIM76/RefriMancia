package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RespuestaPaginada<T> {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<T> data;

    public String getStatus() {
        return status;
    }

    public List<T> getData() {
        return data;
    }
}
