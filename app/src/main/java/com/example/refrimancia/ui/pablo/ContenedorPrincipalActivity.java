package com.example.refrimancia.ui.pablo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.refrimancia.ui.CreateRecipeActivity;
import com.example.refrimancia.R;
import com.example.refrimancia.util.SessionManager;
import com.example.refrimancia.ui.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContenedorPrincipalActivity extends AppCompatActivity
        implements UsuarioFragment.OnFotoListener,
                   UsuarioFragment.OnRecetaListener,
                   InicioFragment.OnRecetaListener {

    // ======================== CONSTANTES ========================

    private static final String TAG_INICIO = "frag_inicio";
    private static final String TAG_USUARIO = "frag_usuario";

    // ======================== VARIABLES DE INSTANCIA ========================

    private InicioFragment inicioFragment;
    private UsuarioFragment usuarioFragment;
    private BottomNavigationView navInferior;

    private final ActivityResultLauncher<Intent> crearRecetaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (inicioFragment != null) inicioFragment.recargar();
                    if (usuarioFragment != null) usuarioFragment.recargar();
                }
            }
    );

    // ======================== CICLO DE VIDA ========================

    @Override
    protected void onResume() {
        super.onResume();
        if (navInferior == null) return;
        if (usuarioFragment != null && usuarioFragment.isVisible()) {
            navInferior.setSelectedItemId(R.id.nav_user);
        } else {
            navInferior.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar sesión antes de continuar
        if (!verificarSesion()) {
            return;
        }

        // Configurar la vista principal
        configurarVistaPrincipal();
        
        // Configurar navegación inferior
        configurarNavegacionInferior();
    }

    // ======================== MÉTODOS DE CONFIGURACIÓN ========================

    private boolean verificarSesion() {
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isSessionValid()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }
    
    private void configurarVistaPrincipal() {
        setContentView(R.layout.activity_contenedor_principal);

        // Obtener o crear fragmentos
        inicioFragment = (InicioFragment) getSupportFragmentManager().findFragmentByTag(TAG_INICIO);
        usuarioFragment = (UsuarioFragment) getSupportFragmentManager().findFragmentByTag(TAG_USUARIO);

        if (inicioFragment == null) {
            inicioFragment = new InicioFragment();
        }
        if (usuarioFragment == null) {
            usuarioFragment = new UsuarioFragment();
        }

        // Agregar fragmentos si es la primera vez
        if (getSupportFragmentManager().findFragmentById(R.id.sub_fragment_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.sub_fragment_container, inicioFragment, TAG_INICIO)
                    .add(R.id.sub_fragment_container, usuarioFragment, TAG_USUARIO)
                    .hide(usuarioFragment)
                    .commit();
        }
    }

    // ======================== MÉTODOS DE FOTO DE PERFIL ========================

    @Override
    public void onFotoPerfilCargada(String url) {
        actualizarIconoUsuario(url);
    }

    @Override
    public void onRecetaCambiada() {
        if (inicioFragment != null) inicioFragment.recargar();
        if (usuarioFragment != null) usuarioFragment.recargar();
    }

    private void actualizarIconoUsuario(String url) {
        if (navInferior == null) return;
        int size = (int) (40 * getResources().getDisplayMetrics().density);
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .circleCrop()
                    .into(new CustomTarget<Bitmap>(size, size) {
                        @Override
                        public void onResourceReady(Bitmap resource,
                                Transition<? super Bitmap> transition) {
                            RoundedBitmapDrawable drawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            drawable.setCircular(true);
                            navInferior.getMenu().findItem(R.id.nav_user).setIcon(drawable);
                        }
                        @Override
                        public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                    });
        } else {
            navInferior.getMenu().findItem(R.id.nav_user)
                    .setIcon(android.R.drawable.ic_menu_myplaces);
        }
    }

    // ======================== MÉTODOS DE NAVEGACIÓN ========================

    private void configurarNavegacionInferior() {
        navInferior = findViewById(R.id.bottom_navigation);
        navInferior.setSelectedItemId(R.id.nav_home);

        String urlFotoGuardada = new SessionManager(this).fetchUserPhoto();
        actualizarIconoUsuario(urlFotoGuardada);
        
        navInferior.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_home) {
                mostrarFragmentoInicio();
                return true;
            }
            if (id == R.id.nav_create) {
                abrirCrearRecetaConSesion();
                return false;
            }
            if (id == R.id.nav_user) {
                mostrarFragmentoUsuario();
                return true;
            }
            return false;
        });
    }
    
    private void mostrarFragmentoInicio() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(usuarioFragment)
                .show(inicioFragment)
                .commit();
    }
    
    private void mostrarFragmentoUsuario() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(inicioFragment)
                .show(usuarioFragment)
                .commit();
    }
    
    private void abrirCrearRecetaConSesion() {
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isSessionValid()) {
            Toast.makeText(this, R.string.error_session_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        crearRecetaLauncher.launch(new Intent(this, CreateRecipeActivity.class));
    }
    
}
