package com.example.refrimancia.model.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Entidad que representa una receta en la aplicación RefriMancia.
 * Contiene toda la información necesaria para mostrar, crear y gestionar recetas
 * incluyendo ingredientes, instrucciones, valoraciones y metadatos.
 */
public class Receta implements Serializable {
    // ======================== ATRIBUTOS PRINCIPALES ========================
    
    /** Identificador único de la receta */
    @SerializedName("id_receta")
    private int idReceta;

    /** Identificador del usuario que creó la receta */
    @SerializedName("id_usuario")
    private int idUsuario;

    /** Nombre del usuario autor de la receta */
    @SerializedName(value = "nombre_usuario", alternate = {"autor"})
    private String nombreUsuario;

    /** URL de la imagen de la receta */
    @SerializedName(value = "imagen_receta", alternate = {"imagen"})
    private String imagenUrl;

    /** Fecha de publicación de la receta */
    @SerializedName("fecha_publicacion")
    private String fechaCreacion;
    
    // ======================== ATRIBUTOS DE CONTENIDO ========================
    
    /** Título de la receta */
    @SerializedName("titulo_receta")
    private String titulo;

    /** Descripción detallada de la receta */
    @SerializedName("descripcion")
    private String descripcion;

    /** Lista de ingredientes necesarios */
    @SerializedName("ingredientes")
    private String ingredientes;

    /** Instrucciones o pasos para preparar la receta */
    @SerializedName(value = "instrucciones", alternate = {"pasos"})
    private String pasos;
    
    // ======================== ATRIBUTOS DE METADATOS ========================
    
    /** Nivel de dificultad de la receta */
    @SerializedName("dificultad")
    private String dificultad;

    /** Categoría o tipo de receta */
    @SerializedName("tipo_receta")
    private String categoria;

    /** Tiempo de preparación en minutos */
    @SerializedName("tiempo_preparacion")
    private int tiempoPreparacion;

    /** Número de porciones que rinde la receta */
    @SerializedName("porciones")
    private int porciones;

    /** Indica si la receta está aprobada por administradores */
    @SerializedName("aprobada")
    private Boolean aprobada;
    
    // ======================== ATRIBUTOS DE VALORACIÓN ========================
    
    /** Puntuación media de la receta (como String) */
    @SerializedName("media_puntuacion")
    private String mediaPuntuacion;
    
    // ======================== ATRIBUTOS ADICIONALES ========================
    
    /** Tipo de consumo habitual de la receta */
    @SerializedName("consumo_habitual")
    private String consumoHabitual;

    /** Clasificación de semáforo nutricional */
    @SerializedName("semaforo")
    private String semaforo;

    /** Calorías de la receta */
    @SerializedName("kcal")
    private float kcal;

    /** Proteínas en gramos */
    @SerializedName("proteinas")
    private float proteinas;

    /** Carbohidratos en gramos */
    @SerializedName("carbohidratos")
    private float carbohidratos;

    /** Fibra en gramos */
    @SerializedName("fibra")
    private float fibra;

    /** Grasas en gramos */
    @SerializedName("grasas")
    private float grasas;

    /** Peso total de la receta en gramos */
    @SerializedName("peso_total_g")
    private float pesoTotalG;

    /** Azúcares en gramos */
    @SerializedName("azucares")
    private float azucares;

    /** Grasas saturadas en gramos */
    @SerializedName("grasas_saturadas")
    private float grasasSaturadas;

    /** Sal en gramos */
    @SerializedName("sal")
    private float sal;

    /** Calorías por 100g */
    @SerializedName("kcal_100g")
    private float kcal100g;

    /** Proteínas por 100g */
    @SerializedName("proteinas_100g")
    private float proteinas100g;

    /** Carbohidratos por 100g */
    @SerializedName("carbohidratos_100g")
    private float carbohidratos100g;

    /** Azúcares por 100g */
    @SerializedName("azucares_100g")
    private float azucares100g;

    /** Grasas por 100g */
    @SerializedName("grasas_100g")
    private float grasas100g;

    /** Grasas saturadas por 100g */
    @SerializedName("grasas_saturadas_100g")
    private float grasasSaturadas100g;

    /** Fibra por 100g */
    @SerializedName("fibra_100g")
    private float fibra100g;

    /** Sal por 100g */
    @SerializedName("sal_100g")
    private float sal100g;

    /** Lista de comentarios asociados a la receta */
    @SerializedName("comentarios")
    private java.util.List<Comentario> comentarios;

    // ======================== CONSTRUCTORES ========================
    
