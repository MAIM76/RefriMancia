package com.example.refrimancia.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.refrimancia.CreateRecipeActivity;
import com.example.refrimancia.R;
import com.example.refrimancia.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContenedorPrincipalActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contenedor_principal);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.sub_fragment_container, new InicioFragment())
					.commit();
		}

		BottomNavigationView navInferior = findViewById(R.id.bottom_navigation);
		navInferior.setSelectedItemId(R.id.nav_home);
		navInferior.setOnItemSelectedListener(item -> {
			int id = item.getItemId();
			if (id == R.id.nav_home) {
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.sub_fragment_container, new InicioFragment())
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
						.replace(R.id.sub_fragment_container, new UsuarioFragment())
						.commit();
				return true;
			}
			return false;
		});
	}

	private void abrirCrearRecetaConSesion() {
		SessionManager sessionManager = new SessionManager(this);
		Intent intent = new Intent(this, CreateRecipeActivity.class);
		int idUsuario = sessionManager.fetchUserId();
		String token = sessionManager.fetchAuthToken();
		if (idUsuario > 0) {
			intent.putExtra("ID_USUARIO", idUsuario);
		}
		if (token != null && !token.isEmpty()) {
			intent.putExtra("TOKEN", token);
		}
		startActivity(intent);
	}
}
