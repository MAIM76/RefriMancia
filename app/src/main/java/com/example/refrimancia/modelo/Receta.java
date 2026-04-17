package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Receta implements Serializable {
    // Identificador único de la receta
    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("imagen_receta")
    private String imagenUrl;

    @SerializedName("fecha_publicacion")
    private String fechaCreacion;
    
    @SerializedName("dificultad")
    private String dificultad;

    @SerializedName("tipo_receta")
    private String categoria;

    @SerializedName("titulo_receta")
    private String titulo;

    @SerializedName("ingredientes")
    private String descripcion;

    @SerializedName("descripcion")
    private String pasos;

    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    @SerializedName("porciones")
    private int porciones;

    @SerializedName("aprobada")
    private Boolean aprobada;

    // Constructor sin argumentos (requerido por Gson para deserialización)
    public Receta() {
    }

    // Constructor simplificado para crear datos de ejemplo o pruebas rápidas
    public Receta(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Receta(int idReceta, String titulo, String descripcion, String pasos, int tiempoPreparacion, int idUsuario, String imagenUrl, String fechaCreacion) {
        this.idReceta = idReceta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.pasos = pasos;
        this.tiempoPreparacion = tiempoPreparacion;
        this.idUsuario = idUsuario;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    public Receta(int idReceta, String titulo, String descripcion, String pasos, int tiempoPreparacion, String dificultad, String categoria, int idUsuario, String imagenUrl, String fechaCreacion) {
        this.idReceta = idReceta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.pasos = pasos;
        this.tiempoPreparacion = tiempoPreparacion;
        this.dificultad = dificultad;
        this.categoria = categoria;
        this.idUsuario = idUsuario;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    // ============ GETTERS ============
    public int getIdReceta() { return idReceta; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getPasos() { return pasos; }
    public int getTiempoPreparacion() { return tiempoPreparacion; }
    public int getPorciones() { return porciones; }
    public String getDificultad() { return dificultad; }
    public String getCategoria() { return categoria; }
    public String getFechaCreacion() { return fechaCreacion; }
    public String getImagenUrl() { return imagenUrl; }
    public Boolean getAprobada() { return aprobada; }

    // ============ SETTERS ============
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPasos(String pasos) { this.pasos = pasos; }
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
    public void setPorciones(int porciones) { this.porciones = porciones; }
    public void setAprobada(Boolean aprobada) { this.aprobada = aprobada; }
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
