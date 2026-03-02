package com.example.refrimancia.ui;

import android.os.Bundle;
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
import com.example.refrimancia.modelo.Receta;

import java.util.ArrayList;
import java.util.List;

public class FragmentoInicio extends Fragment {

    private AdaptadorReceta adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle estadoGuardado) {
        return inflater.inflate(R.layout.fragmento_inicio, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);

        RecyclerView listaRecetas = vista.findViewById(R.id.recipes_recycler_view);
        SearchView barraBusqueda = vista.findViewById(R.id.search_view);

        // Datos de ejemplo
        List<Receta> recetas = construirRecetasEjemplo();

        adaptador = new AdaptadorReceta(recetas);
        adaptador.setEscuchadorClicReceta(receta ->
                Toast.makeText(getContext(), receta.getTitulo(), Toast.LENGTH_SHORT).show()
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
    }

    private List<Receta> construirRecetasEjemplo() {
        List<Receta> lista = new ArrayList<>();
        lista.add(new Receta("Tortilla española",
                "Clásica tortilla de patata con huevo y cebolla", 0));
        lista.add(new Receta("Gazpacho andaluz",
                "Sopa fría de tomate perfecta para el verano", 0));
        lista.add(new Receta("Paella valenciana",
                "Arroz con pollo, conejo y verduras al azafrán", 0));
        lista.add(new Receta("Croquetas caseras",
                "Croquetas cremosas de jamón serrano", 0));
        lista.add(new Receta("Pisto manchego",
                "Guiso de verduras de temporada con tomate", 0));
        lista.add(new Receta("Salmorejo cordobés",
                "Crema espesa de tomate con pan y ajo", 0));
        lista.add(new Receta("Cocido madrileño",
                "Guiso tradicional de garbanzos con carne", 0));
        lista.add(new Receta("Churros con chocolate",
                "Churros crujientes con chocolate a la taza", 0));
        return lista;
    }
}

