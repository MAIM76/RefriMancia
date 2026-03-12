package com.example.refrimancia;

public class RegistroRespuesta {
    private String mensaje;
    private boolean exito;
    private int id;

    public RegistroRespuesta(String mensaje, boolean exito, int id) {
        this.mensaje = mensaje;
        this.exito = exito;
        this.id = id;
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
}
