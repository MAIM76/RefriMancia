package com.example.refrimancia.api;

import com.example.refrimancia.modelo.AuthToken;
import com.example.refrimancia.modelo.LoginRequest;
import com.example.refrimancia.modelo.Usuario;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioService {

    @POST("api/usuarios/login")
    Call<AuthToken> login(@Body LoginRequest loginRequest);

    @POST("api/usuarios/logout")
    Call<ResponseBody> logout();

    @Multipart
    @POST("api/usuarios/crear")
    Call<AuthToken> crearUsuario(
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("nombre_completo") RequestBody nombreCompleto,
            @Part("correo_electronico") RequestBody correoElectronico,
            @Part MultipartBody.Part imagenPerfil
    );

    @GET("api/usuarios/perfil")
    Call<Usuario> obtenerPerfil();

    @Multipart
    @PUT("api/usuarios/modificar/{id}")
    Call<Usuario> modificarUsuario(
            @Path("id") int id,
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("nombre_completo") RequestBody nombreCompleto,
            @Part("correo_electronico") RequestBody correoElectronico,
            @Part MultipartBody.Part imagenPerfil
    );

    @GET("api/usuarios/listar")
    Call<List<Usuario>> listarUsuarios(@Query("page") Integer page);

    @POST("api/usuarios/solicitar-codigo")
    Call<ResponseBody> solicitarCodigo(@Body Map<String, String> request);

    @POST("api/usuarios/cambiar-contrasena")
    Call<ResponseBody> cambiarContrasena(@Body Map<String, String> request);
}
