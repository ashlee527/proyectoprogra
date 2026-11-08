package gt.edu.umg.practicacodigo.vista;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import gt.edu.umg.practicacodigo.R;
import gt.edu.umg.practicacodigo.api.ApiCliente;
import gt.edu.umg.practicacodigo.modelo.EstadoJdk;
import gt.edu.umg.practicacodigo.util.Preferencias;
import gt.edu.umg.practicacodigo.util.TareaAsincrona;

/**
 * Dialogo de configuracion: direccion de la API y ruta del JDK en el servidor.
 * La ruta se persiste en la API (clave jdk.ruta) y la usa guali-dev.jar.
 */
public final class DialogoConfiguracion {

    public interface AlGuardar {
        void alGuardar(EstadoJdk estado, String apiUrl);
    }

    private DialogoConfiguracion() {
    }

    public static void mostrar(final Activity actividad, ApiCliente api,
                               final Preferencias preferencias, final AlGuardar alGuardar) {
        final View vista = LayoutInflater.from(actividad)
                .inflate(R.layout.dialogo_configuracion, null, false);
        final TextInputEditText campoJdk = vista.findViewById(R.id.campoJdk);
        final TextInputEditText campoApi = vista.findViewById(R.id.campoApi);
        final TextView estadoJdk = vista.findViewById(R.id.estadoJdkDialogo);
        final TextView mensaje = vista.findViewById(R.id.mensajeDialogo);

        campoApi.setText(preferencias.getApiUrl());

        final TareaAsincrona tareas = new TareaAsincrona();
        final ApiCliente[] cliente = {api};

        final AlertDialog dialogo = new AlertDialog.Builder(actividad)
                .setTitle(R.string.dialogo_jdk_titulo)
                .setView(vista)
                .setPositiveButton(R.string.guardar, null)
                .setNeutralButton(R.string.verificar, null)
                .setNegativeButton(R.string.cancelar, null)
                .create();

        dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                consultarEstado(cliente[0], tareas, estadoJdk, mensaje, campoJdk);

                dialogo.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = textoDe(campoApi);
                        if (url.isEmpty()) {
                            mostrarMensaje(mensaje, "Indique la direccion de la API.");
                            return;
                        }
                        cliente[0] = new ApiCliente(url);
                        consultarEstado(cliente[0], tareas, estadoJdk, mensaje, campoJdk);
                    }
                });

                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String url = textoDe(campoApi);
                        final String ruta = textoDe(campoJdk);
                        if (url.isEmpty()) {
                            mostrarMensaje(mensaje, "Indique la direccion de la API.");
                            return;
                        }

                        preferencias.setApiUrl(url);
                        final ApiCliente nuevo = new ApiCliente(url);
                        cliente[0] = nuevo;
                        mensaje.setVisibility(View.GONE);
                        habilitarBotones(dialogo, false);

                        tareas.ejecutar(new TareaAsincrona.Tarea<EstadoJdk>() {
                            @Override
                            public EstadoJdk ejecutar() throws Exception {
                                if (ruta.isEmpty()) {
                                    return nuevo.estadoJdk();
                                }
                                return nuevo.guardarRutaJdk(ruta);
                            }
                        }, new TareaAsincrona.Resultado<EstadoJdk>() {
                            @Override
                            public void exito(EstadoJdk estado) {
                                habilitarBotones(dialogo, true);
                                if (estado != null && estado.isConfigurado()) {
                                    estadoJdk.setText(estadoJdk.getContext()
                                            .getString(R.string.jdk_configurado, estado.getRuta()));
                                    estadoJdk.setTextColor(ContextCompat.getColor(estadoJdk.getContext(),
                                            R.color.exito));
                                    if (alGuardar != null) {
                                        alGuardar.alGuardar(estado, url);
                                    }
                                    dialogo.dismiss();
                                } else {
                                    estadoJdk.setText(R.string.jdk_no_configurado);
                                    estadoJdk.setTextColor(ContextCompat.getColor(estadoJdk.getContext(),
                                            R.color.advertencia));
                                    if (alGuardar != null) {
                                        alGuardar.alGuardar(estado, url);
                                    }
                                }
                            }

                            @Override
                            public void error(Exception e) {
                                habilitarBotones(dialogo, true);
                                mostrarMensaje(mensaje, "No se pudo guardar la configuracion: "
                                        + e.getMessage());
                            }
                        });
                    }
                });
            }
        });

        dialogo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface d) {
                tareas.shutdown();
            }
        });

        dialogo.show();
    }

    private static void consultarEstado(final ApiCliente api, TareaAsincrona tareas,
                                        final TextView estadoJdk, final TextView mensaje,
                                        final TextInputEditText campoJdk) {
        estadoJdk.setText(R.string.estado_jdk_consultando);
        estadoJdk.setTextColor(ContextCompat.getColor(estadoJdk.getContext(), R.color.texto_secundario));
        tareas.ejecutar(new TareaAsincrona.Tarea<EstadoJdk>() {
            @Override
            public EstadoJdk ejecutar() throws Exception {
                return api.estadoJdk();
            }
        }, new TareaAsincrona.Resultado<EstadoJdk>() {
            @Override
            public void exito(EstadoJdk estado) {
                mensaje.setVisibility(View.GONE);
                if (estado != null && estado.isConfigurado()) {
                    estadoJdk.setText(estadoJdk.getContext()
                            .getString(R.string.jdk_configurado, estado.getRuta()));
                    estadoJdk.setTextColor(ContextCompat.getColor(estadoJdk.getContext(), R.color.exito));
                    if (textoDe(campoJdk).isEmpty()) {
                        campoJdk.setText(estado.getRuta());
                    }
                } else {
                    estadoJdk.setText(R.string.jdk_no_configurado);
                    estadoJdk.setTextColor(ContextCompat.getColor(estadoJdk.getContext(),
                            R.color.advertencia));
                }
            }

            @Override
            public void error(Exception e) {
                estadoJdk.setText("Sin conexion: " + e.getMessage());
                estadoJdk.setTextColor(ContextCompat.getColor(estadoJdk.getContext(), R.color.error));
            }
        });
    }

    private static String textoDe(TextInputEditText campo) {
        if (campo.getText() == null) {
            return "";
        }
        return campo.getText().toString().trim();
    }

    private static void mostrarMensaje(TextView mensaje, String texto) {
        mensaje.setText(texto);
        mensaje.setVisibility(View.VISIBLE);
    }

    private static void habilitarBotones(AlertDialog dialogo, boolean habilitado) {
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(habilitado);
        dialogo.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(habilitado);
    }
}
