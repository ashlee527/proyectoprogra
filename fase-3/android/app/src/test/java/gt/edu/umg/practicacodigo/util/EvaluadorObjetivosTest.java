package gt.edu.umg.practicacodigo.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gt.edu.umg.practicacodigo.modelo.ObjetivoLeccion;

/**
 * Pruebas de la logica que decide cuando se completa una leccion segun el
 * resultado de compilar o ejecutar. No dependen del SDK de Android.
 */
public class EvaluadorObjetivosTest {

    @Test
    public void compilarExitosoCumpleSoloConCompilacionCorrecta() {
        assertTrue(EvaluadorObjetivos.cumpleAlCompilar(ObjetivoLeccion.COMPILAR_EXITOSO, true));
        assertFalse(EvaluadorObjetivos.cumpleAlCompilar(ObjetivoLeccion.COMPILAR_EXITOSO, false));
    }

    @Test
    public void compilarConErrorCumpleSoloConCompilacionFallida() {
        assertTrue(EvaluadorObjetivos.cumpleAlCompilar(ObjetivoLeccion.COMPILAR_CON_ERROR, false));
        assertFalse(EvaluadorObjetivos.cumpleAlCompilar(ObjetivoLeccion.COMPILAR_CON_ERROR, true));
    }

    @Test
    public void objetivosDeEjecucionNoSeCumplenAlCompilar() {
        assertFalse(EvaluadorObjetivos.cumpleAlCompilar(ObjetivoLeccion.EJECUTAR_EXITOSO, true));
        assertFalse(EvaluadorObjetivos.cumpleAlCompilar(ObjetivoLeccion.EJECUTAR_CON_ERROR, false));
    }

    @Test
    public void ejecutarExitosoCumpleSoloSinErroresNiTimeout() {
        assertTrue(EvaluadorObjetivos.cumpleAlEjecutar(ObjetivoLeccion.EJECUTAR_EXITOSO, true, false));
        assertFalse(EvaluadorObjetivos.cumpleAlEjecutar(ObjetivoLeccion.EJECUTAR_EXITOSO, false, false));
        assertFalse(EvaluadorObjetivos.cumpleAlEjecutar(ObjetivoLeccion.EJECUTAR_EXITOSO, true, true));
    }

    @Test
    public void ejecutarConErrorCumpleConFalloOTimeout() {
        assertTrue(EvaluadorObjetivos.cumpleAlEjecutar(ObjetivoLeccion.EJECUTAR_CON_ERROR, false, false));
        assertTrue(EvaluadorObjetivos.cumpleAlEjecutar(ObjetivoLeccion.EJECUTAR_CON_ERROR, true, true));
        assertFalse(EvaluadorObjetivos.cumpleAlEjecutar(ObjetivoLeccion.EJECUTAR_CON_ERROR, true, false));
    }

    @Test
    public void objetivoNuloNuncaCumple() {
        assertFalse(EvaluadorObjetivos.cumpleAlCompilar(null, true));
        assertFalse(EvaluadorObjetivos.cumpleAlEjecutar(null, true, false));
    }
}
