package com.example.refrimancia.model.request;

import com.google.gson.annotations.SerializedName;

public class CambiarPassRequest {

    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("nueva_contrasena")
    private String nuevaContrasena;

    public CambiarPassRequest() {
    }

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
