package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Receta implements Serializable {
    // ======================== ATRIBUTOS PRINCIPALES ========================
    
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
    
    // ======================== ATRIBUTOS DE CONTENIDO ========================
    
    @SerializedName("titulo_receta")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("ingredientes")
    private String ingredientes;

    @SerializedName(value = "instrucciones", alternate = {"pasos"})
    private String pasos;
    
    // ======================== ATRIBUTOS DE METADATOS ========================
    
    @SerializedName("dificultad")
    private String dificultad;

    @SerializedName("tipo_receta")
    private String categoria;

    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    @SerializedName("porciones")
    private int porciones;

    @SerializedName("aprobada")
    private Boolean aprobada;
    
    // ======================== ATRIBUTOS DE VALORACIÓN ========================
    
    @SerializedName("media_puntuacion")
    private String mediaPuntuacion;
    
    // ======================== ATRIBUTOS ADICIONALES ========================
    
    @SerializedName("consumo_habitual")
    private String consumoHabitual;

    @SerializedName("semaforo")
    private String semaforo;

    @SerializedName("kcal")
    private float kcal;

    @SerializedName("proteinas")
    private float proteinas;

    @SerializedName("carbohidratos")
    private float carbohidratos;

    @SerializedName("fibra")
    private float fibra;

    @SerializedName("grasas")
    private float grasas;

    @SerializedName("peso_total_g")
    private float pesoTotalG;

    @SerializedName("azucares")
    private float azucares;

    @SerializedName("grasas_saturadas")
    private float grasasSaturadas;

    @SerializedName("sal")
    private float sal;

    @SerializedName("kcal_100g")
    private float kcal100g;

    @SerializedName("proteinas_100g")
    private float proteinas100g;

    @SerializedName("carbohidratos_100g")
    private float carbohidratos100g;

    @SerializedName("azucares_100g")
    private float azucares100g;

    @SerializedName("grasas_100g")
    private float grasas100g;

    @SerializedName("grasas_saturadas_100g")
    private float grasasSaturadas100g;

    @SerializedName("fibra_100g")
    private float fibra100g;

    @SerializedName("sal_100g")
    private float sal100g;

    @SerializedName("comentarios")
    private java.util.List<Comentario> comentarios;

    // ======================== CONSTRUCTORES ========================
    
    public Receta() {
    }

    public Receta(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Receta(int idReceta, String titulo, String descripcion, String pasos, int tiempoPreparacion, 
            int idUsuario, String imagenUrl, String fechaCreacion) {
        this.idReceta = idReceta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.pasos = pasos;
        this.tiempoPreparacion = tiempoPreparacion;
        this.idUsuario = idUsuario;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    public Receta(int idReceta, String titulo, String descripcion, String pasos, int tiempoPreparacion, 
            String dificultad, String categoria, int idUsuario, String imagenUrl, String fechaCreacion) {
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

    // ======================== GETTERS ========================
    
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
    
    public float getKcal() { return kcal; }

    public float getProteinas() { return proteinas; }

    public float getCarbohidratos() { return carbohidratos; }

    public float getFibra() { return fibra; }

    public float getGrasas() { return grasas; }

    public float getPesoTotalG() { return pesoTotalG; }

    public float getAzucares() { return azucares; }

    public float getGrasasSaturadas() { return grasasSaturadas; }

    public float getSal() { return sal; }

    public float getKcal100g() { return kcal100g; }

    public float getProteinas100g() { return proteinas100g; }

    public float getCarbohidratos100g() { return carbohidratos100g; }

    public float getAzucares100g() { return azucares100g; }

    public float getGrasas100g() { return grasas100g; }

    public float getGrasasSaturadas100g() { return grasasSaturadas100g; }

    public float getFibra100g() { return fibra100g; }

    public float getSal100g() { return sal100g; }

    public java.util.List<Comentario> getComentarios() { return comentarios; }
    
    // Convierte mediaPuntuacion (String) a Float de forma segura; devuelve null si no es válido
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

    // ======================== SETTERS ========================
    
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }
    
    public void setPasos(String pasos) { this.pasos = pasos; }
    
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
    
    public void setPorciones(int porciones) { this.porciones = porciones; }

    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public void setAprobada(Boolean aprobada) { this.aprobada = aprobada; }
    
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public void setMediaPuntuacion(String mediaPuntuacion) { this.mediaPuntuacion = mediaPuntuacion; }
    
    public void setConsumoHabitual(String consumoHabitual) { this.consumoHabitual = consumoHabitual; }
    
    public void setSemaforo(String semaforo) { this.semaforo = semaforo; }
    
    public void setKcal(float kcal) { this.kcal = kcal; }

    public void setProteinas(float proteinas) { this.proteinas = proteinas; }

    public void setCarbohidratos(float carbohidratos) { this.carbohidratos = carbohidratos; }

    public void setFibra(float fibra) { this.fibra = fibra; }

    public void setGrasas(float grasas) { this.grasas = grasas; }

    public void setPesoTotalG(float pesoTotalG) { this.pesoTotalG = pesoTotalG; }

    public void setAzucares(float azucares) { this.azucares = azucares; }

    public void setGrasasSaturadas(float grasasSaturadas) { this.grasasSaturadas = grasasSaturadas; }

    public void setSal(float sal) { this.sal = sal; }

    public void setKcal100g(float kcal100g) { this.kcal100g = kcal100g; }

    public void setProteinas100g(float proteinas100g) { this.proteinas100g = proteinas100g; }

    public void setCarbohidratos100g(float carbohidratos100g) { this.carbohidratos100g = carbohidratos100g; }

    public void setAzucares100g(float azucares100g) { this.azucares100g = azucares100g; }

    public void setGrasas100g(float grasas100g) { this.grasas100g = grasas100g; }

    public void setGrasasSaturadas100g(float grasasSaturadas100g) { this.grasasSaturadas100g = grasasSaturadas100g; }

    public void setFibra100g(float fibra100g) { this.fibra100g = fibra100g; }

    public void setSal100g(float sal100g) { this.sal100g = sal100g; }

    public void setComentarios(java.util.List<Comentario> comentarios) { this.comentarios = comentarios; }
}
