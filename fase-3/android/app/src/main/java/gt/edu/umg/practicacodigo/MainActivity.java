package gt.edu.umg.practicacodigo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import gt.edu.umg.practicacodigo.api.ApiCliente;
import gt.edu.umg.practicacodigo.datos.RepositorioDemo;
import gt.edu.umg.practicacodigo.modelo.EstadoJdk;
import gt.edu.umg.practicacodigo.modelo.HistorialRegistro;
import gt.edu.umg.practicacodigo.modelo.Leccion;
import gt.edu.umg.practicacodigo.modelo.LeccionResumen;
import gt.edu.umg.practicacodigo.modelo.ObjetivoLeccion;
import gt.edu.umg.practicacodigo.modelo.ResultadoCompilacion;
import gt.edu.umg.practicacodigo.modelo.ResultadoEjecucion;
import gt.edu.umg.practicacodigo.util.EvaluadorObjetivos;
import gt.edu.umg.practicacodigo.util.MarcadorMarkdown;
import gt.edu.umg.practicacodigo.util.Preferencias;
import gt.edu.umg.practicacodigo.util.TareaAsincrona;
import gt.edu.umg.practicacodigo.vista.Consola;
import gt.edu.umg.practicacodigo.vista.DialogoConfiguracion;
import gt.edu.umg.practicacodigo.vista.EditorCodigoView;

/**
 * Pantalla principal: barra de menu, titulo de la leccion activa, panel de la
 * leccion, editor de codigo, consola y botones Compilar/Ejecutar.
 */
public class MainActivity extends AppCompatActivity {

    private TextView menuLeccion;
    private TextView menuConfiguracion;
    private TextView menuHistorial;
    private TextView menuAyuda;

    private TextView tituloLeccionActiva;
    private TextView subtituloLeccion;
    private TextView chipObjetivo;
    private TextView chipEstado;

    private TextView contadorLecciones;
    private Spinner spinnerLecciones;
    private MaterialButton botonAbrirLeccion;

    private TextView tituloLeccion;
    private TextView objetivoLeccion;
    private TextView contenidoLeccion;

    private EditorCodigoView editor;
    private TextView nombreClase;
    private TextView ayudaJdk;
    private MaterialButton botonCompilar;
    private MaterialButton botonEjecutar;
    private ImageButton botonCopiarCodigo;

    private TextView vistaConsola;
    private TextView estadoConexion;
    private ImageButton botonLimpiarConsola;
    private Consola consola;

    private Preferencias preferencias;
    private ApiCliente api;
    private TareaAsincrona tareas;

    private final List<LeccionResumen> lecciones = new ArrayList<>();
    private final List<Leccion> catalogoDemo = new ArrayList<>();

    private Leccion leccionActiva;
    private long registroId = -1;
    private boolean leccionCompletada;
    private boolean compilacionVigente;
    private boolean jdkConfigurado;
    private boolean modoDemo;
    private boolean cargandoLecciones;
    private String nombreClaseCompilada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enlazarVistas();
        configurarMenus();
        configurarEditor();
        configurarBotones();

        preferencias = new Preferencias(this);
        api = new ApiCliente(preferencias.getApiUrl());
        tareas = new TareaAsincrona();
        consola = new Consola(vistaConsola);

