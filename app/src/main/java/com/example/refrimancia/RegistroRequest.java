package com.example.refrimancia;

import java.util.Date;

public class RegistroRequest { //OBSOLETA, POSIBLEMENTE SE TIENE QUE QUITAR
    private String nombre_usuario;
    private String contrasena;
    private String correo_electronico;
    private String nombre_completo;
    private Date fecha_nac;
    private String imagen_perfil;

    public RegistroRequest(String nombre_usuario, String contrasena, String correo_electronico, String nombre_completo, Date fecha_nac, String imagen_perfil) {
        this.nombre_usuario = nombre_usuario;
        this.contrasena = contrasena;
        this.correo_electronico = correo_electronico;
        this.nombre_completo = nombre_completo;
        this.fecha_nac = fecha_nac;
        this.imagen_perfil = imagen_perfil;
    }

    public String getNombre_usuario() {return nombre_usuario;}
    public void setNombre_usuario(String nombre_usuario) {this.nombre_usuario = nombre_usuario;}
    public String getContrasena() {return contrasena;}
    public void setContrasena(String contrasena) {this.contrasena = contrasena;}
    public String getCorreo_electronico() {return correo_electronico;}
    public void setCorreo_electronico(String correo_electronico) {this.correo_electronico = correo_electronico;}
    public String getNombre_completo() {return nombre_completo;}
    public void setNombre_completo(String nombre_completo) {this.nombre_completo = nombre_completo;}
    public Date getFecha_nac() {return fecha_nac;}
    public void setFecha_nac(Date fecha_nac) {this.fecha_nac = fecha_nac;}
    public String getImagen_perfil() {return imagen_perfil;}
    public void setImagen_perfil(String imagen_perfil) {this.imagen_perfil = imagen_perfil;}
}
