package gt.edu.umg.practicacodigo.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;

import androidx.core.content.ContextCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gt.edu.umg.practicacodigo.R;

/**
 * Renderizador Markdown minimo basado en Spannable. Soporta titulos, listas,
 * citas, negritas, codigo en linea, bloques de codigo, enlaces e imagenes
 * (las imagenes se muestran como una referencia textual).
 */
public final class MarcadorMarkdown {

    private static final Pattern LISTA_NUMERADA = Pattern.compile("^\\d+[.)]\\s+.*");
    private static final Pattern BLOQUE_JAVA = Pattern.compile(
            "```(?:java)?[ \\t]*\\r?\\n(.*?)```", Pattern.DOTALL);

    private MarcadorMarkdown() {
    }

    public static CharSequence renderizar(Context contexto, String markdown) {
        SpannableStringBuilder salida = new SpannableStringBuilder();
        if (markdown == null || markdown.trim().isEmpty()) {
            return salida;
        }

        int colorPrimario = ContextCompat.getColor(contexto, R.color.primario);
        int colorTexto = ContextCompat.getColor(contexto, R.color.texto_secundario);
        int colorTenue = ContextCompat.getColor(contexto, R.color.texto_tenue);
        int colorCodigoFondo = ContextCompat.getColor(contexto, R.color.codigo_fondo);
        int colorCodigoTexto = ContextCompat.getColor(contexto, R.color.codigo_texto);

        String normalizado = markdown.replace("\r\n", "\n").replace('\r', '\n');
        String[] lineas = normalizado.split("\n", -1);

        boolean enCodigo = false;
        StringBuilder bloque = new StringBuilder();

        for (String linea : lineas) {
            String recortada = linea.trim();

            if (recortada.startsWith("```")) {
                if (enCodigo) {
                    agregarBloqueCodigo(salida, bloque.toString(), colorCodigoFondo, colorCodigoTexto);
                    bloque.setLength(0);
                    enCodigo = false;
                } else {
                    enCodigo = true;
                }
                continue;
            }

            if (enCodigo) {
                bloque.append(linea).append('\n');
                continue;
            }

            if (recortada.isEmpty()) {
                salida.append('\n');
                continue;
            }

            if (recortada.startsWith("#")) {
                int nivel = 0;
                while (nivel < recortada.length() && recortada.charAt(nivel) == '#') {
                    nivel++;
                }
                String texto = recortada.substring(nivel).trim();
                int inicio = salida.length();
                salida.append(texto);
                int fin = salida.length();
                float escala = nivel <= 1 ? 1.4f : (nivel == 2 ? 1.22f : 1.08f);
                salida.setSpan(new RelativeSizeSpan(escala), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                salida.setSpan(new StyleSpan(Typeface.BOLD), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                salida.setSpan(new ForegroundColorSpan(colorPrimario), inicio, fin,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                salida.append('\n');
                continue;
            }

            if (recortada.startsWith(">")) {
                String texto = recortada.substring(1).trim();
                int inicio = salida.length();
                salida.append("▎ ");
                agregarInline(salida, texto, colorPrimario, colorTenue, colorCodigoTexto, colorCodigoFondo);
                salida.setSpan(new StyleSpan(Typeface.ITALIC), inicio, salida.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                salida.setSpan(new ForegroundColorSpan(colorTenue), inicio, salida.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                salida.append('\n');
                continue;
            }

            if (recortada.equals("---") || recortada.equals("***")) {
                int inicio = salida.length();
                salida.append("────────────────────");
                salida.setSpan(new ForegroundColorSpan(colorTenue), inicio, salida.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                salida.append('\n');
                continue;
            }

            if (recortada.startsWith("- ") || recortada.startsWith("* ")) {
                salida.append("•  ");
                agregarInline(salida, recortada.substring(2).trim(), colorPrimario, colorTenue,
                        colorCodigoTexto, colorCodigoFondo);
                salida.append('\n');
                continue;
            }

            if (LISTA_NUMERADA.matcher(recortada).matches()) {
                int espacio = recortada.indexOf(' ');
                salida.append(recortada, 0, espacio).append("  ");
                agregarInline(salida, recortada.substring(espacio + 1), colorPrimario, colorTenue,
                        colorCodigoTexto, colorCodigoFondo);
                salida.append('\n');
                continue;
            }

            agregarInline(salida, recortada, colorPrimario, colorTenue, colorCodigoTexto, colorCodigoFondo);
            salida.append('\n');
        }

        if (enCodigo && bloque.length() > 0) {
            agregarBloqueCodigo(salida, bloque.toString(), colorCodigoFondo, colorCodigoTexto);
        }
        return salida;
    }

    private static void agregarInline(SpannableStringBuilder salida, String texto, int colorPrimario,
                                      int colorTenue, int colorCodigoTexto, int colorCodigoFondo) {
        int i = 0;
        while (i < texto.length()) {
            if (texto.startsWith("![", i)) {
                int cierraAlt = texto.indexOf(']', i + 2);
                int abreUrl = cierraAlt >= 0 ? texto.indexOf('(', cierraAlt) : -1;
                int cierraUrl = abreUrl >= 0 ? texto.indexOf(')', abreUrl) : -1;
                if (cierraAlt > i && abreUrl == cierraAlt + 1 && cierraUrl > abreUrl) {
                    String alt = texto.substring(i + 2, cierraAlt).trim();
                    int inicio = salida.length();
                    salida.append("[imagen: ").append(alt.isEmpty() ? "recurso" : alt).append(']');
                    salida.setSpan(new StyleSpan(Typeface.ITALIC), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    salida.setSpan(new ForegroundColorSpan(colorTenue), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i = cierraUrl + 1;
                    continue;
                }
            }

            if (texto.charAt(i) == '[') {
                int cierraTexto = texto.indexOf(']', i + 1);
                int abreUrl = cierraTexto >= 0 ? texto.indexOf('(', cierraTexto) : -1;
                int cierraUrl = abreUrl >= 0 ? texto.indexOf(')', abreUrl) : -1;
                if (cierraTexto > i && abreUrl == cierraTexto + 1 && cierraUrl > abreUrl) {
                    int inicio = salida.length();
                    salida.append(texto, i + 1, cierraTexto);
                    salida.setSpan(new ForegroundColorSpan(colorPrimario), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    salida.setSpan(new UnderlineSpan(), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i = cierraUrl + 1;
                    continue;
                }
            }

            if (texto.startsWith("**", i)) {
                int fin = texto.indexOf("**", i + 2);
                if (fin > i + 2) {
                    int inicio = salida.length();
                    salida.append(texto, i + 2, fin);
                    salida.setSpan(new StyleSpan(Typeface.BOLD), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i = fin + 2;
                    continue;
                }
            }

            if (texto.charAt(i) == '`') {
                int fin = texto.indexOf('`', i + 1);
                if (fin > i + 1) {
                    int inicio = salida.length();
                    salida.append(texto, i + 1, fin);
                    salida.setSpan(new TypefaceSpan("monospace"), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    salida.setSpan(new ForegroundColorSpan(colorCodigoTexto), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    salida.setSpan(new BackgroundColorSpan(colorCodigoFondo), inicio, salida.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i = fin + 1;
                    continue;
                }
            }

            salida.append(texto.charAt(i));
            i++;
        }
    }

    private static void agregarBloqueCodigo(SpannableStringBuilder salida, String codigo,
                                            int colorFondo, int colorTexto) {
        String[] lineas = codigo.split("\n", -1);
        int inicio = salida.length();
        for (String linea : lineas) {
            salida.append("  ").append(linea).append('\n');
        }
        int fin = salida.length();
        salida.setSpan(new TypefaceSpan("monospace"), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        salida.setSpan(new ForegroundColorSpan(colorTexto), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        salida.setSpan(new BackgroundColorSpan(colorFondo), inicio, fin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /** Extrae el primer bloque de codigo Java del contenido de una leccion. */
    public static String extraerBloqueJava(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Matcher matcher = BLOQUE_JAVA.matcher(markdown.replace("\r\n", "\n"));
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    public static String plantilla() {
        return "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "        System.out.println(\"Hola, Programacion 2\");\n"
                + "    }\n"
                + "}\n";
    }
}
