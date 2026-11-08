package gt.edu.umg.practicacodigo.modelo;

/** Cuerpo de POST /api/v1/ejecucion/compilar. */
public class SolicitudCompilacion {

    private final String codigo;

    public SolicitudCompilacion(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
