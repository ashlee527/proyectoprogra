package gt.edu.umg.practicacodigo.datos;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import gt.edu.umg.practicacodigo.modelo.Leccion;

/**
 * Catalogo de demostracion local. Se usa solo cuando la API REST no responde,
 * para que el usuario pueda recorrer la interfaz sin servidor. La compilacion y
 * la ejecucion siempre requieren la API.
 */
public final class RepositorioDemo {

    private static final String ETIQUETA = "RepositorioDemo";

    private RepositorioDemo() {
    }

    public static List<Leccion> cargar(Context contexto) {
        List<Leccion> lecciones = new ArrayList<>();
        try (InputStream entrada = contexto.getAssets().open("demo/lecciones.json");
             BufferedReader lector = new BufferedReader(
                     new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            StringBuilder json = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) {
                json.append(linea).append('\n');
            }
            Type tipo = new TypeToken<List<Leccion>>() {
            }.getType();
            List<Leccion> leidas = new Gson().fromJson(json.toString(), tipo);
            if (leidas != null) {
                lecciones.addAll(leidas);
            }
        } catch (Exception e) {
            Log.w(ETIQUETA, "No se pudo leer assets/demo/lecciones.json", e);
        }
        return lecciones;
    }

    public static Leccion buscar(List<Leccion> lecciones, long id) {
        if (lecciones == null) {
            return null;
        }
        for (Leccion leccion : lecciones) {
            if (leccion.getId() == id) {
                return leccion;
            }
        }
        return null;
    }
}
