package gt.edu.umg.practicacodigo.modelo;

/** Registro del historial de sesion devuelto por la API. */
public class HistorialRegistro {

    private long id;
    private Long leccionId;
    private String leccionNombre;
    private ObjetivoLeccion objetivo;
    private String apertura;
    private EstadoLeccion estado;
    private String detalle;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getLeccionId() {
        return leccionId;
    }

    public void setLeccionId(Long leccionId) {
        this.leccionId = leccionId;
    }

    public String getLeccionNombre() {
        return leccionNombre;
    }

    public void setLeccionNombre(String leccionNombre) {
        this.leccionNombre = leccionNombre;
    }

    public ObjetivoLeccion getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(ObjetivoLeccion objetivo) {
        this.objetivo = objetivo;
    }

    public String getApertura() {
        return apertura;
    }

    public void setApertura(String apertura) {
        this.apertura = apertura;
    }

    public EstadoLeccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoLeccion estado) {
        this.estado = estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public boolean estaCompletada() {
        return estado == EstadoLeccion.COMPLETADA;
    }
}
