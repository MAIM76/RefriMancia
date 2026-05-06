package com.example.refrimancia.modelo.request;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Request para registrar un nuevo usuario.
 *
 * @deprecated Esta clase está obsoleta. Usar el endpoint de registro con multipart
 *             directamente en {@link com.example.refrimancia.VentanaRegistro}.
 *
 * @see com.example.refrimancia.VentanaRegistro
 */
@Deprecated
public class RegistroRequest {

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("contrasena")
    private String contrasena;

    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("nombre_completo")
    private String nombreCompleto;

    @SerializedName("fecha_nac")
    private Date fechaNac;

    @SerializedName("imagen_perfil")
    private String imagenPerfil;

    /**
     * Constructor con parámetros.
     */
    public RegistroRequest(String nombreUsuario, String contrasena, String correoElectronico,
                           String nombreCompleto, Date fechaNac, String imagenPerfil) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.correoElectronico = correoElectronico;
        this.nombreCompleto = nombreCompleto;
        this.fechaNac = fechaNac;
        this.imagenPerfil = imagenPerfil;
    }

    // ============ GETTERS Y SETTERS ============

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Date getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(Date fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
