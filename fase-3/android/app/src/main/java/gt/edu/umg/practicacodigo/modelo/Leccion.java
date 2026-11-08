package gt.edu.umg.practicacodigo.modelo;

import java.util.ArrayList;
import java.util.List;

/** Leccion completa con su contenido Markdown y sus recursos. */
public class Leccion extends LeccionResumen {

    private String contenidoMd;
    private List<RecursoLeccion> recursos = new ArrayList<>();

    public String getContenidoMd() {
        return contenidoMd;
    }

    public void setContenidoMd(String contenidoMd) {
        this.contenidoMd = contenidoMd;
    }

    public List<RecursoLeccion> getRecursos() {
        return recursos == null ? new ArrayList<>() : recursos;
    }

    public void setRecursos(List<RecursoLeccion> recursos) {
        this.recursos = recursos;
    }
}
