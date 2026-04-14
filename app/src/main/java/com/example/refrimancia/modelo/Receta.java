package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Receta {
    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("titulo_receta")
    private String tituloReceta;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("imagen_receta")
    private String imagenReceta;

    @SerializedName("ingredientes")
    private String ingredientes;

    @SerializedName("tipo_receta")
    private String tipoReceta;

    @SerializedName("fecha_publicacion")
    private String fechaPublicacion;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("instrucciones")
    private String instrucciones;

    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    // Constructor sin argumentos (necesario para Gson)
    public Receta() {
    }

    // Constructor simplificado para datos de ejemplo
    public Receta(String tituloReceta, String descripcion) {
        this.tituloReceta = tituloReceta;
        this.descripcion = descripcion;
    }

    // Getters
    public int getIdReceta() { return idReceta; }
    public String getTituloReceta() { return tituloReceta; }
    public String getDescripcion() { return descripcion; }
    public String getImagenReceta() { return imagenReceta; }
    public String getIngredientes() { return ingredientes; }
    public String getTipoReceta() { return tipoReceta; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public int getIdUsuario() { return idUsuario; }
    public String getInstrucciones() { return instrucciones; }
    public int getTiempoPreparacion() { return tiempoPreparacion; }

    // Setters
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public void setTituloReceta(String tituloReceta) { this.tituloReceta = tituloReceta; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setImagenReceta(String imagenReceta) { this.imagenReceta = imagenReceta; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }
    public void setTipoReceta(String tipoReceta) { this.tipoReceta = tipoReceta; }
    public void setFechaPublicacion(String fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
}
