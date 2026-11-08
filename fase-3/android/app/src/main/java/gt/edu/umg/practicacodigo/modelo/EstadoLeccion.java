package gt.edu.umg.practicacodigo.modelo;

/** Estado de una leccion dentro del historial de sesion. */
public enum EstadoLeccion {

    COMPLETADA("Completada"),
    PENDIENTE("Pendiente");

    private final String etiqueta;

    EstadoLeccion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static EstadoLeccion desdeCodigo(String texto) {
        if (texto != null && texto.trim().equalsIgnoreCase("COMPLETADA")) {
            return COMPLETADA;
        }
        return PENDIENTE;
    }
}
