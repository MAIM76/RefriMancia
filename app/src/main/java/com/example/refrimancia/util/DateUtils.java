package com.example.refrimancia.util;

import android.content.Context;
import com.example.refrimancia.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatTiempo(Context context, int minutos) {
        if (minutos <= 0) return context.getString(R.string.recipe_time_not_available);
        int horas = minutos / 60;
        int minRestantes = minutos % 60;
        if (horas > 0) {
            if (minRestantes > 0) {
                return context.getString(R.string.time_hours_minutes_format, horas, minRestantes);
            } else {
                return context.getString(R.string.time_hours_format, horas);
            }
        }
        return context.getString(R.string.time_minutes_format, minutos);
    }

    public static String formatFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "";
        String[] formatos = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss"
        };
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        for (String formato : formatos) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
                Date fecha = sdf.parse(fechaOriginal);
                if (fecha != null) return formatoSalida.format(fecha);
            } catch (ParseException ignored) {}
        }
        return fechaOriginal;
    }
}
