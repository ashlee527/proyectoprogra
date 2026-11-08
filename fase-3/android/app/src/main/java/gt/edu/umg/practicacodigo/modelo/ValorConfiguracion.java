package gt.edu.umg.practicacodigo.modelo;

/** Cuerpo de PUT /api/v1/configuracion/{clave}. */
public class ValorConfiguracion {

    private final String valor;

    public ValorConfiguracion(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
