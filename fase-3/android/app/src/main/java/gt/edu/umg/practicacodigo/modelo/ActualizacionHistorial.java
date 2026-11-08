package gt.edu.umg.practicacodigo.modelo;

/** Cuerpo de PATCH /api/v1/historial/{id}. */
public class ActualizacionHistorial {

    private final EstadoLeccion estado;
    private final String detalle;

    public ActualizacionHistorial(EstadoLeccion estado, String detalle) {
        this.estado = estado;
        this.detalle = detalle;
    }

    public EstadoLeccion getEstado() {
        return estado;
    }

    public String getDetalle() {
        return detalle;
    }
}
