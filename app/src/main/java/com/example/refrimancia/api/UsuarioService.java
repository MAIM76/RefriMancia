package com.example.refrimancia.api;

import com.example.refrimancia.model.entity.Usuario;
import com.example.refrimancia.model.request.CambiarPassRequest;
import com.example.refrimancia.model.request.CorreoRequest;
import com.example.refrimancia.model.request.LoginRequest;
import com.example.refrimancia.model.response.Estado;
import com.example.refrimancia.model.response.Login;
import com.example.refrimancia.model.response.Registro;
import com.example.refrimancia.model.response.RespuestaPaginada;
import com.example.refrimancia.model.response.RespuestaUnica;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioService {
    
    // ======================== ENDPOINTS DE AUTENTICACIÓN ========================

    @POST("api/usuarios/login")
    Call<Login> login(@Body LoginRequest request);

    // Invalida el token actual en el servidor
    @POST("api/usuarios/logout")
    Call<ResponseBody> logout();
    
    // ======================== ENDPOINTS DE GESTIÓN DE USUARIOS ========================
    
    @Multipart
    @POST("api/usuarios/crear")
    Call<Registro> registrarUsuario(
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("nombre_completo") RequestBody nombreCompleto,
            @Part("correo_electronico") RequestBody correoElectronico,
            @Part("fecha_nac") RequestBody fechaNacimiento,
            @Part MultipartBody.Part imagenPerfil
    );

    // Requiere token válido; elimina la cuenta del usuario autenticado
    @DELETE("api/usuarios/eliminar")
    Call<ResponseBody> eliminarCuenta();

    @GET("api/usuarios/perfil")
    Call<RespuestaUnica<Usuario>> obtenerPerfil();

    @Multipart
    @PUT("api/usuarios/modificar/{id}")
    Call<ResponseBody> modificarUsuario(
            @Path("id") int id,
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("nombre_completo") RequestBody nombreCompleto,
            @Part("correo_electronico") RequestBody correoElectronico,
            @Part("fecha_nac") RequestBody fechaNacimiento,
            @Part MultipartBody.Part imagenPerfil
    );

    // Solo accesible con rol administrador
    @GET("api/usuarios/listar")
    Call<RespuestaPaginada<Usuario>> listarUsuarios(@Query("page") Integer page);
    
    // ======================== ENDPOINTS DE RECUPERACIÓN DE CONTRASEÑA ========================
    
    @POST("api/usuarios/solicitar-codigo")
    Call<Estado> solicitarCodigo(@Body CorreoRequest request);

    @POST("api/usuarios/cambiar-contrasena")
    Call<Estado> cambiarContrasena(@Body CambiarPassRequest request);
}