        consola.info("Practica de Codigo - Programacion 2, Fase 3");
        consola.info("API configurada: " + api.getBaseUrl());
        actualizarBotones();
        actualizarEstadoJdk();
        cargarLecciones();
    }

    private void enlazarVistas() {
        menuLeccion = findViewById(R.id.menuLeccion);
        menuConfiguracion = findViewById(R.id.menuConfiguracion);
        menuHistorial = findViewById(R.id.menuHistorial);
        menuAyuda = findViewById(R.id.menuAyuda);

        tituloLeccionActiva = findViewById(R.id.tituloLeccionActiva);
        subtituloLeccion = findViewById(R.id.subtituloLeccion);
        chipObjetivo = findViewById(R.id.chipObjetivo);
        chipEstado = findViewById(R.id.chipEstado);

        contadorLecciones = findViewById(R.id.contadorLecciones);
        spinnerLecciones = findViewById(R.id.spinnerLecciones);
        botonAbrirLeccion = findViewById(R.id.botonAbrirLeccion);

        tituloLeccion = findViewById(R.id.tituloLeccion);
        objetivoLeccion = findViewById(R.id.objetivoLeccion);
        contenidoLeccion = findViewById(R.id.contenidoLeccion);

        editor = findViewById(R.id.editor);
        nombreClase = findViewById(R.id.nombreClase);
        ayudaJdk = findViewById(R.id.ayudaJdk);
        botonCompilar = findViewById(R.id.botonCompilar);
        botonEjecutar = findViewById(R.id.botonEjecutar);
        botonCopiarCodigo = findViewById(R.id.botonCopiarCodigo);

        vistaConsola = findViewById(R.id.consola);
        estadoConexion = findViewById(R.id.estadoConexion);
        botonLimpiarConsola = findViewById(R.id.botonLimpiarConsola);
    }

    private void configurarMenus() {
        menuLeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenu().add(Menu.NONE, 1, 1, R.string.menu_abrir_leccion);
                popup.getMenu().add(Menu.NONE, 2, 2, R.string.menu_cerrar_leccion);
                popup.getMenu().add(Menu.NONE, 3, 3, R.string.menu_copiar_codigo);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 1:
                                abrirLeccionSeleccionada();
                                return true;
                            case 2:
                                cerrarLeccion();
                                return true;
                            case 3:
                                copiarCodigoAlEditor();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        menuConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenu().add(Menu.NONE, 1, 1, R.string.menu_configurar_jdk);
                popup.getMenu().add(Menu.NONE, 2, 2, R.string.menu_direccion_api);
                popup.getMenu().add(Menu.NONE, 3, 3, R.string.menu_recargar);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case 1:
                                mostrarConfiguracion();
                                return true;
                            case 2:
                                mostrarDialogoApi();
                                return true;
                            case 3:
                                cargandoLecciones = false;
                                cargarLecciones();
                                actualizarEstadoJdk();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        menuHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistorialActivity.class));
            }
        });

        menuAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenu().add(Menu.NONE, 1, 1, R.string.menu_ayuda_rapida);
                popup.getMenu().add(Menu.NONE, 2, 2, R.string.menu_acerca_de);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == 1) {
                            startActivity(new Intent(MainActivity.this, AyudaActivity.class));
                            return true;
                        }
                        if (item.getItemId() == 2) {
                            mostrarAcercaDe();
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    private void configurarEditor() {
        editor.setAlCambiarCodigo(new Runnable() {
            @Override
            public void run() {
                if (compilacionVigente) {
                    compilacionVigente = false;
                    nombreClaseCompilada = null;
                    nombreClase.setText(R.string.sin_compilar);
                    consola.info("El codigo cambio. Compile de nuevo antes de ejecutar.");
                    actualizarBotones();
                }
            }
        });
    }

    private void configurarBotones() {
        botonAbrirLeccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirLeccionSeleccionada();
            }
        });
        botonCompilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compilar();
            }
        });
        botonEjecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ejecutar();
            }
        });
        botonCopiarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copiarCodigoAlEditor();
            }
        });
        botonLimpiarConsola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consola.limpiar();
            }
        });
    }

    // ------------------------------------------------------------------
    // Lecciones
    // ------------------------------------------------------------------

    private void cargarLecciones() {
        if (cargandoLecciones) {
            return;
        }
        cargandoLecciones = true;
        estadoConexion.setText(R.string.estado_conectando);
        estadoConexion.setTextColor(ContextCompat.getColor(this, R.color.terminal_info));
        consola.info("Consultando lecciones en " + api.getBaseUrl() + " ...");

        tareas.ejecutar(new TareaAsincrona.Tarea<List<LeccionResumen>>() {
            @Override
            public List<LeccionResumen> ejecutar() throws Exception {
                return api.listarLecciones();
            }
        }, new TareaAsincrona.Resultado<List<LeccionResumen>>() {
            @Override
            public void exito(List<LeccionResumen> lista) {
                cargandoLecciones = false;
                modoDemo = false;
                lecciones.clear();
                if (lista != null) {
                    lecciones.addAll(lista);
                }
                actualizarSpinner();
                estadoConexion.setText(R.string.api_conectada);
                estadoConexion.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.terminal_exito));
                consola.exito("Se cargaron " + lecciones.size() + " lecciones desde la API.");
                if (lecciones.isEmpty()) {
                    consola.info("La base de datos no tiene lecciones. Importe lecciones desde el cliente de escritorio de la Fase 2.");
                }
                actualizarBotones();
            }

            @Override
            public void error(Exception e) {
                cargandoLecciones = false;
                activarModoDemo(e);
            }
        });
    }

    private void activarModoDemo(Exception causa) {
        modoDemo = true;
        catalogoDemo.clear();
        catalogoDemo.addAll(RepositorioDemo.cargar(this));
        lecciones.clear();
        lecciones.addAll(catalogoDemo);
        jdkConfigurado = false;
        actualizarSpinner();

        estadoConexion.setText(R.string.sin_conexion_demo);
        estadoConexion.setTextColor(ContextCompat.getColor(this, R.color.terminal_comando));
        consola.error("No se pudo conectar con la API: " + causa.getMessage());
        consola.info(getString(R.string.aviso_demo));
        if (catalogoDemo.isEmpty()) {
            consola.error("Tampoco se pudo cargar el catalogo de demostracion local.");
        }
        actualizarBotones();
    }

    private void actualizarSpinner() {
        ArrayAdapter<LeccionResumen> adaptador = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, lecciones);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLecciones.setAdapter(adaptador);
        if (!lecciones.isEmpty()) {
            spinnerLecciones.setSelection(0);
        }
        int total = lecciones.size();
        contadorLecciones.setText(total == 1 ? "1 leccion" : total + " lecciones");
    }

    private LeccionResumen leccionSeleccionada() {
        int posicion = spinnerLecciones.getSelectedItemPosition();
        if (posicion < 0 || posicion >= lecciones.size()) {
            return null;
        }
        return lecciones.get(posicion);
    }

    private void abrirLeccionSeleccionada() {
        final LeccionResumen resumen = leccionSeleccionada();
        if (resumen == null) {
            aviso(getString(R.string.error_sin_leccion));
            return;
        }

        if (modoDemo) {
            Leccion encontrada = RepositorioDemo.buscar(catalogoDemo, resumen.getId());
            if (encontrada == null) {
                aviso("No se encontro la leccion de demostracion.");
                return;
            }
            mostrarLeccion(encontrada);
            consola.info("Modo demostracion: la apertura no se registra en la API.");
            return;
        }

        consola.info("Abriendo leccion " + resumen.getNombre() + " ...");
        botonAbrirLeccion.setEnabled(false);
        tareas.ejecutar(new TareaAsincrona.Tarea<Leccion>() {
            @Override
            public Leccion ejecutar() throws Exception {
                return api.obtenerLeccion(resumen.getId());
            }
        }, new TareaAsincrona.Resultado<Leccion>() {
            @Override
            public void exito(Leccion leccion) {
                botonAbrirLeccion.setEnabled(true);
                if (leccion == null) {
                    consola.error("La API devolvio una leccion vacia.");
                    return;
                }
                mostrarLeccion(leccion);
                registrarApertura(leccion);
            }

            @Override
            public void error(Exception e) {
                botonAbrirLeccion.setEnabled(true);
                consola.error("No se pudo abrir la leccion: " + e.getMessage());
                aviso(e.getMessage());
            }
        });
    }

    private void mostrarLeccion(Leccion leccion) {
        leccionActiva = leccion;
        leccionCompletada = false;
        compilacionVigente = false;
        nombreClaseCompilada = null;
        registroId = -1;

        String titulo = leccion.getTituloVisible();
        String objetivo = ObjetivoLeccion.etiquetaSegura(leccion.getObjetivo());
        String nombre = leccion.getNombre() == null ? titulo : leccion.getNombre();

        tituloLeccionActiva.setText(getString(R.string.leccion_activa_etiqueta, titulo));
        subtituloLeccion.setText(nombre + "  ·  " + objetivo);
        chipObjetivo.setText(objetivo);
        chipEstado.setText(R.string.chip_pendiente);
        chipEstado.setBackgroundResource(R.drawable.bg_chip_pendiente);
        chipEstado.setTextColor(ContextCompat.getColor(this, R.color.advertencia));

        tituloLeccion.setText(titulo);
        objetivoLeccion.setText(getString(R.string.objetivo_etiqueta, objetivo));

        String markdown = leccion.getContenidoMd();
        if (markdown == null || markdown.trim().isEmpty()) {
            contenidoLeccion.setText(R.string.contenido_vacio);
        } else {
            contenidoLeccion.setText(MarcadorMarkdown.renderizar(this, markdown));
        }

        String codigo = MarcadorMarkdown.extraerBloqueJava(markdown);
        if (codigo.isEmpty()) {
            codigo = MarcadorMarkdown.plantilla();
        }
        editor.setCodigo(codigo);
        nombreClase.setText(R.string.sin_compilar);

        consola.info("Leccion abierta: " + titulo);
        consola.info("Objetivo: " + objetivo);
        actualizarBotones();
    }

    private void cerrarLeccion() {
        leccionActiva = null;
        leccionCompletada = false;
        compilacionVigente = false;
        nombreClaseCompilada = null;
        registroId = -1;

        tituloLeccionActiva.setText(R.string.titulo_sin_leccion);
        subtituloLeccion.setText(R.string.subtitulo_sin_leccion);
        chipObjetivo.setText(R.string.chip_sin_objetivo);
        chipEstado.setText(R.string.chip_pendiente);
        chipEstado.setBackgroundResource(R.drawable.bg_chip_pendiente);
        chipEstado.setTextColor(ContextCompat.getColor(this, R.color.advertencia));

        tituloLeccion.setText("");
        objetivoLeccion.setText("");
        contenidoLeccion.setText(R.string.contenido_vacio);
        editor.limpiar();
        nombreClase.setText(R.string.sin_compilar);

        consola.info("Leccion cerrada. El panel y el editor quedaron limpios.");
        actualizarBotones();
    }

    private void copiarCodigoAlEditor() {
        if (leccionActiva == null) {
            aviso(getString(R.string.error_sin_leccion));
            return;
        }
        String codigo = MarcadorMarkdown.extraerBloqueJava(leccionActiva.getContenidoMd());
        if (codigo.isEmpty()) {
            aviso("La leccion no contiene un bloque de codigo Java.");
            return;
        }
        editor.setCodigo(codigo);
        editor.enfocar();
        consola.info("Codigo de ejemplo copiado al editor.");
    }

    private void registrarApertura(final Leccion leccion) {
        if (leccion.getObjetivo() == null) {
            consola.error("La leccion no tiene objetivo; no se registro en el historial.");
            return;
        }
        final long idLeccion = leccion.getId();
        final String nombre = leccion.getNombre() == null
                ? leccion.getTituloVisible() : leccion.getNombre();

        tareas.ejecutar(new TareaAsincrona.Tarea<HistorialRegistro>() {
            @Override
            public HistorialRegistro ejecutar() throws Exception {
                return api.registrarHistorial(idLeccion, nombre, leccion.getObjetivo());
            }
        }, new TareaAsincrona.Resultado<HistorialRegistro>() {
            @Override
            public void exito(HistorialRegistro registro) {
                if (registro == null) {
                    return;
                }
                if (leccionActiva != null && leccionActiva.getId() == idLeccion) {
                    registroId = registro.getId();
                    consola.info("Apertura registrada en el historial (registro #" + registroId + ").");
                }
            }

            @Override
            public void error(Exception e) {
                consola.error("No se pudo registrar la apertura en el historial: " + e.getMessage());
            }
        });
    }

    // ------------------------------------------------------------------
    // Compilacion y ejecucion
    // ------------------------------------------------------------------

    private void compilar() {
        if (!validarCompilacion()) {
            return;
        }
        final String codigo = editor.getCodigo();
        consola.comando("compilar");
        botonCompilar.setEnabled(false);
        botonEjecutar.setEnabled(false);

        tareas.ejecutar(new TareaAsincrona.Tarea<ResultadoCompilacion>() {
            @Override
            public ResultadoCompilacion ejecutar() throws Exception {
                return api.compilar(codigo);
            }
        }, new TareaAsincrona.Resultado<ResultadoCompilacion>() {
            @Override
            public void exito(ResultadoCompilacion resultado) {
                if (resultado == null) {
                    consola.error("La API devolvio un resultado de compilacion vacio.");
                    compilacionVigente = false;
                    actualizarBotones();
                    return;
                }
                aplicarCompilacion(resultado);
            }

            @Override
            public void error(Exception e) {
                compilacionVigente = false;
                nombreClaseCompilada = null;
                consola.error("No se pudo compilar: " + e.getMessage());
                actualizarBotones();
            }
        });
    }

    private boolean validarCompilacion() {
        if (leccionActiva == null) {
            aviso(getString(R.string.error_sin_leccion));
            return false;
        }
        if (modoDemo) {
            aviso(getString(R.string.aviso_demo));
            return false;
        }
        if (!jdkConfigurado) {
            aviso(getString(R.string.aviso_jdk));
            return false;
        }
        if (editor.getCodigo().trim().isEmpty()) {
            aviso(getString(R.string.error_sin_codigo));
            return false;
        }
        return true;
    }

    private void aplicarCompilacion(ResultadoCompilacion resultado) {
        if (resultado.isExitoso()) {
            compilacionVigente = true;
            nombreClaseCompilada = resultado.getNombreClase();
            consola.exito("Compilacion exitosa. Clase generada: " + resultado.getNombreClase() + ".");
            nombreClase.setText(resultado.getNombreClase());
            consola.salida(resultado.getSalida());
            evaluarCompilacion(true);
        } else {
            compilacionVigente = false;
            nombreClaseCompilada = null;
            consola.error("La compilacion reporto errores.");
            nombreClase.setText(R.string.sin_compilar);
            consola.salida(resultado.getSalida());
            evaluarCompilacion(false);
        }
        actualizarBotones();
    }

    private void ejecutar() {
        if (leccionActiva == null) {
            aviso(getString(R.string.error_sin_leccion));
            return;
        }
        if (!compilacionVigente || nombreClaseCompilada == null) {
            aviso(getString(R.string.error_compilar_primero));
            return;
        }

        final String clase = nombreClaseCompilada;
        consola.comando("java " + clase);
        botonCompilar.setEnabled(false);
        botonEjecutar.setEnabled(false);

        tareas.ejecutar(new TareaAsincrona.Tarea<ResultadoEjecucion>() {
            @Override
            public ResultadoEjecucion ejecutar() throws Exception {
                return api.ejecutar(clase, 10);
            }
        }, new TareaAsincrona.Resultado<ResultadoEjecucion>() {
            @Override
            public void exito(ResultadoEjecucion resultado) {
                if (resultado == null) {
                    consola.error("La API devolvio un resultado de ejecucion vacio.");
                    actualizarBotones();
                    return;
                }
                aplicarEjecucion(resultado);
            }

            @Override
            public void error(Exception e) {
                consola.error("No se pudo ejecutar: " + e.getMessage());
                actualizarBotones();
            }
        });
    }

    private void aplicarEjecucion(ResultadoEjecucion resultado) {
        if (resultado.isTimeout()) {
            consola.error("La ejecucion supero el tiempo limite y fue detenida.");
        } else if (resultado.isExitoso()) {
            consola.exito("La ejecucion termino sin errores.");
        } else {
            consola.error("La ejecucion termino con errores.");
        }
        consola.salida(resultado.getSalida());
        consola.salida(resultado.getErrores());
        evaluarEjecucion(resultado);
        actualizarBotones();
    }

    private void evaluarCompilacion(boolean exitoso) {
        if (leccionCompletada || leccionActiva == null) {
            return;
        }
        if (EvaluadorObjetivos.cumpleAlCompilar(leccionActiva.getObjetivo(), exitoso)) {
            completarLeccion("Se cumplio el objetivo "
                    + ObjetivoLeccion.etiquetaSegura(leccionActiva.getObjetivo()) + " al compilar.");
        }
    }

    private void evaluarEjecucion(ResultadoEjecucion resultado) {
        if (leccionCompletada || leccionActiva == null) {
            return;
        }
        if (EvaluadorObjetivos.cumpleAlEjecutar(leccionActiva.getObjetivo(),
                resultado.isExitoso(), resultado.isTimeout())) {
            completarLeccion("Se cumplio el objetivo "
                    + ObjetivoLeccion.etiquetaSegura(leccionActiva.getObjetivo()) + " al ejecutar.");
        }
    }

    private void completarLeccion(final String detalle) {
        leccionCompletada = true;
        chipEstado.setText(R.string.chip_completada);
        chipEstado.setBackgroundResource(R.drawable.bg_chip_completada);
        chipEstado.setTextColor(ContextCompat.getColor(this, R.color.exito));
        consola.exito("Leccion completada: " + leccionActiva.getTituloVisible());

        if (registroId > 0 && !modoDemo) {
            final long id = registroId;
            tareas.ejecutar(new TareaAsincrona.Tarea<HistorialRegistro>() {
                @Override
                public HistorialRegistro ejecutar() throws Exception {
                    return api.marcarCompletada(id, detalle);
                }
            }, new TareaAsincrona.Resultado<HistorialRegistro>() {
                @Override
                public void exito(HistorialRegistro registro) {
                    consola.info("Historial actualizado: registro #" + id + " marcado como COMPLETADA.");
                }

                @Override
                public void error(Exception e) {
                    consola.error("No se pudo actualizar el historial: " + e.getMessage());
                }
            });
        } else {
            consola.info("El estado se muestra en pantalla, pero no hay un registro de historial que actualizar.");
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.leccion_completada_titulo)
                .setMessage(getString(R.string.leccion_completada_mensaje, detalle))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void actualizarBotones() {
        boolean hayLeccion = leccionActiva != null;
        boolean puedeCompilar = hayLeccion && !modoDemo && jdkConfigurado;

        botonCompilar.setEnabled(puedeCompilar);
        botonEjecutar.setEnabled(puedeCompilar && compilacionVigente);
        botonAbrirLeccion.setEnabled(!lecciones.isEmpty());

        if (!hayLeccion) {
            ayudaJdk.setText(R.string.ayuda_jdk_inicial);
        } else if (modoDemo) {
            ayudaJdk.setText(R.string.ayuda_demo);
        } else if (!jdkConfigurado) {
            ayudaJdk.setText(R.string.aviso_jdk);
        } else {
            ayudaJdk.setText(R.string.ayuda_jdk_listo);
        }
    }

    // ------------------------------------------------------------------
    // Configuracion y ayuda
    // ------------------------------------------------------------------

    private void actualizarEstadoJdk() {
        if (modoDemo) {
            jdkConfigurado = false;
            actualizarBotones();
            return;
        }
        tareas.ejecutar(new TareaAsincrona.Tarea<EstadoJdk>() {
            @Override
            public EstadoJdk ejecutar() throws Exception {
                return api.estadoJdk();
            }
        }, new TareaAsincrona.Resultado<EstadoJdk>() {
            @Override
            public void exito(EstadoJdk estado) {
                jdkConfigurado = estado != null && estado.isConfigurado();
                if (jdkConfigurado) {
                    consola.info("JDK del servidor: " + estado.getRuta());
                } else {
                    consola.info("JDK no configurado. Use Configuracion > Configurar JDK.");
                }
                actualizarBotones();
            }

            @Override
            public void error(Exception e) {
                jdkConfigurado = false;
                consola.error("No se pudo consultar el estado del JDK: " + e.getMessage());
                actualizarBotones();
            }
        });
    }

    private void mostrarConfiguracion() {
        DialogoConfiguracion.mostrar(this, api, preferencias,
                new DialogoConfiguracion.AlGuardar() {
                    @Override
                    public void alGuardar(EstadoJdk estado, String apiUrl) {
                        api = new ApiCliente(apiUrl);
                        jdkConfigurado = estado != null && estado.isConfigurado();
                        consola.info("Direccion de la API: " + api.getBaseUrl());
                        if (jdkConfigurado) {
                            consola.exito("JDK configurado: " + estado.getRuta());
                        } else {
                            consola.error("El JDK del servidor aun no esta configurado.");
                        }
                        cargandoLecciones = false;
                        actualizarBotones();
                        cargarLecciones();
                    }
                });
    }

    private void mostrarDialogoApi() {
        final EditText campo = new EditText(this);
        campo.setText(preferencias.getApiUrl());
        campo.setSingleLine(true);
        campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

        FrameLayout contenedor = new FrameLayout(this);
        int margen = (int) (20 * getResources().getDisplayMetrics().density);
        contenedor.setPadding(margen, margen / 2, margen, 0);
        contenedor.addView(campo, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_direccion_api)
                .setView(contenedor)
                .setPositiveButton(R.string.guardar, (dialogo, boton) -> {
                    String url = campo.getText().toString().trim();
                    if (url.isEmpty()) {
                        aviso("Indique una direccion valida.");
                        return;
                    }
                    preferencias.setApiUrl(url);
                    api = new ApiCliente(url);
                    consola.info("Direccion de la API actualizada: " + api.getBaseUrl());
                    cargandoLecciones = false;
                    cargarLecciones();
                    actualizarEstadoJdk();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void mostrarAcercaDe() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.acerca_titulo)
                .setMessage(R.string.acerca_texto)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void aviso(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tareas != null) {
            tareas.shutdown();
        }
    }
}
