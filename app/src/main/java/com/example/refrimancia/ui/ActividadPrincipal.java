package com.example.refrimancia.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.refrimancia.R;

public class ActividadPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.actividad_principal);

        if (estadoGuardado == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FragmentoContenedorPrincipal())
                    .commit();
        }

        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                androidx.fragment.app.FragmentManager gestorFragmentos = getSupportFragmentManager();
                if (gestorFragmentos.getBackStackEntryCount() > 0) {
                    gestorFragmentos.popBackStack();
                } else {
                    setEnabled(false);
                    dispatcher.onBackPressed();
                }
            }
        });
    }
}


