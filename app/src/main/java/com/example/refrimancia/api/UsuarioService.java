package com.example.refrimancia.api;

import com.example.refrimancia.modelo.entidad.Usuario;
import com.example.refrimancia.modelo.response.RespuestaPaginada;
import com.example.refrimancia.modelo.response.RespuestaUnica;

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

/**
 * Interfaz de servicio Retrofit para la gestión de usuarios.
 * Proporciona endpoints para autenticación, registro, gestión de perfiles
 * y recuperación de contraseñas en la aplicación RefriMancia.
 */
public interface UsuarioService {
    
    // ======================== ENDPOINTS DE AUTENTICACIÓN ========================
    
    /**
     * Cierra la sesión del usuario autenticado.
     * Invalida el token actual en el servidor.
     * @return ResponseBody con el resultado de la operación
     */
    @POST("api/usuarios/logout")
    Call<ResponseBody> logout();
    
    // ======================== ENDPOINTS DE GESTIÓN DE USUARIOS ========================
    
    /**
     * Crea un nuevo usuario con imagen de perfil.
     * Utiliza multipart/form-data para enviar tanto los datos como la imagen.
     * 
     * @param nombreUsuario Nombre de usuario único
     * @param contrasena Contraseña del usuario
     * @param nombreCompleto Nombre completo del usuario
     * @param correoElectronico Correo electrónico válido
     * @param fechaNacimiento Fecha de nacimiento del usuario
     * @param imagenPerfil Imagen de perfil (opcional)
     * @return ResponseBody con el resultado de la operación
     */
    @Multipart
    @POST("api/usuarios/crear")
    Call<ResponseBody> crearUsuario(
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("contrasena") RequestBody contrasena,
            @Part("nombre_completo") RequestBody nombreCompleto,
            @Part("correo_electronico") RequestBody correoElectronico,
            @Part("fecha_nac") RequestBody fechaNacimiento,
            @Part MultipartBody.Part imagenPerfil
    );

    /**
     * Obtiene los datos del perfil del usuario autenticado.
     * Requiere estar autenticado con un token válido.
     * @return Respuesta única con los datos del perfil
     */
    @GET("api/usuarios/perfil")
    Call<RespuestaUnica<Usuario>> obtenerPerfil();

    /**
     * Modifica los datos de un usuario existente.
     * Utiliza multipart/form-data para enviar tanto los datos como la imagen.
     * 
     * @param id ID del usuario a modificar
     * @param nombreUsuario Nuevo nombre de usuario
     * @param contrasena Nueva contraseña (opcional)
     * @param nombreCompleto Nuevo nombre completo
     * @param correoElectronico Nuevo correo electrónico
     * @param fechaNacimiento Nueva fecha de nacimiento
     * @param imagenPerfil Nueva imagen de perfil (opcional)
     * @return ResponseBody con el resultado de la operación
     */
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

    /**
     * Lista todos los usuarios con soporte de paginación.
     * Requiere privilegios de administrador.
     * @param page Número de página (opcional)
     * @return Respuesta paginada con la lista de usuarios
     */
    @GET("api/usuarios/listar")
    Call<RespuestaPaginada<Usuario>> listarUsuarios(@Query("page") Integer page);
    
    // ======================== ENDPOINTS DE RECUPERACIÓN DE CONTRASEÑA ========================
    
    /**
     * Solicita un código de recuperación de contraseña.
     * Envía un código al correo electrónico del usuario.
     * 
     * @param request Map con el correo electrónico del usuario
     * @return ResponseBody con el resultado de la operación
     */
    @POST("api/usuarios/solicitar-codigo")
    Call<ResponseBody> solicitarCodigo(@Body Map<String, String> request);

    /**
     * Cambia la contraseña usando un código de recuperación.
     * Valida el código y actualiza la contraseña del usuario.
     * 
     * @param request Map con el código de recuperación y la nueva contraseña
     * @return ResponseBody con el resultado de la operación
     */
    @POST("api/usuarios/cambiar-contrasena")
    Call<ResponseBody> cambiarContrasena(@Body Map<String, String> request);
}
