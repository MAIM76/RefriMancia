package com.example.refrimancia.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.refrimancia.api.ClienteRetrofit;

public class SessionManager {
    private static final String PREF_NAME = "RefriManciaSession";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHOTO = "user_photo";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String fetchAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void saveUserDetail(int id, String name) {
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public void saveUserPhoto(String url) {
        editor.putString(KEY_USER_PHOTO, url);
        editor.apply();
    }

    public String fetchUserPhoto() {
        return sharedPreferences.getString(KEY_USER_PHOTO, null);
    }

    public int fetchUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String fetchUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Usuario");
    }

    public boolean isSessionValid() {
        String token = fetchAuthToken();
        int userId = fetchUserId();
        return token != null && !token.isEmpty() && userId > 0;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
        ClienteRetrofit.resetInstancia();
    }
}
