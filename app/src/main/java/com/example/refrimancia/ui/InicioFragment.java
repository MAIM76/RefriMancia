package com.example.refrimancia.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refrimancia.R;
import com.example.refrimancia.adaptador.AdaptadorReceta;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.modelo.Receta;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {

    private AdaptadorReceta adaptador;
    private RecyclerView listaRecetas;
    private static final String TAG = "InicioFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle estadoGuardado) {
        return inflater.inflate(R.layout.inicio_fragment, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);

        listaRecetas = vista.findViewById(R.id.recipes_recycler_view);
        SearchView barraBusqueda = vista.findViewById(R.id.search_view);

        // Inicializar adaptador con lista vacía
        adaptador = new AdaptadorReceta(new ArrayList<>());
        adaptador.setEscuchadorClicReceta(receta ->
                Toast.makeText(getContext(), receta.getTituloReceta(), Toast.LENGTH_SHORT).show()
        );

        listaRecetas.setLayoutManager(new LinearLayoutManager(getContext()));
        listaRecetas.setAdapter(adaptador);

        // Filtrado con la barra de búsqueda
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                adaptador.filtrar(consulta);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String nuevoTexto) {
                adaptador.filtrar(nuevoTexto);
                return true;
            }
        });

        // Cargar recetas desde la API
        cargarRecetasDesdeAPI();
    }

    private void cargarRecetasDesdeAPI() {
        RecetaService servicio = ClienteRetrofit.obtenerInstancia().create(RecetaService.class);
        
        Call<List<Receta>> llamada = servicio.obtenerRecetas();
        
        llamada.enqueue(new Callback<List<Receta>>() {
            @Override
            public void onResponse(Call<List<Receta>> call, Response<List<Receta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Receta> recetas = response.body();
                    Log.d(TAG, "Recetas cargadas: " + recetas.size());
                    adaptador.actualizarDatos(recetas);
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                    mostrarMensajeError("Error al cargar recetas");
                    cargarRecetasEjemplo();
                }
            }

            @Override
            public void onFailure(Call<List<Receta>> call, Throwable error) {
                Log.e(TAG, "Error en la llamada API: " + error.getMessage());
                mostrarMensajeError("Error de conexión");
                cargarRecetasEjemplo();
            }
        });
    }

    private void mostrarMensajeError(String mensaje) {
        if (getContext() != null) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
        }
    }

    private void cargarRecetasEjemplo() {
        List<Receta> recetas = new ArrayList<>();
        recetas.add(new Receta("Tortilla española",
                "Clásica tortilla de patata con huevo y cebolla"));
        recetas.add(new Receta("Gazpacho andaluz",
                "Sopa fría de tomate perfecta para el verano"));
        recetas.add(new Receta("Paella valenciana",
                "Arroz con pollo, conejo y verduras al azafrán"));
        recetas.add(new Receta("Croquetas caseras",
                "Croquetas cremosas de jamón serrano"));
        recetas.add(new Receta("Pisto manchego",
                "Guiso de verduras de temporada con tomate"));
        recetas.add(new Receta("Salmorejo cordobés",
                "Crema espesa de tomate con pan y ajo"));
        recetas.add(new Receta("Cocido madrileño",
                "Guiso tradicional de garbanzos con carne"));
        recetas.add(new Receta("Churros con chocolate",
                "Churros crujientes con chocolate a la taza"));
        recetas.add(new Receta("Empanadas argentinas",
                "Masa rellena de carne, cebolla y especias"));
        recetas.add(new Receta("Tarta de Santiago",
                "Tarta de almendra típica de Galicia"));
        recetas.add(new Receta("Fabada asturiana",
                "Guiso de judías con chorizo y morcilla"));
        recetas.add(new Receta("Pimientos de Padrón",
                "Pimientos pequeños fritos con sal gruesa"));
        recetas.add(new Receta("Torrijas",
                "Rebanadas de pan empapadas en leche y fritas"));
        recetas.add(new Receta("Pulpo a la gallega",
                "Pulpo cocido con pimentón y aceite de oliva"));
        recetas.add(new Receta("Ensalada campera",
                "Ensalada fresca con patata, huevo, atún y verduras"));
        recetas.add(new Receta("Bacalao al pil-pil",
                "Bacalao cocinado con aceite, ajo y guindilla"));
        recetas.add(new Receta("Albondigas en salsa",
                "Albóndigas de carne en salsa de tomate casera"));
        recetas.add(new Receta("Tarta de queso",
                "Tarta cremosa de queso con base de galleta"));
        
        adaptador.actualizarDatos(recetas);
    }
}
