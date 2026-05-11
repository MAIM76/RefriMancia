package com.example.refrimancia.ui;

import android.text.InputFilter;
import android.text.Spanned;

/*Esta clase se utilizará para los EditText
Lo que hace es comprobar que el caracter introducido es una letra
(con o sin tilde, mayúscula o minúscula) o un espacio
No aceptará caracteres especiales ni números
 */
public class FiltroSoloLetras implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source,
                               int start,
                               int end,
                               Spanned dest,
                               int dstart,
                               int dend) {

        for (int i = start; i < end; i++) {
            char c = source.charAt(i);

            if (!Character.isLetter(c) && c != ' ' && c != '-') {
                return "";
            }
        }

        return null;
    }
}