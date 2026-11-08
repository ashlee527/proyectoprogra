package gt.edu.umg.practicacodigo.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gt.edu.umg.practicacodigo.R;
import gt.edu.umg.practicacodigo.modelo.HistorialRegistro;
import gt.edu.umg.practicacodigo.modelo.ObjetivoLeccion;
import gt.edu.umg.practicacodigo.util.Formato;

/** Adaptador de la tabla de historial de sesion. */
public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.Titular> {

    private final List<HistorialRegistro> registros = new ArrayList<>();

    public void setRegistros(List<HistorialRegistro> nuevos) {
        registros.clear();
        if (nuevos != null) {
            registros.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Titular onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_historial, padre, false);
        return new Titular(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Titular titular, int posicion) {
        HistorialRegistro registro = registros.get(posicion);
        String nombre = registro.getLeccionNombre();
        titular.leccion.setText(nombre == null || nombre.trim().isEmpty()
                ? "Leccion sin nombre" : nombre);
        titular.apertura.setText(Formato.fechaHora(registro.getApertura()) + "  ·  "
                + ObjetivoLeccion.etiquetaSegura(registro.getObjetivo()));

        String detalle = registro.getDetalle();
        if (detalle == null || detalle.trim().isEmpty()) {
            titular.detalle.setVisibility(View.GONE);
        } else {
            titular.detalle.setVisibility(View.VISIBLE);
            titular.detalle.setText(detalle);
        }

        if (registro.estaCompletada()) {
            titular.estado.setText(R.string.chip_completada);
            titular.estado.setBackgroundResource(R.drawable.bg_chip_completada);
            titular.estado.setTextColor(ContextCompat.getColor(titular.itemView.getContext(),
                    R.color.exito));
        } else {
            titular.estado.setText(R.string.chip_pendiente);
            titular.estado.setBackgroundResource(R.drawable.bg_chip_pendiente);
            titular.estado.setTextColor(ContextCompat.getColor(titular.itemView.getContext(),
                    R.color.advertencia));
        }
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    static class Titular extends RecyclerView.ViewHolder {

        final TextView leccion;
        final TextView apertura;
        final TextView detalle;
        final TextView estado;

        Titular(@NonNull View item) {
            super(item);
            leccion = item.findViewById(R.id.itemLeccion);
            apertura = item.findViewById(R.id.itemApertura);
            detalle = item.findViewById(R.id.itemDetalle);
            estado = item.findViewById(R.id.itemEstado);
        }
    }
}
