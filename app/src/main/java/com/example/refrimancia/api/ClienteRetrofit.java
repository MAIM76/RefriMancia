package com.example.refrimancia.api;

import android.content.Context;
import android.content.Intent;

import com.example.refrimancia.ui.LoginActivity;
import com.example.refrimancia.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente Retrofit para la comunicación con la API del backend.
 * Proporciona una instancia singleton de Retrofit configurada con:
 * - Autenticación mediante tokens Bearer
 * - Logging de peticiones HTTP
 * - Manejo automático de sesiones inválidas (401)
 * - Configuración Gson para parsing JSON
 */
public class ClienteRetrofit {
    
    // ======================== CONSTANTES ========================
    
    /** URL base del servidor backend */
    private static final String URL_BASE = "https://refrimacia-backend.onrender.com/";
    
    // ======================== VARIABLES DE INSTANCIA ========================

    /** Instancia singleton de Retrofit */
    private static Retrofit instancia;

    /** Evita múltiples redirects simultáneos al login cuando el token es invalidado */
    private static final AtomicBoolean sesionInvalidadaHandled = new AtomicBoolean(false);

    // ======================== MÉTODOS PÚBLICOS ========================
    
    /**
     * Obtiene la instancia singleton de Retrofit configurada.
     * Si no existe, crea una nueva instancia con toda la configuración necesaria.
     * 
     * @param context Contexto de la aplicación para acceder a SessionManager
     * @return Instancia de Retrofit configurada y lista para usar
     */
    public static Retrofit obtenerInstancia(Context context) {
        if (instancia == null) {
            instancia = crearInstanciaRetrofit(context);
        }
        return instancia;
    }

    public static void resetInstancia() {
        instancia = null;
        sesionInvalidadaHandled.set(false);
    }
    
    // ======================== MÉTODOS PRIVADOS ========================
    
    /**
     * Crea una nueva instancia de Retrofit con toda la configuración necesaria.
     * 
     * @param context Contexto de la aplicación
     * @return Nueva instancia de Retrofit configurada
     */
    private static Retrofit crearInstanciaRetrofit(Context context) {
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());

        // Configurar interceptores
        OkHttpClient clienteOkHttp = new OkHttpClient.Builder()
                .addInterceptor(crearInterceptorLogging())
                .addInterceptor(crearInterceptorAuth(context, sessionManager))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Configurar Gson
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Crear instancia de Retrofit
        return new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(clienteOkHttp)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    
    /**
     * Crea el interceptor para logging de peticiones HTTP.
     * 
     * @return Interceptor de logging configurado
     */
    private static HttpLoggingInterceptor crearInterceptorLogging() {
        HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
        interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptorLog;
    }
    
    /**
     * Crea el interceptor de autenticación que añade el token Bearer a cada petición.
     *
     * @param context Contexto de la aplicación
     * @param sessionManager Gestor de sesiones
     * @return Interceptor de autenticación configurado
     */
    private static Interceptor crearInterceptorAuth(Context context, SessionManager sessionManager) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder();

                // Añadir token de autenticación si existe
                String token = sessionManager.fetchAuthToken();
                if (token != null && !token.isEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                }

                Response response = chain.proceed(requestBuilder.build());

                if (response.code() == 401) {
                    manejarTokenInvalidado(context, sessionManager);
                }

                return response;
            }
        };
    }

    /**
     * Limpia la sesión y redirige al login cuando el servidor rechaza el token (401).
     * Ocurre cuando el token es invalidado por un login desde otro dispositivo.
     * Usa AtomicBoolean para ejecutarse una sola vez ante peticiones simultáneas.
     */
    private static void manejarTokenInvalidado(Context context, SessionManager sessionManager) {
        if (!sesionInvalidadaHandled.compareAndSet(false, true)) {
            return;
        }
        sessionManager.clearSession();
        Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.getApplicationContext().startActivity(intent);
    }
}
