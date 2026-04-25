package com.example.refrimancia.api;

import com.example.refrimancia.modelo.RespuestaValoracionReceta;
import com.example.refrimancia.modelo.Valoracion;
import com.example.refrimancia.modelo.ValoracionRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ValoracionService {
    // Obtener todas las valoraciones disponibles
    @GET("api/valoraciones/")
    Call<List<Valoracion>> obtenerValoraciones();

    // Obtener todas las valoraciones de una receta especfica
    @GET("api/valoraciones/receta/{id}")
    Call<RespuestaValoracionReceta> obtenerValoracionesPorReceta(@Path("id") int idReceta);

    // Crear una nueva valoracion o actualizar una existente
    @POST("api/valoraciones/")
    Call<Valoracion> crearValoracion(@Body ValoracionRequest valoracion);
}
