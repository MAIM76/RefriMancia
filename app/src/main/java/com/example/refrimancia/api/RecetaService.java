package com.example.refrimancia.api;

import com.example.refrimancia.modelo.entidad.Receta;
import com.example.refrimancia.modelo.response.RecetaCreada;
import com.example.refrimancia.modelo.response.RespuestaPaginada;
import com.example.refrimancia.modelo.response.RespuestaUnica;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaz de servicio Retrofit para la gestión de recetas.
 * Proporciona endpoints para crear, modificar, eliminar, buscar y obtener recetas
 * en la aplicación RefriMancia.
 */
public interface RecetaService {
    
    // ======================== ENDPOINTS DE CONSULTA ========================
    
    /**
     * Obtiene todas las recetas con soporte de paginación.
     * @param page Número de página (opcional, null para todas)
     * @return Respuesta paginada con la lista de recetas
     */
    @GET("api/recetas/listar")
    Call<RespuestaPaginada<Receta>> obtenerRecetas(@Query("page") Integer page);

    /**
     * Obtiene todas las recetas sin paginación.
     * @return Respuesta paginada con todas las recetas
     */
    @GET("api/recetas/listar")
    Call<RespuestaPaginada<Receta>> obtenerRecetas();

    /**
     * Obtiene una receta específica por su ID.
     * @param id ID de la receta a obtener
     * @return Respuesta única con la receta solicitada
     */
    @GET("api/recetas/{id}")
    Call<RespuestaUnica<Receta>> obtenerReceta(@Path("id") int id);

    /**
     * Obtiene la recomendación diaria de receta.
     * @return Respuesta única con la receta recomendada del día
     */
    @GET("api/recetas/recomendacion/diaria")
    Call<RespuestaUnica<Receta>> recomendacionDiaria();

    /**
     * Busca recetas por ingredientes.
     * @param ingredientes Cadena de texto con los ingredientes a buscar
     * @return Respuesta paginada con las recetas que contienen los ingredientes
     */
    @GET("api/recetas/buscar/ingredientes")
    Call<RespuestaPaginada<Receta>> buscarPorIngredientes(@Query("ingredientes") String ingredientes);

    /**
     * Obtiene URL para compartir una receta.
     * @param id ID de la receta a compartir
     * @return ResponseBody con la URL de compartir
     */
    @GET("api/recetas/compartir/{id}")
    Call<ResponseBody> compartirReceta(@Path("id") int id);
    
    // ======================== ENDPOINTS DE CREACIÓN ========================
    
    /**
     * Crea una nueva receta con imagen.
     * Utiliza multipart/form-data para enviar tanto los datos como la imagen.
     * 
     * @param tituloReceta Título de la receta
     * @param descripcion Descripción detallada de la receta
     * @param ingredientes Lista de ingredientes necesarios
     * @param tipoReceta Tipo o categoría de la receta
     * @param tiempoPreparacion Tiempo de preparación en minutos
     * @param imagenReceta Imagen de la receta (opcional)
     * @return Receta creada con su ID asignado
     */
    @Multipart
    @POST("api/recetas/crear")
    Call<Receta> crearReceta(
            @Part("titulo_receta") RequestBody tituloReceta,
            @Part("descripcion") RequestBody descripcion,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("tipo_receta") RequestBody tipoReceta,
            @Part("tiempo_preparacion") RequestBody tiempoPreparacion,
            @Part MultipartBody.Part imagenReceta
    );

    /**
     * Crea una nueva receta devolviendo la respuesta completa de creación.
     * Devuelve status, id_receta y foto en lugar del objeto Receta completo.
     */
    @Multipart
    @POST("api/recetas/crear")
    Call<RecetaCreada> crearRecetaConRespuesta(
            @Part("titulo_receta") RequestBody tituloReceta,
            @Part("descripcion") RequestBody descripcion,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("tipo_receta") RequestBody tipoReceta,
            @Part("tiempo_preparacion") RequestBody tiempoPreparacion,
            @Part MultipartBody.Part imagenReceta
    );
    
    // ======================== ENDPOINTS DE MODIFICACIÓN ========================
    
    /**
     * Modifica una receta existente con soporte para imagen.
     * Utiliza multipart/form-data para enviar tanto los datos como la imagen.
     * 
     * @param id ID de la receta a modificar
     * @param tituloReceta Nuevo título de la receta
     * @param descripcion Nueva descripción de la receta
     * @param ingredientes Nueva lista de ingredientes
     * @param tipoReceta Nuevo tipo o categoría de la receta
     * @param tiempoPreparacion Nuevo tiempo de preparación
     * @param imagenReceta Nueva imagen de la receta (opcional)
     * @return Receta actualizada
     */
    @Multipart
    @PUT("api/recetas/modificar/{id}")
    Call<Receta> modificarReceta(
            @Path("id") int id,
            @Part("titulo_receta") RequestBody tituloReceta,
            @Part("descripcion") RequestBody descripcion,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("tipo_receta") RequestBody tipoReceta,
            @Part("tiempo_preparacion") RequestBody tiempoPreparacion,
            @Part MultipartBody.Part imagenReceta
    );
    
    // ======================== ENDPOINTS DE ELIMINACIÓN ========================
    
    /**
     * Elimina una receta por su ID.
     * @param id ID de la receta a eliminar
     * @return ResponseBody con el resultado de la operación
     */
    @DELETE("api/recetas/eliminar/{id}")
    Call<ResponseBody> eliminarReceta(@Path("id") int id);
}
