package com.example.refrimancia.api;

import com.example.refrimancia.modelo.entidad.Comentario;
import com.example.refrimancia.modelo.request.ComentarioRequest;
import com.example.refrimancia.modelo.response.RespuestaPaginada;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaz de servicio Retrofit para la gestión de comentarios.
 * Proporciona endpoints para crear, modificar, eliminar y obtener comentarios
 * de las recetas en la aplicación RefriMancia.
 */
public interface ComentarioService {
    
    // ======================== ENDPOINTS DE CREACIÓN ========================
    
    /**
     * Crea un nuevo comentario en una receta.
     * @param comentario Objeto con los datos del comentario a crear
     * @return ResponseBody con el resultado de la operación
     */
    @POST("api/comentarios/crear")
    Call<ResponseBody> crearComentario(@Body ComentarioRequest comentario);

    // ======================== ENDPOINTS DE MODIFICACIÓN ========================
    
    /**
     * Modifica un comentario existente por su ID.
     * @param id ID del comentario a modificar
     * @param comentario Objeto con los nuevos datos del comentario
     * @return Comentario actualizado
     */
    @PUT("api/comentarios/modificar/{id}")
    Call<Comentario> modificarComentario(@Path("id") int id, @Body Comentario comentario);
    
    // ======================== ENDPOINTS DE CONSULTA ========================
    
    /**
     * Obtiene todos los comentarios de una receta específica.
     * @param idReceta ID de la receta de la cual obtener comentarios
     * @return Respuesta paginada con la lista de comentarios
     */
    @GET("api/comentarios/receta/{id}")
    Call<RespuestaPaginada<Comentario>> obtenerComentariosPorReceta(@Path("id") int idReceta);

    /**
     * Obtiene comentarios de una receta con soporte de paginación.
     * @param idReceta ID de la receta de la cual obtener comentarios
     * @param page Número de página (opcional, null para todas)
     * @return Respuesta paginada con la lista de comentarios
     */
    @GET("api/comentarios/receta/{id}")
    Call<RespuestaPaginada<Comentario>> obtenerComentariosPorRecetaPaginado(@Path("id") int idReceta, 
            @Query("page") Integer page);
    
    // ======================== ENDPOINTS DE ELIMINACIÓN ========================
    
    /**
     * Elimina un comentario por su ID.
     * @param id ID del comentario a eliminar
     * @return ResponseBody con el resultado de la operación
     */
    @DELETE("api/comentarios/eliminar/{id}")
    Call<ResponseBody> eliminarComentario(@Path("id") int id);
}
