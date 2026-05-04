package com.example.refrimancia.modelo.response;

import com.example.refrimancia.modelo.entidad.Receta;
import com.google.gson.annotations.SerializedName;

public class RecomendacionDiaria {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("desayuno")
        private Receta desayuno;

        @SerializedName("almuerzo")
        private Receta almuerzo;

        @SerializedName("comida")
        private Receta comida;

        @SerializedName("merienda")
        private Receta merienda;

        @SerializedName("cena")
        private Receta cena;

        @SerializedName("postre")
        private Receta postre;

        @SerializedName("snack")
        private Receta snack;

        public Receta getDesayuno() { return desayuno; }
        public Receta getAlmuerzo() { return almuerzo; }
        public Receta getComida() { return comida; }
        public Receta getMerienda() { return merienda; }
        public Receta getCena() { return cena; }
        public Receta getPostre() { return postre; }
        public Receta getSnack() { return snack; }
    }
}

