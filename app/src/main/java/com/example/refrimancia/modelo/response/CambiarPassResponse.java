package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response del cambio de contraseña.
 *
 * @see com.example.refrimancia.VentanaRecuperarPassword
 * @see com.example.refrimancia.api.ApiService#cambiarPassword(com.example.refrimancia.modelo.request.CambiarPassRequest)
 *
 * Nota: Reemplaza a la clase anterior CambiarPassRespuesta (paquete raíz).
 */
public class CambiarPassResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // ============ GETTERS Y SETTERS ============

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
