package gt.edu.umg.practicacodigo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import gt.edu.umg.practicacodigo.util.MarcadorMarkdown;

/** Pantalla de ayuda con la guia de uso de la aplicacion. */
public class AyudaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAyuda);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView contenido = findViewById(R.id.contenidoAyuda);
        contenido.setText(MarcadorMarkdown.renderizar(this, leerAyuda()));

        MaterialButton cerrar = findViewById(R.id.botonCerrarAyuda);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String leerAyuda() {
        StringBuilder texto = new StringBuilder();
        try (InputStream entrada = getResources().openRawResource(R.raw.ayuda);
             BufferedReader lector = new BufferedReader(
                     new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                texto.append(linea).append('\n');
            }
        } catch (IOException e) {
            return "No se pudo cargar el archivo de ayuda.";
        }
        return texto.toString();
    }
}
