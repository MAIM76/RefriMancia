package com.example.refrimancia.modelo;

import com.google.gson.annotations.SerializedName;

public class Receta {
    // Identificador único de la receta
    @SerializedName("id_receta")
    private int idReceta;

    // Título o nombre de la receta
    @SerializedName("titulo_receta")
    private String tituloReceta;

    // Descripción breve de la receta
    @SerializedName("descripcion")
    private String descripcion;

    // URL de la imagen de la receta
    @SerializedName("imagen_receta")
    private String imagenReceta;

    // Lista de ingredientes (formato JSON o texto)
    @SerializedName("ingredientes")
    private String ingredientes;

    // Tipo o categoría de la receta
    @SerializedName("tipo_receta")
    private String tipoReceta;

    // Fecha en que se publicó la receta
    @SerializedName("fecha_publicacion")
    private String fechaPublicacion;

    // ID del usuario que creó la receta
    @SerializedName("id_usuario")
    private int idUsuario;

    // Instrucciones detalladas para preparar la receta
    @SerializedName("instrucciones")
    private String instrucciones;

    // Tiempo en minutos necesario para preparar la receta
    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    // Constructor sin argumentos (requerido por Gson para deserialización)
    public Receta() {
    }

    // Constructor simplificado para crear datos de ejemplo o pruebas rápidas
    public Receta(String tituloReceta, String descripcion) {
        this.tituloReceta = tituloReceta;
        this.descripcion = descripcion;
    }

    // ============ GETTERS ============
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

    // ============ SETTERS ============
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
