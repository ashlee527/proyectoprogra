package gt.edu.umg.practicacodigo.modelo;

/** Metadatos de una imagen asociada a una leccion. */
public class RecursoLeccion {

    private String nombre;
    private String mime;
    private long tamano;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public long getTamano() {
        return tamano;
    }

    public void setTamano(long tamano) {
        this.tamano = tamano;
    }
}
