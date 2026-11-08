package gt.edu.umg.practicacodigo.util;

import gt.edu.umg.practicacodigo.modelo.ObjetivoLeccion;

/**
 * Evalua si una accion del estudiante cumple el objetivo de la leccion.
 * Es una clase sin dependencias de Android para poder probarla con JUnit.
 */
public final class EvaluadorObjetivos {

    private EvaluadorObjetivos() {
    }

    public static boolean cumpleAlCompilar(ObjetivoLeccion objetivo, boolean exitoso) {
        if (objetivo == null) {
            return false;
        }
        switch (objetivo) {
            case COMPILAR_CON_ERROR:
                return !exitoso;
            case COMPILAR_EXITOSO:
                return exitoso;
            default:
                return false;
        }
    }

    public static boolean cumpleAlEjecutar(ObjetivoLeccion objetivo, boolean exitoso, boolean timeout) {
        if (objetivo == null) {
            return false;
        }
        switch (objetivo) {
            case EJECUTAR_CON_ERROR:
                return timeout || !exitoso;
            case EJECUTAR_EXITOSO:
                return exitoso && !timeout;
            default:
                return false;
        }
    }
}
