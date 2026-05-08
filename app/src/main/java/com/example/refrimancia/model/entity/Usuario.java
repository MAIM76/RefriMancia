package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Entidad que representa un usuario en la aplicación RefriMancia.
 * Contiene información personal, credenciales y metadatos del usuario.
 */
public class Usuario {
    // ======================== ATRIBUTOS PRINCIPALES ========================
    
    /** Identificador único del usuario */
    @SerializedName("id_usuario")
    private int idUsuario;

    /** Nombre de usuario único para login */
    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    /** Correo electrónico del usuario */
    @SerializedName("correo_electronico")
    private String correoElectronico;

    /** Contraseña del usuario (encriptada) */
    @SerializedName("contrasena")
    private String contrasena;
    
    // ======================== ATRIBUTOS DE PERFIL ========================
    
    /** Nombre completo del usuario */
    @SerializedName("nombre_completo")
    private String nombreCompleto;

    /** URL de la foto de perfil */
    @SerializedName(value = "url_foto_perfil", alternate = {"imagen_perfil"})
    private String urlFotoPerfil;

    /** Fecha de nacimiento del usuario */
    @SerializedName("fecha_nac")
    private String fechaNac;
    
    // ======================== ATRIBUTOS DE SISTEMA ========================
    
    /** Rol del usuario en el sistema */
    @SerializedName("rol")
    private String rol;

    /** Fecha de registro en el sistema */
    @SerializedName("fecha_registro")
    private String fechaRegistro;

    /** Indica si el usuario está activo (1=activo, 0=inactivo) */
    @SerializedName("is_active")
    private Integer isActive;

    /** Último token de autenticación generado */
    @SerializedName("ultimo_token")
    private String ultimoToken;

    /** Código de verificación para email/telefono */
    @SerializedName("codigo_verificacion")
    private String codigoVerificacion;

    // ======================== CONSTRUCTORES ========================
    
    /**
     * Constructor vacío requerido para Gson/JSON parsing.
     */
    public Usuario() {
    }

    /**
     * Constructor básico para usuarios.
     * @param nombreUsuario Nombre de usuario único
     * @param correoElectronico Correo electrónico válido
     */
    public Usuario(String nombreUsuario, String correoElectronico) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
    }

    /**
     * Constructor para usuarios con información de sistema.
     * @param idUsuario ID del usuario
     * @param nombreUsuario Nombre de usuario
     * @param correoElectronico Correo electrónico
     * @param rol Rol en el sistema
     * @param fechaRegistro Fecha de registro
     * @param isActive Estado de actividad
     */
    public Usuario(int idUsuario, String nombreUsuario, String correoElectronico, String rol, 
            String fechaRegistro, Integer isActive) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.isActive = isActive;
    }

    // ======================== GETTERS ========================
    
    /** @return ID único del usuario */
    public int getIdUsuario() { return idUsuario; }
    
    /** @return Nombre de usuario */
    public String getNombreUsuario() { return nombreUsuario; }
    
    /** @return Correo electrónico */
    public String getCorreoElectronico() { return correoElectronico; }
    
    /** @return Contraseña (encriptada) */
    public String getContrasena() { return contrasena; }
    
    /** @return Rol en el sistema */
    public String getRol() { return rol; }
    
    /** @return Fecha de registro */
    public String getFechaRegistro() { return fechaRegistro; }
    
    /** @return Estado de actividad (1=activo, 0=inactivo) */
    public Integer getIsActive() { return isActive; }
    
    /** @return URL de la foto de perfil */
    public String getUrlFotoPerfil() { return urlFotoPerfil; }
    
    /** @return Nombre completo */
    public String getNombreCompleto() { return nombreCompleto; }
    
    /** @return Fecha de nacimiento */
    public String getFechaNac() { return fechaNac; }
    
    /** @return Último token de autenticación */
    public String getUltimoToken() { return ultimoToken; }
    
    /** @return Código de verificación */
    public String getCodigoVerificacion() { return codigoVerificacion; }

    // ======================== SETTERS ========================
    
    /** @param idUsuario Nuevo ID del usuario */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    /** @param nombreUsuario Nuevo nombre de usuario */
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    /** @param correoElectronico Nuevo correo electrónico */
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
    
    /** @param contrasena Nueva contraseña (encriptada) */
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    
    /** @param rol Nuevo rol en el sistema */
    public void setRol(String rol) { this.rol = rol; }
    
    /** @param fechaRegistro Nueva fecha de registro */
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    /** @param isActive Nuevo estado de actividad */
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
    
    /** @param urlFotoPerfil Nueva URL de foto de perfil */
    public void setUrlFotoPerfil(String urlFotoPerfil) { this.urlFotoPerfil = urlFotoPerfil; }
    
    /** @param nombreCompleto Nuevo nombre completo */
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    /** @param fechaNac Nueva fecha de nacimiento */
    public void setFechaNac(String fechaNac) { this.fechaNac = fechaNac; }
    
    /** @param ultimoToken Nuevo último token */
    public void setUltimoToken(String ultimoToken) { this.ultimoToken = ultimoToken; }
    
    /** @param codigoVerificacion Nuevo código de verificación */
    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
}