    /**
     * Constructor vacío requerido para Gson/JSON parsing.
     */
    public Receta() {
    }

    /**
     * Constructor simplificado para crear recetas básicas.
     * @param titulo Título de la receta
     * @param descripcion Descripción de la receta
     */
    public Receta(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    /**
     * Constructor para recetas con información básica.
     * @param idReceta ID de la receta
     * @param titulo Título de la receta
     * @param descripcion Descripción de la receta
     * @param pasos Instrucciones de preparación
     * @param tiempoPreparacion Tiempo en minutos
     * @param idUsuario ID del autor
     * @param imagenUrl URL de la imagen
     * @param fechaCreacion Fecha de publicación
     */
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

    /**
     * Constructor completo para recetas con toda la información.
     * @param idReceta ID de la receta
     * @param titulo Título de la receta
     * @param descripcion Descripción de la receta
     * @param pasos Instrucciones de preparación
     * @param tiempoPreparacion Tiempo en minutos
     * @param dificultad Nivel de dificultad
     * @param categoria Categoría de la receta
     * @param idUsuario ID del autor
     * @param imagenUrl URL de la imagen
     * @param fechaCreacion Fecha de publicación
     */
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
    
    /** @return ID único de la receta */
    public int getIdReceta() { return idReceta; }
    
    /** @return ID del usuario autor */
    public int getIdUsuario() { return idUsuario; }
    
    /** @return Nombre del usuario autor */
    public String getNombreUsuario() { return nombreUsuario; }
    
    /** @return Título de la receta */
    public String getTitulo() { return titulo; }
    
    /** @return Descripción de la receta */
    public String getDescripcion() { return descripcion; }
    
    /** @return Ingredientes de la receta */
    public String getIngredientes() { return ingredientes; }
    
    /** @return Instrucciones de preparación */
    public String getPasos() { return pasos; }
    
    /** @return Tiempo de preparación en minutos */
    public int getTiempoPreparacion() { return tiempoPreparacion; }
    
    /** @return Número de porciones */
    public int getPorciones() { return porciones; }
    
    /** @return Nivel de dificultad */
    public String getDificultad() { return dificultad; }
    
    /** @return Categoría de la receta */
    public String getCategoria() { return categoria; }
    
    /** @return Fecha de creación */
    public String getFechaCreacion() { return fechaCreacion; }
    
    /** @return URL de la imagen */
    public String getImagenUrl() { return imagenUrl; }
    
    /** @return Estado de aprobación */
    public Boolean getAprobada() { return aprobada; }
    
    /** @return Puntuación media como String */
    public String getMediaPuntuacion() { return mediaPuntuacion; }
    
    /** @return Tipo de consumo habitual */
    public String getConsumoHabitual() { return consumoHabitual; }
    
    /** @return Clasificación de semáforo nutricional */
    public String getSemaforo() { return semaforo; }
    
    /** @return Calorías */
    public float getKcal() { return kcal; }

    /** @return Proteínas en gramos */
    public float getProteinas() { return proteinas; }

    /** @return Carbohidratos en gramos */
    public float getCarbohidratos() { return carbohidratos; }

    /** @return Fibra en gramos */
    public float getFibra() { return fibra; }

    /** @return Grasas en gramos */
    public float getGrasas() { return grasas; }

    /** @return Peso total en gramos */
    public float getPesoTotalG() { return pesoTotalG; }

    /** @return Azúcares en gramos */
    public float getAzucares() { return azucares; }

    /** @return Grasas saturadas en gramos */
    public float getGrasasSaturadas() { return grasasSaturadas; }

    /** @return Sal en gramos */
    public float getSal() { return sal; }

    /** @return Calorías por 100g */
    public float getKcal100g() { return kcal100g; }

    /** @return Proteínas por 100g */
    public float getProteinas100g() { return proteinas100g; }

    /** @return Carbohidratos por 100g */
    public float getCarbohidratos100g() { return carbohidratos100g; }

    /** @return Azúcares por 100g */
    public float getAzucares100g() { return azucares100g; }

    /** @return Grasas por 100g */
    public float getGrasas100g() { return grasas100g; }

    /** @return Grasas saturadas por 100g */
    public float getGrasasSaturadas100g() { return grasasSaturadas100g; }

    /** @return Fibra por 100g */
    public float getFibra100g() { return fibra100g; }

    /** @return Sal por 100g */
    public float getSal100g() { return sal100g; }

    /** @return Lista de comentarios */
    public java.util.List<Comentario> getComentarios() { return comentarios; }
    
    /**
     * Obtiene la puntuación media como Float.
     * Convierte el String a Float de forma segura.
     * @return Puntuación media como Float o null si no es válido
     */
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
    
    /** @param idReceta Nuevo ID de la receta */
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    
    /** @param idUsuario Nuevo ID del usuario autor */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    /** @param nombreUsuario Nuevo nombre del usuario autor */
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    
    /** @param titulo Nuevo título de la receta */
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    /** @param descripcion Nueva descripción de la receta */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    /** @param ingredientes Nuevos ingredientes de la receta */
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }
    
