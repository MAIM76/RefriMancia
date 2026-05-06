package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response del registro de usuario.
 * Compatible con múltiples formatos de respuesta del backend.
 *
 * @see com.example.refrimancia.VentanaRegistro
 * @see com.example.refrimancia.VentanaEditarPerfil
 * @see com.example.refrimancia.api.ApiService
 */
public class Registro {

    // Campos formato 1: status/message
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Campos formato 2: exito/mensaje (usado en registro/actualización)
    @SerializedName("exito")
    private boolean exito;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("id")
    private int id;

    @SerializedName("foto")
    private String foto;

    // ============ GETTERS Y SETTERS ============

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
