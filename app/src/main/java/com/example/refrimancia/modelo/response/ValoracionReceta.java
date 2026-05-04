package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

public class ValoracionReceta {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("total_votos")
        private int totalVotos;

        @SerializedName("nota_media")
        private float notaMedia;

        public int getTotalVotos() { return totalVotos; }
        public float getNotaMedia() { return notaMedia; }
    }
}
