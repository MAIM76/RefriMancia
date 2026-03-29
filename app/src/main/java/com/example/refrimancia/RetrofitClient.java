package com.example.refrimancia;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://refrimacia-backend.onrender.com/";

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            final Context appContext = context.getApplicationContext();
            SessionManager sessionManager = new SessionManager(appContext);

            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    
                    Request.Builder builder = originalRequest.newBuilder();
                    
                    // No enviamos el token si estamos intentando hacer login
                    if (!originalRequest.url().encodedPath().contains("/login")) {
                        String token = sessionManager.fetchAuthToken();
                        if (token != null) {
                            builder.header("Authorization", "Bearer " + token);
                        }
                    }

                    Response response = chain.proceed(builder.build());

                    // --- PUNTO 1: GESTIÓN DE ERROR 401 (Sesión Expirada) ---
                    if (response.code() == 401 && !originalRequest.url().encodedPath().contains("/login")) {
                        // Limpiamos la sesión
                        sessionManager.clearSession();

                        // Notificamos al usuario y redirigimos al login
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(appContext, "Sesión expirada. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(appContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            appContext.startActivity(intent);
                        });
                    }

                    return response;
                }
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
