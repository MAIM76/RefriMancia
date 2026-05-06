package com.example.refrimancia.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.refrimancia.CreateRecipeActivity;
import com.example.refrimancia.R;
import com.example.refrimancia.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Actividad principal que contiene el contenedor de la aplicación.
 * Gestiona la navegación entre fragmentos principales y maneja la sesión del usuario.
 * Incluye navegación inferior con acceso a Inicio, Crear Receta y Perfil de Usuario.
 */
public class ContenedorPrincipalActivity extends AppCompatActivity
        implements UsuarioFragment.OnFotoPerfilCargadaListener {

    // ======================== CONSTANTES ========================
    
    /** Tag para identificar el fragmento de inicio */
    private static final String TAG_INICIO = "frag_inicio";
    
    /** Tag para identificar el fragmento de usuario */
    private static final String TAG_USUARIO = "frag_usuario";
    
    // ======================== VARIABLES DE INSTANCIA ========================
    
    /** Fragmento principal de inicio */
    private InicioFragment inicioFragment;
    
    /** Fragmento del perfil de usuario */
    private UsuarioFragment usuarioFragment;
    
    /** Referencia a la barra de navegación inferior */
    private BottomNavigationView navInferior;

    /** BroadcastReceiver para manejar sesiones expiradas */
    private final BroadcastReceiver sessionExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ClienteRetrofit.ACTION_SESSION_EXPIRED.equals(intent.getAction())) {
                manejarSesionExpirada();
            }
        }
    };

    // ======================== MÉTODOS DEL CICLO DE VIDA ========================
    
    /**
     * Inicializa la actividad y verifica la sesión del usuario.
     * Si no hay sesión válida, redirige al login.
     */
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
    
    /**
     * Registra el BroadcastReceiver para sesiones expiradas.
     */
    @Override
    protected void onStart() {
        super.onStart();
        registrarReceptorSesion();
    }

    /**
     * Desregistra el BroadcastReceiver para evitar memory leaks.
     */
    @Override
    protected void onStop() {
        desregistrarReceptorSesion();
        super.onStop();
    }

    // ======================== MÉTODOS DE CONFIGURACIÓN ========================
    
    /**
     * Verifica si existe una sesión válida del usuario.
     * @return true si la sesión es válida, false si redirige al login
     */
    private boolean verificarSesion() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.fetchAuthToken();
        
        if (token == null || token.isEmpty()) {
            redirigirALogin();
            return false;
        }
        return true;
    }
    
    /**
     * Configura la vista principal y los fragmentos.
     */
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
    
    /**
     * Configura la navegación inferior y sus listeners.
     */
    @Override
    public void onFotoPerfilCargada(String url) {
        actualizarIconoUsuario(url);
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
                return true;
            }
            if (id == R.id.nav_user) {
                mostrarFragmentoUsuario();
                return true;
            }
            return false;
        });
    }
    
    // ======================== MÉTODOS DE NAVEGACIÓN ========================
    
    /**
     * Muestra el fragmento de inicio y oculta el de usuario.
     */
    private void mostrarFragmentoInicio() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(usuarioFragment)
                .show(inicioFragment)
                .commit();
    }
    
    /**
     * Muestra el fragmento de usuario y oculta el de inicio.
     */
    private void mostrarFragmentoUsuario() {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(inicioFragment)
                .show(usuarioFragment)
                .commit();
    }
    
    /**
     * Abre la actividad de crear receta verificando la sesión.
     */
    private void abrirCrearRecetaConSesion() {
        SessionManager sessionManager = new SessionManager(this);
        int idUsuario = sessionManager.fetchUserId();
        String token = sessionManager.fetchAuthToken();

        if (idUsuario <= 0 || token == null || token.isEmpty()) {
            Toast.makeText(this, R.string.error_session_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CreateRecipeActivity.class);
        intent.putExtra("ID_USUARIO", idUsuario);
        intent.putExtra("TOKEN", token);
        startActivity(intent);
    }
    
    // ======================== MÉTODOS DE SESIÓN ========================
    
    /**
     * Registra el BroadcastReceiver para manejar sesiones expiradas.
     */
    private void registrarReceptorSesion() {
        IntentFilter filter = new IntentFilter(ClienteRetrofit.ACTION_SESSION_EXPIRED);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sessionExpiredReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(sessionExpiredReceiver, filter);
        }
    }
    
    /**
     * Desregistra el BroadcastReceiver de forma segura.
     */
    private void desregistrarReceptorSesion() {
        try {
            unregisterReceiver(sessionExpiredReceiver);
        } catch (IllegalArgumentException ignored) {
            // Receiver ya estaba desregistrado, no es un error.
        }
    }
    
    /**
     * Maneja el caso de sesión expirada redirigiendo al login.
     */
    private void manejarSesionExpirada() {
        redirigirALogin();
    }
    
    /**
     * Redirige a la actividad de login limpiando el stack de actividades.
     */
    private void redirigirALogin() {
        Intent loginIntent = new Intent(this, com.example.refrimancia.LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
