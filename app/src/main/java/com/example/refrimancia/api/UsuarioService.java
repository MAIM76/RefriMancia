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

    // Autenticar usuario y obtener token de acceso
    @POST("api/usuarios/login")
    Call<AuthToken> login(@Body LoginRequest loginRequest);

    // Cerrar sesión del usuario autenticado
    @POST("api/usuarios/logout")
    Call<ResponseBody> logout();

    // Crear nuevo usuario con imagen de perfil
    @Multipart
    @POST("api/usuarios/crear")
    Call<AuthToken> crearUsuario(
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("nombre_completo") RequestBody nombreCompleto,
            @Part("correo_electronico") RequestBody correoElectronico,
            @Part MultipartBody.Part imagenPerfil
    );

    // Obtener datos del perfil del usuario autenticado
    @GET("api/usuarios/perfil")
    Call<Usuario> obtenerPerfil();

    // Modificar datos de un usuario existente
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

    // Listar todos los usuarios con paginación
    @GET("api/usuarios/listar")
    Call<List<Usuario>> listarUsuarios(@Query("page") Integer page);

    // Solicitar código de recuperación de contraseña
    @POST("api/usuarios/solicitar-codigo")
    Call<ResponseBody> solicitarCodigo(@Body Map<String, String> request);

    // Cambiar contraseña con código de recuperación
    @POST("api/usuarios/cambiar-contrasena")
    Call<ResponseBody> cambiarContrasena(@Body Map<String, String> request);
}
