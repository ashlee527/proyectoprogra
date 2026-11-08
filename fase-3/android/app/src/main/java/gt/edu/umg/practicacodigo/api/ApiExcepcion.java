package gt.edu.umg.practicacodigo.api;

/** Error controlado de la comunicacion con la API REST. */
public class ApiExcepcion extends Exception {

    private final int estadoHttp;

    public ApiExcepcion(int estadoHttp, String mensaje) {
        super(mensaje);
        this.estadoHttp = estadoHttp;
    }

    public ApiExcepcion(int estadoHttp, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.estadoHttp = estadoHttp;
    }

    public int getEstadoHttp() {
        return estadoHttp;
    }

    public boolean esDeConexion() {
        return estadoHttp == 0;
    }
}
