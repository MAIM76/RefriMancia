package com.example.refrimancia.model.response;

import com.google.gson.annotations.SerializedName;

public class ValoracionUsuario {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("ha_valorado")
        private boolean haValorado;

        @SerializedName("puntuacion")
        private int puntuacion;

        public boolean isHaValorado() { return haValorado; }
        public int getPuntuacion() { return puntuacion; }
    }
}
