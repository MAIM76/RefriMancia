package com.example.refrimancia;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("api/usuarios/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Multipart
    @POST("api/recetas/crear")
    Call<CreateRecipeResponse> crearReceta(
            @Part("titulo_receta") RequestBody titulo,
            @Part("ingredientes") RequestBody ingredientes,
            @Part("descripcion") RequestBody descripcion,
            @Part("tipo_receta") RequestBody tipo,
            @Part("tiempo_preparacion") RequestBody tiempo,
            @Part MultipartBody.Part imagen_receta
    );
}
