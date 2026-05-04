package com.example.refrimancia.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.refrimancia.CreateRecipeActivity;
import com.example.refrimancia.R;
import com.example.refrimancia.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContenedorPrincipalActivity extends AppCompatActivity {

    private static final String TAG_INICIO = "frag_inicio";
    private static final String TAG_USUARIO = "frag_usuario";
    private InicioFragment inicioFragment;
    private UsuarioFragment usuarioFragment;

    private final BroadcastReceiver sessionExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ClienteRetrofit.ACTION_SESSION_EXPIRED.equals(intent.getAction())) {
                Intent loginIntent = new Intent(ContenedorPrincipalActivity.this, com.example.refrimancia.LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.fetchAuthToken();
        if (token == null || token.isEmpty()) {
            Intent loginIntent = new Intent(this, com.example.refrimancia.LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_contenedor_principal);

        inicioFragment = (InicioFragment) getSupportFragmentManager().findFragmentByTag(TAG_INICIO);
        usuarioFragment = (UsuarioFragment) getSupportFragmentManager().findFragmentByTag(TAG_USUARIO);

        if (inicioFragment == null) {
            inicioFragment = new InicioFragment();
        }
        if (usuarioFragment == null) {
            usuarioFragment = new UsuarioFragment();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.sub_fragment_container, inicioFragment, TAG_INICIO)
                    .add(R.id.sub_fragment_container, usuarioFragment, TAG_USUARIO)
                    .hide(usuarioFragment)
                    .commit();
        }

        BottomNavigationView navInferior = findViewById(R.id.bottom_navigation);
        navInferior.setSelectedItemId(R.id.nav_home);
        navInferior.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(usuarioFragment)
                        .show(inicioFragment)
                        .commit();
                return true;
            }
            if (id == R.id.nav_create) {
                abrirCrearRecetaConSesion();
                return true;
            }
            if (id == R.id.nav_user) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(inicioFragment)
                        .show(usuarioFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ClienteRetrofit.ACTION_SESSION_EXPIRED);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sessionExpiredReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
//            registerReceiver(sessionExpiredReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(sessionExpiredReceiver);
        } catch (IllegalArgumentException ignored) {
            // Receiver ya estaba desregistrado.
        }
        super.onStop();
    }

    private void abrirCrearRecetaConSesion() {
        SessionManager sessionManager = new SessionManager(this);
        int idUsuario = sessionManager.fetchUserId();
        String token = sessionManager.fetchAuthToken();

        if (idUsuario <= 0 || token == null || token.isEmpty()) {
            Toast.makeText(this, "No se pudo obtener la sesión del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CreateRecipeActivity.class);
        intent.putExtra("ID_USUARIO", idUsuario);
        intent.putExtra("TOKEN", token);
        startActivity(intent);
    }
}
