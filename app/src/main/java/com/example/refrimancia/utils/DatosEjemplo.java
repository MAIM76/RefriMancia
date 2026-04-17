package com.example.refrimancia.utils;

import com.example.refrimancia.modelo.LoginRequest;
import com.example.refrimancia.modelo.Usuario;
import com.example.refrimancia.modelo.AuthToken;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.UsuarioService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatosEjemplo {

    public static String tokenActual = null;
    public static Usuario usuarioActual = null;

    public interface LoginCallback {
        void onResult(boolean exito);
    }

    /**
     * Función asíncrona temporal para obtener el token automáticamente para toda la app.
     */
    public static void loginAutomaticoTemporal(LoginCallback callback) {
        if (tokenActual != null) {
            callback.onResult(true);
            return;
        }

        UsuarioService usuarioService = ClienteRetrofit.obtenerInstancia().create(UsuarioService.class);
        usuarioService.login(obtenerCredencialesPorDefecto()).enqueue(new Callback<AuthToken>() {
            @Override
            public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenActual = response.body().getToken();
                    usuarioActual = response.body().getUsuario();
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<AuthToken> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }

    /**
     * Devuelve las credenciales temporales de ejemplo utilizadas en la sesión actual
     * para el usuario de demo.
     */
    public static LoginRequest obtenerCredencialesPorDefecto() {
        return new LoginRequest("1916184@alu.murciaeduca.es", "PaBlo2411mEh@@#");
    }

}
