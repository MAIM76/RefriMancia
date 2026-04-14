package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("nombre_completo")
    private String nombreCompleto;

    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("imagen_perfil")
    private String imagenPerfil;

    @SerializedName("fecha_nac")
    private String fechaNacimiento;

    // Constructor vacío (necesario para Gson)
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String nombreUsuario, String nombreCompleto, String correoElectronico) {
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
    }

    // Getters
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getImagenPerfil() { return imagenPerfil; }
    public String getFechaNacimiento() { return fechaNacimiento; }

    // Setters
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public void setImagenPerfil(String imagenPerfil) { this.imagenPerfil = imagenPerfil; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
