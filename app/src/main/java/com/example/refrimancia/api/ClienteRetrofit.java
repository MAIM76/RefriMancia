package com.example.refrimancia.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.refrimancia.utils.DatosEjemplo;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.example.refrimancia.modelo.AuthToken;

public class ClienteRetrofit {
    // URL base del servidor backend
    private static final String URL_BASE = "https://refrimacia-backend.onrender.com/";
    // Instancia única de Retrofit (patrón Singleton)
    private static Retrofit instancia;

    public static Retrofit obtenerInstancia() {
        if (instancia == null) {
            // Configurar interceptor de logging HTTP (para desarrollo y depuración)
            // Registra el cuerpo completo de las peticiones HTTP
            HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
            interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder p = chain.request().newBuilder();
                    if (DatosEjemplo.tokenActual != null) {
                        p.addHeader("Authorization", "Bearer " + DatosEjemplo.tokenActual);
                    }
                    Response response = chain.proceed(p.build());
                    
                    if (response.code() == 401) { // Unauthorized, token might be expired
                        response.close();
                        UsuarioService usuarioService = new Retrofit.Builder()
                            .baseUrl(URL_BASE)
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                            .build()
                            .create(UsuarioService.class);
                        
                        try {
                            retrofit2.Response<AuthToken> authResponse = usuarioService.login(DatosEjemplo.obtenerCredencialesPorDefecto()).execute();
                            if (authResponse.isSuccessful() && authResponse.body() != null) {
                                DatosEjemplo.tokenActual = authResponse.body().getToken();
                                DatosEjemplo.usuarioActual = authResponse.body().getUsuario();
                                
                                // Retry the failed request with the new token
                                Request.Builder newP = chain.request().newBuilder();
                                newP.addHeader("Authorization", "Bearer " + DatosEjemplo.tokenActual);
                                return chain.proceed(newP.build());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    return response;
                }
            };

            // Configurar cliente OkHttp con el interceptor de logging
            OkHttpClient clienteOkHttp = new OkHttpClient.Builder()
                    .addInterceptor(interceptorLog)
                    .addInterceptor(authInterceptor)
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
