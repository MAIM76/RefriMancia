package com.example.refrimancia.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.refrimancia.R;
import com.example.refrimancia.modelo.Usuario;

public class UsuarioFragment extends Fragment {

    private static final String TAG = "UsuarioFragment";
    
    private ImageView imagenPerfil;
    private TextView nombreCompleto;
    private TextView nombreUsuario;
    private TextView correoElectronico;
    private TextView fechaNacimiento;
    private View contenidoVacio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle estadoGuardado) {
        return inflater.inflate(R.layout.usuario_fragment, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);

        inicializarVistas(vista);
        cargarDatosUsuario();
    }

    private void inicializarVistas(View vista) {
        imagenPerfil = vista.findViewById(R.id.profile_image);
        nombreCompleto = vista.findViewById(R.id.nombre_completo);
        nombreUsuario = vista.findViewById(R.id.nombre_usuario);
        correoElectronico = vista.findViewById(R.id.correo_electronico);
        fechaNacimiento = vista.findViewById(R.id.fecha_nacimiento);
        contenidoVacio = vista.findViewById(R.id.empty_state);
    }

    private void cargarDatosUsuario() {
        // Datos de ejemplo - en el futuro se cargarán desde la API o SharedPreferences
        Usuario usuarioEjemplo = new Usuario(
            "chef_pablo",
            "Pablo García",
            "pablo.garcia@ejemplo.com"
        );
        usuarioEjemplo.setFechaNacimiento("1990-05-15");

        mostrarDatosUsuario(usuarioEjemplo);
    }

    private void mostrarDatosUsuario(Usuario usuario) {
        if (usuario != null) {
            nombreCompleto.setText(usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : "Usuario");
            nombreUsuario.setText("@" + (usuario.getNombreUsuario() != null ? usuario.getNombreUsuario() : "usuario"));
            correoElectronico.setText(usuario.getCorreoElectronico() != null ? usuario.getCorreoElectronico() : "correo@ejemplo.com");
            fechaNacimiento.setText(usuario.getFechaNacimiento() != null ? usuario.getFechaNacimiento() : "No disponible");

            // TODO: Cargar imagen de perfil con Glide si está disponible
            // imagenPerfil.setImageURI(usuario.getImagenPerfil());

            contenidoVacio.setVisibility(View.GONE);
        } else {
            mostrarEstadoVacio();
        }
    }

    private void mostrarEstadoVacio() {
        contenidoVacio.setVisibility(View.VISIBLE);
        Log.d(TAG, "No hay datos de usuario disponibles");
    }
}
