package com.example.refrimancia.api;

import com.example.refrimancia.model.entity.Valoracion;
import com.example.refrimancia.model.request.ValoracionRequest;
import com.example.refrimancia.model.response.ValoracionReceta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interfaz de servicio Retrofit para la gestión de valoraciones.
 * Proporciona endpoints para crear, obtener y gestionar valoraciones
 * de las recetas en la aplicación RefriMancia.
 */
public interface ValoracionService {
    
    // ======================== ENDPOINTS DE CONSULTA ========================
    
    /**
     * Obtiene todas las valoraciones disponibles en el sistema.
     * @return Lista con todas las valoraciones
     */
    @GET("api/valoraciones/")
    Call<List<Valoracion>> obtenerValoraciones();

    /**
     * Obtiene todas las valoraciones de una receta específica.
     * Incluye información agregada como la nota media.
     * @param idReceta ID de la receta de la cual obtener valoraciones
     * @return Objeto ValoracionReceta con las valoraciones y estadísticas
     */
    @GET("api/valoraciones/receta/{id}")
    Call<ValoracionReceta> obtenerValoracionesPorReceta(@Path("id") int idReceta);
    
    // ======================== ENDPOINTS DE CREACIÓN ========================
    
    /**
     * Crea una nueva valoración o actualiza una existente.
     * Si el usuario ya ha valorado la receta, se actualiza la valoración existente.
     * @param valoracion Objeto con los datos de la valoración
     * @return Valoración creada o actualizada
     */
    @POST("api/valoraciones/")
    Call<Valoracion> crearValoracion(@Body ValoracionRequest valoracion);
}
