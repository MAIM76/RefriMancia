package com.example.refrimancia.api;

import com.example.refrimancia.modelo.Comentario;

import java.util.List;

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
    @POST("api/comentarios/crear")
    Call<Comentario> crearComentario(@Body Comentario comentario);

    @PUT("api/comentarios/modificar/{id}")
    Call<Comentario> modificarComentario(@Path("id") int id, @Body Comentario comentario);

    @GET("api/comentarios/receta/{id}")
    Call<List<Comentario>> obtenerComentariosPorReceta(@Path("id") int idReceta);

    @GET("api/comentarios/receta/{id}")
    Call<List<Comentario>> obtenerComentariosPorRecetaPaginado(@Path("id") int idReceta, @Query("page") Integer page);

    @DELETE("api/comentarios/eliminar/{id}")
    Call<ResponseBody> eliminarComentario(@Path("id") int id);
}
