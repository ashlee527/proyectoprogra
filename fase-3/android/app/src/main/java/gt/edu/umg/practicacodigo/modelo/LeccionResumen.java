package gt.edu.umg.practicacodigo.modelo;

/** Resumen de una leccion tal como lo devuelve GET /api/v1/lecciones. */
public class LeccionResumen {

    private long id;
    private String nombre;
    private String titulo;
    private ObjetivoLeccion objetivo;
    private String rutaOrigen;
    private String fechaImportacion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ObjetivoLeccion getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(ObjetivoLeccion objetivo) {
        this.objetivo = objetivo;
    }

    public String getRutaOrigen() {
        return rutaOrigen;
    }

    public void setRutaOrigen(String rutaOrigen) {
        this.rutaOrigen = rutaOrigen;
    }

    public String getFechaImportacion() {
        return fechaImportacion;
    }

    public void setFechaImportacion(String fechaImportacion) {
        this.fechaImportacion = fechaImportacion;
    }

    public String getTituloVisible() {
        if (titulo != null && !titulo.trim().isEmpty()) {
            return titulo;
        }
        return nombre == null ? "Leccion sin titulo" : nombre;
    }

    @Override
    public String toString() {
        return getTituloVisible();
    }
}
