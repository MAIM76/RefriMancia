package com.example.refrimancia.api;

import com.example.refrimancia.modelo.Valoracion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ValoracionService {
    @GET("api/valoraciones/")
    Call<List<Valoracion>> obtenerValoraciones();

    @GET("api/valoraciones/receta/{id}")
    Call<List<Valoracion>> obtenerValoracionesPorReceta(@Path("id") int idReceta);

    // Endpoint for creating/updating a valoration might exist, but Postman has limited urls, assuming POST
    @POST("api/valoraciones/")
    Call<Valoracion> crearValoracion(@Body Valoracion valoracion);
}
