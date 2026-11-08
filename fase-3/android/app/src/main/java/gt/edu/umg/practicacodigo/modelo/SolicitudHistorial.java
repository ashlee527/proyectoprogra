package gt.edu.umg.practicacodigo.modelo;

/** Cuerpo de POST /api/v1/historial. */
public class SolicitudHistorial {

    private final Long leccionId;
    private final String leccionNombre;
    private final ObjetivoLeccion objetivo;
    private final String detalle;

    public SolicitudHistorial(Long leccionId, String leccionNombre,
                              ObjetivoLeccion objetivo, String detalle) {
        this.leccionId = leccionId;
        this.leccionNombre = leccionNombre;
        this.objetivo = objetivo;
        this.detalle = detalle;
    }

    public Long getLeccionId() {
        return leccionId;
    }

    public String getLeccionNombre() {
        return leccionNombre;
    }

    public ObjetivoLeccion getObjetivo() {
        return objetivo;
    }

    public String getDetalle() {
        return detalle;
    }
}
