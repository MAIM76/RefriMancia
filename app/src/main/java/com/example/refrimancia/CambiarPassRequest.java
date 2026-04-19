package com.example.refrimancia;

public class CambiarPassRequest {
    private String correo_electronico;
    private String codigo;
    private String nueva_contrasena;

    public CambiarPassRequest(String correo, String codigo, String nuevaPass) {
        this.correo_electronico = correo;
        this.codigo = codigo;
        this.nueva_contrasena = nuevaPass;
    }
    public String getCorreo_electronico() {return correo_electronico;}
    public void setCorreo_electronico(String correo_electronico) {this.correo_electronico = correo_electronico;}
    public String getCodigo() {return codigo;}
    public void setCodigo(String codigo) {this.codigo = codigo;}
    public String getNueva_contrasena() {return nueva_contrasena;}
    public void setNueva_contrasena(String nueva_contrasena) {this.nueva_contrasena = nueva_contrasena;}
}
