package com.example.refrimancia.modelo.response;

import com.example.refrimancia.modelo.entidad.Usuario;
import com.google.gson.annotations.SerializedName;

/**
 * Response del login de usuario.
 * Usa la entidad {@link Usuario} para los datos del usuario.
 *
 * @see com.example.refrimancia.LoginActivity
 * @see com.example.refrimancia.api.ApiService#login(com.example.refrimancia.modelo.request.LoginRequest)
 */
public class Login {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("data")
    private Usuario data;

    // ============ GETTERS Y SETTERS ============

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Usuario getData() { return data; }
    public void setData(Usuario data) { this.data = data; }

    /**
     * Obtiene el ID del usuario desde los datos.
     * @return ID del usuario o -1 si no hay datos
     */
    public int getIdUsuario() {
        return data != null ? data.getIdUsuario() : -1;
    }

    /**
     * Obtiene el nombre de usuario desde los datos.
     * @return Nombre de usuario o null si no hay datos
     */
    public String getNombreUsuario() {
        return data != null ? data.getNombreUsuario() : null;
    }
}
