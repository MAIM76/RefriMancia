package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    // Identificador único del usuario
    @SerializedName("id_usuario")
    private int idUsuario;

    // Nombre de usuario único para login
    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    // Nombre completo del usuario
    @SerializedName("nombre_completo")
    private String nombreCompleto;

    // Email del usuario
    @SerializedName("correo_electronico")
    private String correoElectronico;

    // URL de la imagen de perfil
    @SerializedName("imagen_perfil")
    private String imagenPerfil;

    // Fecha de nacimiento del usuario
    @SerializedName("fecha_nac")
    private String fechaNacimiento;

    // Constructor vacío (necesario para Gson)
    public Usuario() {
    }

    // Constructor con parámetros principales
    public Usuario(String nombreUsuario, String nombreCompleto, String correoElectronico) {
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
    }

    // ============ GETTERS ============
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getImagenPerfil() { return imagenPerfil; }
    public String getFechaNacimiento() { return fechaNacimiento; }

    // ============ SETTERS ============
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public void setImagenPerfil(String imagenPerfil) { this.imagenPerfil = imagenPerfil; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
