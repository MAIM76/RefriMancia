package com.example.refrimancia.modelo.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request para cambiar la contraseña de un usuario.
 * Usado en el flujo de recuperación de contraseña.
 *
 * @see com.example.refrimancia.ui.VentanaRecuperarPassword
 */
public class CambiarPassRequest {

    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("nueva_contrasena")
    private String nuevaContrasena;

    /**
     * Constructor vacío requerido para Retrofit/Gson.
     */
    public CambiarPassRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param correo Correo electrónico del usuario
     * @param codigo Código de verificación recibido
     * @param nuevaPass Nueva contraseña
     */
    public CambiarPassRequest(String correo, String codigo, String nuevaPass) {
        this.correoElectronico = correo;
        this.codigo = codigo;
        this.nuevaContrasena = nuevaPass;
    }

    // ============ GETTERS Y SETTERS ============

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}
