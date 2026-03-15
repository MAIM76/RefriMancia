package com.example.refrimancia.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    private static final String URL_BASE = "http://refrimacia-backend.onrender.com/";
    private static Retrofit instancia;

    public static Retrofit obtenerInstancia() {
        if (instancia == null) {
            // Configurar logging HTTP (para desarrollo)
            HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
            interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar OkHttpClient
            OkHttpClient clienteOkHttp = new OkHttpClient.Builder()
                    .addInterceptor(interceptorLog)
                    .build();

            // Configurar Gson
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Crear Retrofit
            instancia = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(clienteOkHttp)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return instancia;
    }
}

