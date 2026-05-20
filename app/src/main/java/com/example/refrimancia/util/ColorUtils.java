package com.example.refrimancia.util;

import android.content.Context;
import androidx.core.content.ContextCompat;

public class ColorUtils {

    public static int colorSemaforo(Context context, String semaforo) {
        if (semaforo == null) return 0;
        switch (semaforo.toLowerCase()) {
            case "rojo":
                return ContextCompat.getColor(context, android.R.color.holo_red_dark);
            case "naranja":
                return ContextCompat.getColor(context, android.R.color.holo_orange_dark);
            case "amarillo":
                return ContextCompat.getColor(context, android.R.color.holo_orange_light);
            case "verde_claro":
                return ContextCompat.getColor(context, android.R.color.holo_green_light);
            case "verde_oscuro":
                return ContextCompat.getColor(context, android.R.color.holo_green_dark);
            default:
                return 0;
        }
    }

    public static String textoSemaforo(Context context, String semaforo) {
        if (semaforo == null) {
            return context.getString(com.example.refrimancia.R.string.semaforo_unknown);
        }
        switch (semaforo.toLowerCase()) {
            case "rojo":
                return context.getString(com.example.refrimancia.R.string.semaforo_red);
            case "naranja":
                return context.getString(com.example.refrimancia.R.string.semaforo_orange);
            case "amarillo":
                return context.getString(com.example.refrimancia.R.string.semaforo_yellow);
            case "verde_claro":
                return context.getString(com.example.refrimancia.R.string.semaforo_light_green);
            case "verde_oscuro":
                return context.getString(com.example.refrimancia.R.string.semaforo_dark_green);
            case "gris":
            default:
                return context.getString(com.example.refrimancia.R.string.semaforo_unknown);
        }
    }
}
