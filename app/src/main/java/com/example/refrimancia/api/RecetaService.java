package com.example.refrimancia.api;

import com.example.refrimancia.modelo.Receta;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecetaService {
    // Obtener todas las recetas con paginación
    @GET("api/recetas/listar")
    Call<List<Receta>> obtenerRecetas(@Query("page") Integer page);

    // Obtener todas las recetas sin paginación
    @GET("api/recetas/listar")
    Call<List<Receta>> obtenerRecetas();

    // Obtener una receta específica por su ID
    @GET("api/recetas/{id}")
    Call<Receta> obtenerReceta(@Path("id") int id);

    // Obtener la recomendación diaria de receta
    @GET("api/recetas/recomendacion/diaria")
    Call<Receta> recomendacionDiaria();

    // Buscar recetas por ingredientes
    @GET("api/recetas/buscar/ingredientes")
    Call<List<Receta>> buscarPorIngredientes(@Query("ingredientes") String ingredientes);

    // Obtener URL para compartir una receta
    @GET("api/recetas/compartir/{id}")
    Call<ResponseBody> compartirReceta(@Path("id") int id);

    @Multipart
    @POST("api/recetas/crear")
    Call<Receta> crearReceta(
            @Part("titulo_receta") RequestBody tituloReceta,
            @Part("descripcion") RequestBody descripcion,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("tipo_receta") RequestBody tipoReceta,
            @Part("instrucciones") RequestBody instrucciones,
            @Part("tiempo_preparacion") RequestBody tiempoPreparacion,
            @Part MultipartBody.Part imagen
    );

    // Modificar una receta existente con multipart (incluyendo imagen)
    @Multipart
    @PUT("api/recetas/modificar/{id}")
    Call<Receta> modificarReceta(
            @Path("id") int id,
            @Part("titulo_receta") RequestBody tituloReceta,
            @Part("descripcion") RequestBody descripcion,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("tipo_receta") RequestBody tipoReceta,
            @Part("instrucciones") RequestBody instrucciones,
            @Part("tiempo_preparacion") RequestBody tiempoPreparacion,
            @Part MultipartBody.Part imagen
    );

    // Eliminar una receta por su ID
    @DELETE("api/recetas/eliminar/{id}")
    Call<ResponseBody> eliminarReceta(@Path("id") int id);
}
