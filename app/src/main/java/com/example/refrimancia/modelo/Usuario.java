package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    // Identificador único del usuario
    @SerializedName("id_usuario")
    private int idUsuario;

    // Nombre de usuario único para login
    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    // Email del usuario
    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("contrasena")
    private String contrasena;

    @SerializedName("rol")
    private String rol;

    @SerializedName("fecha_registro")
    private String fechaRegistro;

    @SerializedName("is_active")
    private Integer isActive;

    @SerializedName(value="url_foto_perfil", alternate={"imagen_perfil"})
    private String urlFotoPerfil;

    public Usuario() {
    }

    // Constructor con parámetros principales
    public Usuario(String nombreUsuario, String correoElectronico) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
    }

    public Usuario(int idUsuario, String nombreUsuario, String correoElectronico, String rol, String fechaRegistro, Integer isActive) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.isActive = isActive;
    }

    // ============ GETTERS ============
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public String getFechaRegistro() { return fechaRegistro; }
    public Integer getIsActive() { return isActive; }
    public String getUrlFotoPerfil() { return urlFotoPerfil; }

    // ============ SETTERS ============
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRol(String rol) { this.rol = rol; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    public void setUrlFotoPerfil(String urlFotoPerfil) { this.urlFotoPerfil = urlFotoPerfil; }
}
