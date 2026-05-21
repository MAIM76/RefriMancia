package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class ValoracionUsuario {

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

        @SerializedName("ha_valorado")
        private boolean haValorado;

        @SerializedName("puntuacion")
        private int puntuacion;

        // ======================== GETTERS ========================

        public boolean isHaValorado() { return haValorado; }
        public int getPuntuacion() { return puntuacion; }
    }
}
