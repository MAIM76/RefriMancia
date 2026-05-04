package com.example.refrimancia.modelo.entidad;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Receta implements Serializable {
    @SerializedName("id_receta")
    private int idReceta;

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName(value = "nombre_usuario", alternate = {"autor"})
    private String nombreUsuario;

    @SerializedName(value = "imagen_receta", alternate = {"imagen"})
    private String imagenUrl;

    @SerializedName("fecha_publicacion")
    private String fechaCreacion;

    @SerializedName("dificultad")
    private String dificultad;

    @SerializedName("tipo_receta")
    private String categoria;

    @SerializedName("titulo_receta")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("ingredientes")
    private String ingredientes;

    @SerializedName(value = "instrucciones", alternate = {"pasos"})
    private String pasos;

    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    @SerializedName("porciones")
    private int porciones;

    @SerializedName("aprobada")
    private Boolean aprobada;

    @SerializedName("media_puntuacion")
    private String mediaPuntuacion;

    @SerializedName("consumo_habitual")
    private String consumoHabitual;

    @SerializedName("semaforo")
    private String semaforo;

    @SerializedName("comentarios")
    private java.util.List<Comentario> comentarios;

    public Receta() {
    }

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

    public int getIdReceta() { return idReceta; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getIngredientes() { return ingredientes; }
    public String getPasos() { return pasos; }
    public int getTiempoPreparacion() { return tiempoPreparacion; }
    public int getPorciones() { return porciones; }
    public String getDificultad() { return dificultad; }
    public String getCategoria() { return categoria; }
    public String getFechaCreacion() { return fechaCreacion; }
    public String getImagenUrl() { return imagenUrl; }
    public Boolean getAprobada() { return aprobada; }
    public String getMediaPuntuacion() { return mediaPuntuacion; }
    public String getConsumoHabitual() { return consumoHabitual; }
    public String getSemaforo() { return semaforo; }
    public java.util.List<Comentario> getComentarios() { return comentarios; }

    public Float getMediaPuntuacionFloat() {
        if (mediaPuntuacion == null || mediaPuntuacion.trim().isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(mediaPuntuacion);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }
    public void setPasos(String pasos) { this.pasos = pasos; }
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
    public void setPorciones(int porciones) { this.porciones = porciones; }
    public void setAprobada(Boolean aprobada) { this.aprobada = aprobada; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setMediaPuntuacion(String mediaPuntuacion) { this.mediaPuntuacion = mediaPuntuacion; }
    public void setConsumoHabitual(String consumoHabitual) { this.consumoHabitual = consumoHabitual; }
    public void setSemaforo(String semaforo) { this.semaforo = semaforo; }
    public void setComentarios(java.util.List<Comentario> comentarios) { this.comentarios = comentarios; }
}
