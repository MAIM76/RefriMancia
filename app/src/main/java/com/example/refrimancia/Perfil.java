package com.example.refrimancia;

import com.google.gson.annotations.SerializedName;

public class Perfil {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre_usuario")
    private String nombre_usuario;

    @SerializedName("imagen_perfil")
    private String imagen_perfil;

    @SerializedName("nombre_completo")
    private String nombre_completo;

    @SerializedName("fecha_nac")
    private String fecha_nac;

    @SerializedName("correo_electronico")
    private String correo_electronico;

    // GETTERS

    public int getIdUsuario() {
        return idUsuario;
    }
    public String getNombreUsuario() {
        return nombre_usuario;
    }
    public String getImagenPerfil() {
        return imagen_perfil;
    }
    public String getNombreCompleto() {
        return nombre_completo;
    }
    public String getFechaNac() {
        return fecha_nac;
    }
    public String getCorreoElectronico() {
        return correo_electronico;
    }
}