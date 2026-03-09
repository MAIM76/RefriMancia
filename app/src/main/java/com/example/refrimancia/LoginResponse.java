package com.example.refrimancia;

public class LoginResponse {
    private String mensaje;
    private boolean exito;

    // Puedes añadir más campos según lo que devuelva tu API (token, id_usuario, etc.)
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
}
