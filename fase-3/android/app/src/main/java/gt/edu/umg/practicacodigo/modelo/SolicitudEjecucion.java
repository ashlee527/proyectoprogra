package gt.edu.umg.practicacodigo.modelo;

/** Cuerpo de POST /api/v1/ejecucion/ejecutar. */
public class SolicitudEjecucion {

    private final String nombreClase;
    private final int timeoutSegundos;

    public SolicitudEjecucion(String nombreClase, int timeoutSegundos) {
        this.nombreClase = nombreClase;
        this.timeoutSegundos = timeoutSegundos;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public int getTimeoutSegundos() {
        return timeoutSegundos;
    }
}
