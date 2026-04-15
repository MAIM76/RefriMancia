package com.example.refrimancia.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    // URL base del servidor backend
    private static final String URL_BASE = "http://refrimacia-backend.onrender.com/";
    // Instancia única de Retrofit (patrón Singleton)
    private static Retrofit instancia;

    public static Retrofit obtenerInstancia() {
        if (instancia == null) {
            // Configurar interceptor de logging HTTP (para desarrollo y depuración)
            // Registra el cuerpo completo de las peticiones HTTP
            HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
            interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar cliente OkHttp con el interceptor de logging
            OkHttpClient clienteOkHttp = new OkHttpClient.Builder()
                    .addInterceptor(interceptorLog)
                    .build();

            // Configurar Gson para deserialización/serialización de JSON
            // setLenient() permite parsear JSON no estricto
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Crear instancia de Retrofit con configuración del cliente
            instancia = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(clienteOkHttp)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return instancia;
    }
}
