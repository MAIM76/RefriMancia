package com.example.refrimancia;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/usuarios/crear")
    Call<RegistroRespuesta> registro(@Body RegistroRequest request);
    @POST("api/usuarios/solicitar-codigo")
    Call<CorreoRespuesta> solicitarCodigo(@Body CorreoRequest request);
    @POST("api/usuarios/cambiar-contrasena")
    Call<CambiarPassRespuesta> cambiarPassword(@Body CambiarPassRequest request);
}