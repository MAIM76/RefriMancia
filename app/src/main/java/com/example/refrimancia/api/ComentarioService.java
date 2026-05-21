package com.example.refrimancia.api;

import com.example.refrimancia.model.entity.Comentario;
import com.example.refrimancia.model.request.ComentarioRequest;
import com.example.refrimancia.model.response.RespuestaPaginada;

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
    
    // ======================== ENDPOINTS DE CREACIÓN ========================
    
    @POST("api/comentarios/crear")
    Call<ResponseBody> crearComentario(@Body ComentarioRequest comentario);

    // ======================== ENDPOINTS DE MODIFICACIÓN ========================
    
    @PUT("api/comentarios/modificar/{id}")
    Call<ResponseBody> modificarComentario(@Path("id") int id, @Body ComentarioRequest comentario);
    
    // ======================== ENDPOINTS DE CONSULTA ========================
    
    // Sin parámetro page: devuelve todos los comentarios de una vez
    @GET("api/comentarios/receta/{id}")
    Call<RespuestaPaginada<Comentario>> obtenerComentariosPorReceta(@Path("id") int idReceta);

    // Con parámetro page: paginación lazy para listas largas
    @GET("api/comentarios/receta/{id}")
    Call<RespuestaPaginada<Comentario>> obtenerComentariosPorRecetaPaginado(@Path("id") int idReceta, 
            @Query("page") Integer page);
    
    // ======================== ENDPOINTS DE ELIMINACIÓN ========================
    
    @DELETE("api/comentarios/eliminar/{id}")
    Call<ResponseBody> eliminarComentario(@Path("id") int id);
}
