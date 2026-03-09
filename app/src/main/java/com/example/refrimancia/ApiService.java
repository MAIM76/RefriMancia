package com.example.refrimancia;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/usuarios/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
