package com.example.refrimancia;

public class RegistroRespuesta {
    private String mensaje;
    private boolean exito;
    private int id;
    private String foto;

    public RegistroRespuesta(String mensaje, boolean exito, int id, String foto) {
        this.mensaje = mensaje;
        this.exito = exito;
        this.id = id;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
