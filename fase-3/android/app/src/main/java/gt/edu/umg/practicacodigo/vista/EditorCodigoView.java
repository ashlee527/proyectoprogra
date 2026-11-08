package gt.edu.umg.practicacodigo.vista;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import gt.edu.umg.practicacodigo.R;

/**
 * Editor de codigo con numeracion de lineas, fuente monoespaciada y
 * desplazamiento horizontal para lineas largas.
 */
public class EditorCodigoView extends LinearLayout {

    private TextView numeros;
    private EditText editor;
    private HorizontalScrollView desplazamiento;
    private Runnable alCambiarCodigo;
    private boolean actualizando;

    public EditorCodigoView(Context contexto) {
        super(contexto);
        inicializar(contexto);
    }

    public EditorCodigoView(Context contexto, AttributeSet atributos) {
        super(contexto, atributos);
        inicializar(contexto);
    }

    public EditorCodigoView(Context contexto, AttributeSet atributos, int estilo) {
        super(contexto, atributos, estilo);
        inicializar(contexto);
    }

    private void inicializar(Context contexto) {
        setOrientation(HORIZONTAL);
        setBackgroundColor(ContextCompat.getColor(contexto, R.color.codigo_fondo));

        numeros = new TextView(contexto);
        numeros.setTextColor(ContextCompat.getColor(contexto, R.color.codigo_numero));
        numeros.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        numeros.setTypeface(Typeface.MONOSPACE);
        numeros.setGravity(Gravity.TOP | Gravity.END);
        numeros.setIncludeFontPadding(false);
        numeros.setLineSpacing(0f, 1.25f);
        numeros.setPadding(dp(6), dp(8), dp(6), dp(8));
        numeros.setBackgroundColor(Color.parseColor("#191D2C"));
        LayoutParams parametrosNumeros = new LayoutParams(
                dp(40), LayoutParams.MATCH_PARENT);
        addView(numeros, parametrosNumeros);

        desplazamiento = new HorizontalScrollView(contexto);
        desplazamiento.setFillViewport(true);
        desplazamiento.setHorizontalScrollBarEnabled(true);
        LayoutParams parametrosScroll = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
        addView(desplazamiento, parametrosScroll);

        editor = new EditText(contexto);
        editor.setBackgroundColor(Color.TRANSPARENT);
        editor.setTextColor(ContextCompat.getColor(contexto, R.color.codigo_texto));
        editor.setHintTextColor(ContextCompat.getColor(contexto, R.color.codigo_numero));
        editor.setHint("// Escriba aqui su codigo Java");
        editor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        editor.setTypeface(Typeface.MONOSPACE);
        editor.setGravity(Gravity.TOP | Gravity.START);
        editor.setIncludeFontPadding(false);
        editor.setLineSpacing(0f, 1.25f);
        editor.setPadding(dp(8), dp(8), dp(20), dp(8));
        editor.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editor.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        editor.setHorizontallyScrolling(true);
        editor.setVerticalScrollBarEnabled(true);
        editor.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        editor.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        desplazamiento.addView(editor);

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int inicio, int cantidad, int despues) {
            }

            @Override
            public void onTextChanged(CharSequence s, int inicio, int antes, int despues) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                actualizarNumeros();
                if (!actualizando && alCambiarCodigo != null) {
                    alCambiarCodigo.run();
                }
            }
        });

        editor.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int anteriorX, int anteriorY) {
                numeros.scrollTo(0, scrollY);
            }
        });

        actualizarNumeros();
    }

    public void setAlCambiarCodigo(Runnable accion) {
        this.alCambiarCodigo = accion;
    }

    public void setCodigo(String codigo) {
        actualizando = true;
        editor.setText(codigo == null ? "" : codigo);
        if (editor.getText() != null) {
            editor.setSelection(editor.getText().length());
        }
        actualizando = false;
        actualizarNumeros();
    }

    public String getCodigo() {
        return editor.getText() == null ? "" : editor.getText().toString();
    }

    public void limpiar() {
        setCodigo("");
    }

    public void enfocar() {
        editor.requestFocus();
    }

    private void actualizarNumeros() {
        String texto = editor.getText() == null ? "" : editor.getText().toString();
        int total = texto.split("\n", -1).length;
        StringBuilder lineas = new StringBuilder();
        for (int i = 1; i <= total; i++) {
            if (i > 1) {
                lineas.append('\n');
            }
            lineas.append(i);
        }
        numeros.setText(lineas.toString());
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }
}
