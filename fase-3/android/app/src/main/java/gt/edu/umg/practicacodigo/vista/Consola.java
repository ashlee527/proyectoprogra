package gt.edu.umg.practicacodigo.vista;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gt.edu.umg.practicacodigo.R;

/** Consola de salida con lineas coloreadas y desplazamiento automatico. */
public class Consola {

    private final TextView vista;
    private final SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final int colorInfo;
    private final int colorExito;
    private final int colorError;
    private final int colorComando;
    private final int colorSalida;

    public Consola(TextView vista) {
        this.vista = vista;
        this.colorInfo = ContextCompat.getColor(vista.getContext(), R.color.terminal_info);
        this.colorExito = ContextCompat.getColor(vista.getContext(), R.color.terminal_exito);
        this.colorError = ContextCompat.getColor(vista.getContext(), R.color.terminal_error);
        this.colorComando = ContextCompat.getColor(vista.getContext(), R.color.terminal_comando);
        this.colorSalida = ContextCompat.getColor(vista.getContext(), R.color.consola_texto);
        vista.setTypeface(Typeface.MONOSPACE);
    }

    public void info(String mensaje) {
        agregar(mensaje, colorInfo);
    }

    public void exito(String mensaje) {
        agregar(mensaje, colorExito);
    }

    public void error(String mensaje) {
        agregar(mensaje, colorError);
    }

    public void comando(String mensaje) {
        agregar("$ " + mensaje, colorComando);
    }

    public void salida(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return;
        }
        agregar(mensaje, colorSalida);
    }

    public void limpiar() {
        vista.setText("");
    }

    private void agregar(String mensaje, int color) {
        String texto = mensaje == null ? "" : mensaje;
        SpannableStringBuilder linea = new SpannableStringBuilder();
        linea.append(hora.format(new Date())).append("  ").append(texto);
        linea.setSpan(new ForegroundColorSpan(colorInfo), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linea.setSpan(new ForegroundColorSpan(color), 8, linea.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (vista.length() > 0) {
            vista.append("\n");
        }
        vista.append(linea);
        desplazarAlFinal();
    }

    private void desplazarAlFinal() {
        vista.post(new Runnable() {
            @Override
            public void run() {
                if (vista.getParent() instanceof ScrollView) {
                    ((ScrollView) vista.getParent()).fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });
    }
}
