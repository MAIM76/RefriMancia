package com.example.refrimancia.modelo;

public class Receta {
    private String titulo;
    private String descripcion;
    private int imagenResId; // 0 = sin imagen asignada aún

    public Receta(String titulo, String descripcion, int imagenResId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenResId = imagenResId;
    }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getImagenResId() { return imagenResId; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }
}

