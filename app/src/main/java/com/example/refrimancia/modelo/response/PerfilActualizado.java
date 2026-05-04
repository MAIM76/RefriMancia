package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

public class PerfilActualizado {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("foto_actualizada")
        private String fotoActualizada;

        public String getFotoActualizada() { return fotoActualizada; }
    }
}

