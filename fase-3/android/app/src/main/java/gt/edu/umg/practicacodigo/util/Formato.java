package gt.edu.umg.practicacodigo.util;

import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Utilidades de formato para fechas y tamanos. */
public final class Formato {

    private Formato() {
    }

    /** Convierte una fecha ISO-8601 de la API a un formato legible. */
    public static String fechaHora(String iso) {
        if (iso == null || iso.trim().isEmpty()) {
            return "Sin fecha";
        }
        String limpio = iso.trim();
        String[] patrones = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss"
        };
        for (String patron : patrones) {
            try {
                SimpleDateFormat lector = new SimpleDateFormat(patron, Locale.US);
                lector.setLenient(false);
                Date fecha = lector.parse(limpio);
                if (fecha != null) {
                    SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    return salida.format(fecha);
                }
            } catch (ParseException ignorada) {
                // Se intenta con el siguiente patron.
            }
        }
        return limpio.replace('T', ' ');
    }

    public static String tamano(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0);
        }
        return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
    }

    public static String recortar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, Math.max(0, maximo - 1)) + "…";
    }
}
