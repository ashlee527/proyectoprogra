package gt.edu.umg.practicacodigo.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ejecuta tareas bloqueantes (HTTP) en un hilo secundario y entrega el
 * resultado en el hilo principal de la interfaz.
 */
public class TareaAsincrona {

    public interface Tarea<T> {
        T ejecutar() throws Exception;
    }

    public interface Resultado<T> {
        void exito(T valor);

        void error(Exception e);
    }

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    public <T> void ejecutar(final Tarea<T> tarea, final Resultado<T> resultado) {
        ejecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final T valor = tarea.ejecutar();
                    principal.post(new Runnable() {
                        @Override
                        public void run() {
                            resultado.exito(valor);
                        }
                    });
                } catch (final Exception e) {
                    principal.post(new Runnable() {
                        @Override
                        public void run() {
                            resultado.error(e);
                        }
                    });
                }
            }
        });
    }

    public void shutdown() {
        ejecutor.shutdownNow();
    }
}
