package gt.edu.umg.practicacodigo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import gt.edu.umg.practicacodigo.modelo.ActualizacionHistorial;
import gt.edu.umg.practicacodigo.modelo.EstadoJdk;
import gt.edu.umg.practicacodigo.modelo.EstadoLeccion;
import gt.edu.umg.practicacodigo.modelo.ErrorRespuesta;
import gt.edu.umg.practicacodigo.modelo.HistorialRegistro;
import gt.edu.umg.practicacodigo.modelo.Leccion;
import gt.edu.umg.practicacodigo.modelo.LeccionResumen;
import gt.edu.umg.practicacodigo.modelo.ObjetivoLeccion;
import gt.edu.umg.practicacodigo.modelo.ResultadoCompilacion;
import gt.edu.umg.practicacodigo.modelo.ResultadoEjecucion;
import gt.edu.umg.practicacodigo.modelo.SolicitudCompilacion;
import gt.edu.umg.practicacodigo.modelo.SolicitudEjecucion;
import gt.edu.umg.practicacodigo.modelo.SolicitudHistorial;
import gt.edu.umg.practicacodigo.modelo.ValorConfiguracion;

/**
 * Cliente HTTP de la API REST de la Fase 2. Usa HttpURLConnection y Gson.
 * Todos los metodos son sincronicos: la actividad los invoca desde un hilo
 * secundario con {@code TareaAsincrona}.
 */
public class ApiCliente {

    private static final int TIMEOUT_CONEXION = 10000;
    private static final int TIMEOUT_CORTO = 20000;
    private static final int TIMEOUT_COMPILAR = 150000;
    private static final int TIMEOUT_EJECUTAR = 90000;

    private final Gson gson = new GsonBuilder().create();
    private final String baseUrl;

