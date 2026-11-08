package gt.edu.umg.practicacodigo.modelo;

/** Estado del JDK configurado en el servidor (GET /api/v1/ejecucion/jdk). */
public class EstadoJdk {

    private boolean configurado;
    private String ruta;
    private String mensaje;

    public boolean isConfigurado() {
        return configurado;
    }

    public void setConfigurado(boolean configurado) {
        this.configurado = configurado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
