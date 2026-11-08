package gt.edu.umg.practicacodigo.modelo;

/** Resultado de ejecutar el bytecode compilado en el servidor. */
public class ResultadoEjecucion {

    private boolean exitoso;
    private String salida;
    private String errores;
    private boolean timeout;

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public String getErrores() {
        return errores;
    }

    public void setErrores(String errores) {
        this.errores = errores;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }
}
