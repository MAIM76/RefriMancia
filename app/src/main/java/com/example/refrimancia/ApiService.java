package com.example.refrimancia;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart  //esta petición no es JSON, sino multipart/form-data
    @POST("api/usuarios/crear") //Cada campo del form-data se manda como una parte separada
    Call<RegistroRespuesta> registro(
            @Part("nombre_usuario") RequestBody nombre_usuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("correo_electronico") RequestBody correo_electronico,
            @Part("nombre_completo") RequestBody nombre_completo,
            @Part("fecha_nac") RequestBody fecha_nac,
            @Part MultipartBody.Part imagen_perfil //La imagen no es texto, así que no puede ir como RequestBody normal

    );
    @POST("api/usuarios/solicitar-codigo")
    Call<CorreoRespuesta> solicitarCodigo(@Body CorreoRequest request);
    @POST("api/usuarios/cambiar-contrasena")
    Call<CambiarPassRespuesta> cambiarPassword(@Body CambiarPassRequest request);
}