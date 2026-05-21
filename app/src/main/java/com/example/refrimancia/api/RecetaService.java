package com.example.refrimancia.api;

import java.util.List;

import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.response.RecetaCreada;
import com.example.refrimancia.model.response.RespuestaPaginada;
import com.example.refrimancia.model.response.RespuestaUnica;

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

public interface RecetaService {
    
    // ======================== ENDPOINTS DE CONSULTA ========================
    
    @GET("api/recetas/listar")
    Call<RespuestaPaginada<Receta>> obtenerRecetas(@Query("page") Integer page);

    @GET("api/recetas/listar")
    Call<RespuestaPaginada<Receta>> obtenerRecetas();

    @GET("api/recetas/{id}")
    Call<RespuestaUnica<Receta>> obtenerReceta(@Path("id") int id);

    @GET("api/recetas/recomendacion/diaria")
    Call<RespuestaUnica<Receta>> recomendacionDiaria();

    // ingredientes separados por coma (OR lógico); tiposReceta es multivalor; todos opcionales
    @GET("api/recetas/buscar/ingredientes")
    Call<RespuestaPaginada<Receta>> buscarPorIngredientes(
            @Query("ingredientes") String ingredientes,
            @Query("tipo_receta") List<String> tiposReceta,
            @Query("page") Integer page);

    @GET("api/recetas/compartir/{id}")
    Call<ResponseBody> compartirReceta(@Path("id") int id);
    
    // ======================== ENDPOINTS DE CREACIÓN ========================
    
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

    // Igual que crearReceta pero devuelve status, id_receta y foto en lugar del objeto Receta completo
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
    
    @Multipart
    @PUT("api/recetas/modificar/{id}")
    Call<RespuestaUnica<Receta>> modificarReceta(
            @Path("id") int id,
            @Part("titulo_receta") RequestBody tituloReceta,
            @Part("descripcion") RequestBody descripcion,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("tipo_receta") RequestBody tipoReceta,
            @Part("tiempo_preparacion") RequestBody tiempoPreparacion,
            @Part MultipartBody.Part imagenReceta
    );
    
    // ======================== ENDPOINTS DE ELIMINACIÓN ========================
    
    @DELETE("api/recetas/eliminar/{id}")
    Call<ResponseBody> eliminarReceta(@Path("id") int id);
}
