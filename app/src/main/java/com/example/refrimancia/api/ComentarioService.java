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

public interface ComentarioService {
    // Crear un nuevo comentario en una receta
    @POST("api/comentarios/crear")
    Call<ResponseBody> crearComentario(@Body ComentarioRequest comentario);

    // Compatibilidad temporal: el nuevo flujo debería usar ComentarioRequest.
    @Deprecated
    @POST("api/comentarios/crear")
    Call<ResponseBody> crearComentario(@Body Comentario comentario);

    // Modificar un comentario existente por su ID
    @PUT("api/comentarios/modificar/{id}")
    Call<Comentario> modificarComentario(@Path("id") int id, @Body Comentario comentario);

    // Obtener todos los comentarios de una receta específica
    @GET("api/comentarios/receta/{id}")
    Call<RespuestaPaginada<Comentario>> obtenerComentariosPorReceta(@Path("id") int idReceta);

    // Obtener comentarios de una receta con paginación
    @GET("api/comentarios/receta/{id}")
    Call<RespuestaPaginada<Comentario>> obtenerComentariosPorRecetaPaginado(@Path("id") int idReceta, @Query("page") Integer page);

    // Eliminar un comentario por su ID
    @DELETE("api/comentarios/eliminar/{id}")
    Call<ResponseBody> eliminarComentario(@Path("id") int id);
}
