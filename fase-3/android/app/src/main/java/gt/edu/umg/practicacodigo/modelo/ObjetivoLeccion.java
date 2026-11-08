package gt.edu.umg.practicacodigo.modelo;

/**
 * Tipos de objetivo que puede tener una leccion. El objetivo define la
 * condicion que debe cumplirse para marcarla como completada.
 */
public enum ObjetivoLeccion {

    COMPILAR_CON_ERROR("Compilar con error"),
    COMPILAR_EXITOSO("Compilar sin errores"),
    EJECUTAR_CON_ERROR("Ejecutar con error"),
    EJECUTAR_EXITOSO("Ejecutar con exito");

    private final String etiqueta;

    ObjetivoLeccion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Convierte el texto recibido de la API; devuelve null si no es valido. */
    public static ObjetivoLeccion desdeCodigo(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        for (ObjetivoLeccion objetivo : values()) {
            if (objetivo.name().equalsIgnoreCase(limpio)) {
                return objetivo;
            }
        }
        return null;
    }

    public static String etiquetaSegura(ObjetivoLeccion objetivo) {
        return objetivo == null ? "Sin objetivo" : objetivo.getEtiqueta();
    }
}