    /** @param pasos Nuevas instrucciones de preparación */
    public void setPasos(String pasos) { this.pasos = pasos; }
    
    /** @param tiempoPreparacion Nuevo tiempo de preparación en minutos */
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
    
    /** @param porciones Nuevo número de porciones */
    public void setPorciones(int porciones) { this.porciones = porciones; }

    /** @param dificultad Nuevo nivel de dificultad */
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    /** @param categoria Nueva categoría de la receta */
    public void setCategoria(String categoria) { this.categoria = categoria; }

    /** @param aprobada Nuevo estado de aprobación */
    public void setAprobada(Boolean aprobada) { this.aprobada = aprobada; }
    
    /** @param imagenUrl Nueva URL de la imagen */
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    /** @param fechaCreacion Nueva fecha de creación */
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    /** @param mediaPuntuacion Nueva puntuación media */
    public void setMediaPuntuacion(String mediaPuntuacion) { this.mediaPuntuacion = mediaPuntuacion; }
    
    /** @param consumoHabitual Nuevo tipo de consumo habitual */
    public void setConsumoHabitual(String consumoHabitual) { this.consumoHabitual = consumoHabitual; }
    
    /** @param semaforo Nueva clasificación de semáforo nutricional */
    public void setSemaforo(String semaforo) { this.semaforo = semaforo; }
    
    /** @param kcal Nuevas calorías */
    public void setKcal(float kcal) { this.kcal = kcal; }

    /** @param proteinas Nuevas proteínas en gramos */
    public void setProteinas(float proteinas) { this.proteinas = proteinas; }

    /** @param carbohidratos Nuevos carbohidratos en gramos */
    public void setCarbohidratos(float carbohidratos) { this.carbohidratos = carbohidratos; }

    /** @param fibra Nueva fibra en gramos */
    public void setFibra(float fibra) { this.fibra = fibra; }

    /** @param grasas Nuevas grasas en gramos */
    public void setGrasas(float grasas) { this.grasas = grasas; }

    /** @param pesoTotalG Nuevo peso total en gramos */
    public void setPesoTotalG(float pesoTotalG) { this.pesoTotalG = pesoTotalG; }

    /** @param azucares Nuevos azúcares en gramos */
    public void setAzucares(float azucares) { this.azucares = azucares; }

    /** @param grasasSaturadas Nuevas grasas saturadas en gramos */
    public void setGrasasSaturadas(float grasasSaturadas) { this.grasasSaturadas = grasasSaturadas; }

    /** @param sal Nueva sal en gramos */
    public void setSal(float sal) { this.sal = sal; }

    /** @param kcal100g Nuevas calorías por 100g */
    public void setKcal100g(float kcal100g) { this.kcal100g = kcal100g; }

    /** @param proteinas100g Nuevas proteínas por 100g */
    public void setProteinas100g(float proteinas100g) { this.proteinas100g = proteinas100g; }

    /** @param carbohidratos100g Nuevos carbohidratos por 100g */
    public void setCarbohidratos100g(float carbohidratos100g) { this.carbohidratos100g = carbohidratos100g; }

    /** @param azucares100g Nuevos azúcares por 100g */
    public void setAzucares100g(float azucares100g) { this.azucares100g = azucares100g; }

    /** @param grasas100g Nuevas grasas por 100g */
    public void setGrasas100g(float grasas100g) { this.grasas100g = grasas100g; }

    /** @param grasasSaturadas100g Nuevas grasas saturadas por 100g */
    public void setGrasasSaturadas100g(float grasasSaturadas100g) { this.grasasSaturadas100g = grasasSaturadas100g; }

    /** @param fibra100g Nueva fibra por 100g */
    public void setFibra100g(float fibra100g) { this.fibra100g = fibra100g; }

    /** @param sal100g Nueva sal por 100g */
    public void setSal100g(float sal100g) { this.sal100g = sal100g; }

    /** @param comentarios Nueva lista de comentarios */
    public void setComentarios(java.util.List<Comentario> comentarios) { this.comentarios = comentarios; }
}
