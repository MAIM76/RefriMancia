package com.example.refrimancia.api;

import com.example.refrimancia.modelo.Receta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecetaService {
    @GET("api/recetas/listar")
    Call<List<Receta>> obtenerRecetas();
}

