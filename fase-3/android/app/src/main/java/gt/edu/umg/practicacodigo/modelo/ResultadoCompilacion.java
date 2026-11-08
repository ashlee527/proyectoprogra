package gt.edu.umg.practicacodigo.modelo;

/** Resultado de compilar codigo Java en el servidor. */
public class ResultadoCompilacion {

    private boolean exitoso;
    private String salida;
    private String nombreClase;

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

    public String getNombreClase() {
        return nombreClase;
    }

    public void setNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }
}
