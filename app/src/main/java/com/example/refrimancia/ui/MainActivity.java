package com.example.refrimancia.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;

import com.example.refrimancia.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        // Configurar la ventana para que los fragmentos se ajusten al diseño del sistema
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        // Cargar el fragmento contenedor principal solo si no se restaura un estado guardado
        if (estadoGuardado == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ContenedorPrincipalFragment())
                    .commit();
        }

        // Configurar manejador de la tecla de retroceso
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Obtener el gestor de fragmentos y navegar hacia atrás en la pila
                FragmentManager gestorFragmentos = getSupportFragmentManager();
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
