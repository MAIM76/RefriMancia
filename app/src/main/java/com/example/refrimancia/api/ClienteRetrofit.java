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

public class ClienteRetrofit {

    // ======================== CONSTANTES ========================

    private static final String URL_BASE = "https://refrimacia-backend.onrender.com/";

    // ======================== VARIABLES DE INSTANCIA ========================

    private static Retrofit instancia;

    // Evita múltiples redirects simultáneos al login cuando el token es invalidado
    private static final AtomicBoolean sesionInvalidadaHandled = new AtomicBoolean(false);

    // ======================== MÉTODOS PÚBLICOS ========================

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
    
    private static HttpLoggingInterceptor crearInterceptorLogging() {
        HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
        interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptorLog;
    }
    
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

    // Token invalidado por login desde otro dispositivo; se ejecuta una sola vez gracias al AtomicBoolean
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
