package com.iespp.app.utils;

import java.util.*;

public class CifradoUrlUtil {

    private static final String TABLA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String CLAVE = "FISI";
    private static final char SELLO = 'J';

    private static final Map<Character, String> URL_MAP = new LinkedHashMap<>();
    private static final Map<String, Character> REV_URL_MAP = new LinkedHashMap<>();
    private static final Map<String, Character> OLD_REV_URL_MAP = new LinkedHashMap<>();

    static {
        URL_MAP.put('/', "@A");
        URL_MAP.put('-', "@B");
        URL_MAP.put('.', "@C");
        URL_MAP.put(':', "@D");
        URL_MAP.put('?', "@E");
        URL_MAP.put('=', "@F");
        URL_MAP.put('&', "@G");
        URL_MAP.put('_', "@H");
        URL_MAP.put('%', "@I");
        URL_MAP.put('#', "@J");
        URL_MAP.put('~', "@K");

        for (Map.Entry<Character, String> e : URL_MAP.entrySet()) {
            REV_URL_MAP.put(e.getValue(), e.getKey());
        }

        OLD_REV_URL_MAP.put("AA", '/');
        OLD_REV_URL_MAP.put("AB", '-');
        OLD_REV_URL_MAP.put("AC", '.');
        OLD_REV_URL_MAP.put("AD", ':');
        OLD_REV_URL_MAP.put("AE", '?');
        OLD_REV_URL_MAP.put("AF", '=');
        OLD_REV_URL_MAP.put("AG", '&');
        OLD_REV_URL_MAP.put("AH", '_');
        OLD_REV_URL_MAP.put("AI", '%');
        OLD_REV_URL_MAP.put("AJ", '#');
        OLD_REV_URL_MAP.put("AK", '~');
    }

    private static int pos(char c) {
        return TABLA.indexOf(Character.toUpperCase(c));
    }

    private static char charAt(int p) {
        p = ((p % 36) + 36) % 36;
        return TABLA.charAt(p);
    }

    private static String preEncode(String texto) {
        String t = texto.toUpperCase(Locale.ROOT);
        for (Map.Entry<Character, String> e : URL_MAP.entrySet()) {
            t = t.replace(String.valueOf(e.getKey()), e.getValue());
        }
        return t;
    }

    private static String postDecode(String texto) {
        String result = texto;
        for (Map.Entry<String, Character> e : REV_URL_MAP.entrySet()) {
            result = result.replace(e.getKey(), String.valueOf(e.getValue()));
        }
        return result;
    }

    private static String postDecodeOld(String texto) {
        String result = texto;
        for (Map.Entry<String, Character> e : OLD_REV_URL_MAP.entrySet()) {
            result = result.replace(e.getKey(), String.valueOf(e.getValue()));
        }
        return result;
    }

    public static String encriptar(String texto) {
        String mensaje = preEncode(texto);
        if (mensaje.isEmpty()) return "";

        int n = mensaje.length();
        StringBuilder desplazado = new StringBuilder();
        int ki = 0;

        for (int i = 0; i < n; i++) {
            char c = mensaje.charAt(i);
            if (c == '@') {
                desplazado.append('@');
                ki++;
            } else {
                int pM = pos(c);
                int pK = pos(CLAVE.charAt(ki % CLAVE.length()));
                ki++;
                if (pM < 0) return "";
                desplazado.append(charAt(pM + pK));
            }
        }

        StringBuilder impares = new StringBuilder();
        StringBuilder pares = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) impares.append(desplazado.charAt(i));
            else pares.append(desplazado.charAt(i));
        }

        String impRev = impares.reverse().toString();
        String unido = impRev + pares.toString();

        return SELLO + unido + SELLO;
    }

    public static String desencriptar(String criptograma) {
        if (criptograma == null || criptograma.length() < 2) return "";
        if (criptograma.charAt(0) != SELLO || criptograma.charAt(criptograma.length() - 1) != SELLO) return "";

        String interno = criptograma.substring(1, criptograma.length() - 1);
        int n = interno.length();

        int mitad = (n + 1) / 2;
        String impRev = interno.substring(0, mitad);
        String pares = interno.substring(mitad);

        String impares = new StringBuilder(impRev).reverse().toString();

        StringBuilder desplazado = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) desplazado.append(impares.charAt(i / 2));
            else desplazado.append(pares.charAt(i / 2));
        }

        StringBuilder original = new StringBuilder();
        int ki = 0;
        for (int i = 0; i < n; i++) {
            char c = desplazado.charAt(i);
            if (c == '@') {
                original.append('@');
                ki++;
            } else {
                int pD = pos(c);
                int pK = pos(CLAVE.charAt(ki % CLAVE.length()));
                ki++;
                if (pD < 0) return "";
                original.append(charAt(pD - pK));
            }
        }

        String decodificado = original.toString();
        return decodificado.contains("@") ? postDecode(decodificado) : postDecodeOld(decodificado);
    }

    public static boolean esEncriptado(String path) {
        return path != null && path.length() > 2 &&
               path.charAt(0) == SELLO && path.charAt(path.length() - 1) == SELLO;
    }
}