    public ApiCliente(String baseUrl) {
        this.baseUrl = normalizar(baseUrl);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private static String normalizar(String url) {
        String limpio = url == null ? "" : url.trim();
        if (limpio.isEmpty()) {
            return "http://10.0.2.2:8080";
        }
        if (!limpio.startsWith("http://") && !limpio.startsWith("https://")) {
            limpio = "http://" + limpio;
        }
        while (limpio.endsWith("/")) {
            limpio = limpio.substring(0, limpio.length() - 1);
        }
        return limpio;
    }

    // ------------------------------------------------------------------
    // Lecciones
    // ------------------------------------------------------------------

    public List<LeccionResumen> listarLecciones() throws ApiExcepcion {
        Type tipo = new TypeToken<List<LeccionResumen>>() {
        }.getType();
        return enviar("GET", "/api/v1/lecciones", null, TIMEOUT_CORTO, tipo);
    }

    public Leccion obtenerLeccion(long id) throws ApiExcepcion {
        return enviar("GET", "/api/v1/lecciones/" + id, null, TIMEOUT_CORTO, Leccion.class);
    }

    // ------------------------------------------------------------------
    // Historial
    // ------------------------------------------------------------------

    public List<HistorialRegistro> listarHistorial() throws ApiExcepcion {
        Type tipo = new TypeToken<List<HistorialRegistro>>() {
        }.getType();
        return enviar("GET", "/api/v1/historial", null, TIMEOUT_CORTO, tipo);
    }

    public HistorialRegistro registrarHistorial(Long leccionId, String leccionNombre,
                                                ObjetivoLeccion objetivo) throws ApiExcepcion {
        SolicitudHistorial cuerpo = new SolicitudHistorial(leccionId, leccionNombre, objetivo, null);
        return enviar("POST", "/api/v1/historial", gson.toJson(cuerpo), TIMEOUT_CORTO,
                HistorialRegistro.class);
    }

    public HistorialRegistro marcarCompletada(long id, String detalle) throws ApiExcepcion {
        ActualizacionHistorial cuerpo = new ActualizacionHistorial(EstadoLeccion.COMPLETADA, detalle);
        return enviar("PATCH", "/api/v1/historial/" + id, gson.toJson(cuerpo), TIMEOUT_CORTO,
                HistorialRegistro.class);
    }

    // ------------------------------------------------------------------
    // Configuracion y JDK
    // ------------------------------------------------------------------

    public EstadoJdk estadoJdk() throws ApiExcepcion {
        return enviar("GET", "/api/v1/ejecucion/jdk", null, TIMEOUT_CORTO, EstadoJdk.class);
    }

    public EstadoJdk guardarRutaJdk(String ruta) throws ApiExcepcion {
        enviar("PUT", "/api/v1/configuracion/jdk.ruta",
                gson.toJson(new ValorConfiguracion(ruta)), TIMEOUT_CORTO, Object.class);
        return estadoJdk();
    }

    // ------------------------------------------------------------------
    // Compilacion y ejecucion
    // ------------------------------------------------------------------

    public ResultadoCompilacion compilar(String codigo) throws ApiExcepcion {
        return enviar("POST", "/api/v1/ejecucion/compilar",
                gson.toJson(new SolicitudCompilacion(codigo)), TIMEOUT_COMPILAR,
                ResultadoCompilacion.class);
    }

    public ResultadoEjecucion ejecutar(String nombreClase, int timeoutSegundos) throws ApiExcepcion {
        return enviar("POST", "/api/v1/ejecucion/ejecutar",
                gson.toJson(new SolicitudEjecucion(nombreClase, timeoutSegundos)), TIMEOUT_EJECUTAR,
                ResultadoEjecucion.class);
    }

    // ------------------------------------------------------------------
    // Transporte HTTP
    // ------------------------------------------------------------------

    private <T> T enviar(String metodo, String ruta, String cuerpo, int timeoutLectura, Type tipo)
            throws ApiExcepcion {
        HttpURLConnection conexion = null;
        try {
            URL url = new URL(baseUrl + ruta);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod(metodo);
            conexion.setConnectTimeout(TIMEOUT_CONEXION);
            conexion.setReadTimeout(timeoutLectura);
            conexion.setRequestProperty("Accept", "application/json");
            conexion.setUseCaches(false);

            if (cuerpo != null) {
                byte[] datos = cuerpo.getBytes(StandardCharsets.UTF_8);
                conexion.setDoOutput(true);
                conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conexion.setFixedLengthStreamingMode(datos.length);
                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(datos);
                    salida.flush();
                }
            }

            int estado = conexion.getResponseCode();
            boolean correcto = estado >= 200 && estado < 300;
            String texto = leer(correcto ? conexion.getInputStream() : conexion.getErrorStream());

            if (!correcto) {
                throw new ApiExcepcion(estado, mensajeDeError(texto, estado));
            }
            if (tipo == null || Void.class.equals(tipo) || texto == null || texto.trim().isEmpty()) {
                return null;
            }
            return gson.fromJson(texto, tipo);
        } catch (ApiExcepcion e) {
            throw e;
        } catch (JsonParseException e) {
            throw new ApiExcepcion(0, "La API devolvio una respuesta JSON inesperada.", e);
        } catch (IOException e) {
            throw new ApiExcepcion(0, "No se pudo conectar con " + baseUrl
                    + ". Verifique que la API este iniciada y que la direccion sea correcta.", e);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    private String mensajeDeError(String texto, int estado) {
        if (texto != null && !texto.trim().isEmpty()) {
            try {
                ErrorRespuesta error = gson.fromJson(texto, ErrorRespuesta.class);
                if (error != null && error.getMensaje() != null && !error.getMensaje().trim().isEmpty()) {
                    return error.getMensaje();
                }
            } catch (JsonParseException ignorada) {
                // Se usa el mensaje generico.
            }
        }
        return "La API respondio con el codigo HTTP " + estado + ".";
    }

    private String leer(InputStream entrada) throws IOException {
        if (entrada == null) {
            return null;
        }
        StringBuilder texto = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                texto.append(linea).append('\n');
            }
        }
        return texto.toString();
    }
}
