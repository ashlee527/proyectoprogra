package gt.edu.umg.practicacodigo.modelo;

/** Cuerpo de error uniforme que devuelve la API REST. */
public class ErrorRespuesta {

    private int estado;
    private String error;
    private String mensaje;
    private String ruta;
    private String hora;

    public int getEstado() {
        return estado;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getRuta() {
        return ruta;
    }

    public String getHora() {
        return hora;
    }
}
