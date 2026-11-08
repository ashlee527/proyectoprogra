package gt.edu.umg.practicacodigo;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gt.edu.umg.practicacodigo.api.ApiCliente;
import gt.edu.umg.practicacodigo.modelo.HistorialRegistro;
import gt.edu.umg.practicacodigo.util.Preferencias;
import gt.edu.umg.practicacodigo.util.TareaAsincrona;
import gt.edu.umg.practicacodigo.vista.AdaptadorHistorial;

/** Panel del historial de sesion: leccion, hora de apertura y estado. */
public class HistorialActivity extends AppCompatActivity {

    private TextView resumenHistorial;
    private TextView textoVacio;
    private ProgressBar progresoHistorial;
    private RecyclerView listaHistorial;
    private MaterialButton botonActualizar;
    private AdaptadorHistorial adaptador;

    private ApiCliente api;
    private TareaAsincrona tareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        MaterialToolbar toolbar = findViewById(R.id.toolbarHistorial);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resumenHistorial = findViewById(R.id.resumenHistorial);
        textoVacio = findViewById(R.id.textoVacio);
        progresoHistorial = findViewById(R.id.progresoHistorial);
        listaHistorial = findViewById(R.id.listaHistorial);
        botonActualizar = findViewById(R.id.botonActualizar);
        MaterialButton botonCerrar = findViewById(R.id.botonCerrar);

        adaptador = new AdaptadorHistorial();
        listaHistorial.setLayoutManager(new LinearLayoutManager(this));
        listaHistorial.setAdapter(adaptador);

        api = new ApiCliente(new Preferencias(this).getApiUrl());
        tareas = new TareaAsincrona();

        botonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarHistorial();
            }
        });
        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cargarHistorial();
    }

    private void cargarHistorial() {
        progresoHistorial.setVisibility(View.VISIBLE);
        textoVacio.setVisibility(View.GONE);
        listaHistorial.setVisibility(View.INVISIBLE);
        resumenHistorial.setText(R.string.historial_cargando);
        botonActualizar.setEnabled(false);

        tareas.ejecutar(new TareaAsincrona.Tarea<List<HistorialRegistro>>() {
            @Override
            public List<HistorialRegistro> ejecutar() throws Exception {
                return api.listarHistorial();
            }
        }, new TareaAsincrona.Resultado<List<HistorialRegistro>>() {
            @Override
            public void exito(List<HistorialRegistro> registros) {
                botonActualizar.setEnabled(true);
                progresoHistorial.setVisibility(View.GONE);
                listaHistorial.setVisibility(View.VISIBLE);

                if (registros == null || registros.isEmpty()) {
                    adaptador.setRegistros(null);
                    textoVacio.setText(R.string.historial_vacio);
                    textoVacio.setVisibility(View.VISIBLE);
                    resumenHistorial.setText(getString(R.string.registros_etiqueta, 0, 0));
                    return;
                }

                Collections.sort(registros, new Comparator<HistorialRegistro>() {
                    @Override
                    public int compare(HistorialRegistro a, HistorialRegistro b) {
                        String fechaA = a.getApertura() == null ? "" : a.getApertura();
                        String fechaB = b.getApertura() == null ? "" : b.getApertura();
                        return fechaB.compareTo(fechaA);
                    }
                });

                int completadas = 0;
                for (HistorialRegistro registro : registros) {
                    if (registro.estaCompletada()) {
                        completadas++;
                    }
                }
                adaptador.setRegistros(registros);
                textoVacio.setVisibility(View.GONE);
                resumenHistorial.setText(getString(R.string.registros_etiqueta,
                        registros.size(), completadas));
            }

            @Override
            public void error(Exception e) {
                botonActualizar.setEnabled(true);
                progresoHistorial.setVisibility(View.GONE);
                listaHistorial.setVisibility(View.INVISIBLE);
                adaptador.setRegistros(null);
                textoVacio.setText(getString(R.string.historial_error) + "\n\n" + e.getMessage());
                textoVacio.setVisibility(View.VISIBLE);
                resumenHistorial.setText(R.string.historial_error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tareas != null) {
            tareas.shutdown();
        }
    }
}
