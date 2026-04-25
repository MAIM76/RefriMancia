package com.example.refrimancia.api;

import android.content.Context;

import com.example.refrimancia.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    private static final String URL_BASE = "https://refrimacia-backend.onrender.com/";
    private static Retrofit instancia;

    public static Retrofit obtenerInstancia(Context context) {
        if (instancia == null) {
            SessionManager sessionManager = new SessionManager(context.getApplicationContext());

            HttpLoggingInterceptor interceptorLog = new HttpLoggingInterceptor();
            interceptorLog.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request.Builder requestBuilder = originalRequest.newBuilder();

                    String token = sessionManager.fetchAuthToken();
                    if (token != null && !token.isEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                    }

                    Response response = chain.proceed(requestBuilder.build());
                    if (response.code() == 401) {
                        sessionManager.clearSession();
                        if (token != null) {
                            response.close();
                        }
                    }

                    return response;
                }
            };

            OkHttpClient clienteOkHttp = new OkHttpClient.Builder()
                    .addInterceptor(interceptorLog)
                    .addInterceptor(authInterceptor)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            instancia = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(clienteOkHttp)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return instancia;
    }
}
