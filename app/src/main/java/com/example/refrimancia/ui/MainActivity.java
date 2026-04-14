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
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);

        if (estadoGuardado == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ContenedorPrincipalFragment())
                    .commit();
        }

        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
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
