package com.example.refrimancia.api;

import com.example.refrimancia.model.entity.Valoracion;
import com.example.refrimancia.model.request.ValoracionRequest;
import com.example.refrimancia.model.response.ValoracionReceta;
import com.example.refrimancia.model.response.ValoracionUsuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ValoracionService {
    
    // ======================== ENDPOINTS DE CONSULTA ========================
    
    @GET("api/valoraciones/")
    Call<List<Valoracion>> obtenerValoraciones();

    @GET("api/valoraciones/receta/{id}")
    Call<ValoracionReceta> obtenerValoracionesPorReceta(@Path("id") int idReceta);
    
    // ======================== ENDPOINTS DE CREACIÓN ========================
    
    // Si el usuario ya ha valorado la receta, actualiza la valoración existente
    @POST("api/valoraciones")
    Call<ResponseBody> crearValoracion(@Body ValoracionRequest valoracion);

    // Devuelve ha_valorado (boolean) y puntuacion del usuario autenticado para esa receta
    @GET("api/valoraciones/mi-valoracion/{id}")
    Call<ValoracionUsuario> obtenerValoracionUsuario(@Path("id") int idReceta);
}
