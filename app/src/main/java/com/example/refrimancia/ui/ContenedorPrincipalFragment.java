package com.example.refrimancia.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.refrimancia.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContenedorPrincipalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle estadoGuardado) {
        return inflater.inflate(R.layout.contenedor_principal_fragment, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);

        BottomNavigationView navInferior = vista.findViewById(R.id.bottom_navigation);

        // Cargar FragmentoInicio por defecto
        if (estadoGuardado == null) {
            cargarSubFragmento(new InicioFragment());
            navInferior.setSelectedItemId(R.id.nav_home);
        }

        navInferior.setOnItemSelectedListener(elemento -> {
            int id = elemento.getItemId();
            if (id == R.id.nav_home) {
                cargarSubFragmento(new InicioFragment());
                return true;
            } else if (id == R.id.nav_user) {
                cargarSubFragmento(new UsuarioFragment());
                return true;
            } else if (id == R.id.nav_create) {
                // TODO: Fragment de creación de receta (pendiente de otro miembro del equipo)
                return false;
            }
            return false;
        });
    }

    private void cargarSubFragmento(Fragment fragmento) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.sub_fragment_container, fragmento)
                .commit();
    }
}
