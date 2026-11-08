package gt.edu.umg.practicacodigo.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Preferencias locales de la aplicacion (direccion de la API, etc.). */
public class Preferencias {

    public static final String API_POR_OMISION = "http://10.0.2.2:8080";

    private static final String ARCHIVO = "practica_codigo";
    private static final String CLAVE_API = "api_url";

    private final SharedPreferences datos;

    public Preferencias(Context contexto) {
        this.datos = contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public String getApiUrl() {
        String url = datos.getString(CLAVE_API, API_POR_OMISION);
        return url == null || url.trim().isEmpty() ? API_POR_OMISION : url.trim();
    }

    public void setApiUrl(String url) {
        datos.edit().putString(CLAVE_API, url == null ? API_POR_OMISION : url.trim()).apply();
    }
}
