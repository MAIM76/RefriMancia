package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class ValoracionReceta {

    // ======================== ATRIBUTOS ========================

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    // ======================== GETTERS ========================

    public String getStatus() { return status; }
    public Data getData() { return data; }

    // ======================== CLASE INTERNA ========================

    public static class Data {

        // ======================== ATRIBUTOS ========================

        @SerializedName("total_votos")
        private int totalVotos;

        @SerializedName("nota_media")
        private float notaMedia;

        // ======================== GETTERS ========================

        public int getTotalVotos() { return totalVotos; }
        public float getNotaMedia() { return notaMedia; }
    }
}
