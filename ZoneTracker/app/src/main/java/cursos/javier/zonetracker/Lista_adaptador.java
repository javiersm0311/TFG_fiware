package cursos.javier.zonetracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public abstract class Lista_adaptador extends BaseAdapter {

    private List<?> entradas;
    private int R_layout_IdView;
    private Context contexto;
    private String unidad;
    private OnZoneClickListener onZoneClickListener;

    public Lista_adaptador(Context contexto, int R_layout_IdView, List<?> entradas, String unidad, OnZoneClickListener listener) {
        super();
        this.contexto = contexto;
        this.entradas = entradas;
        this.R_layout_IdView = R_layout_IdView;
        this.unidad = unidad;
        this.onZoneClickListener = listener;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup pariente) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView, null);
        }
        Zona entrada = (Zona) entradas.get(posicion);

        TextView texto_id = (TextView) view.findViewById(R.id.textView_id);
        if (texto_id != null)
            texto_id.setText("ID: " + entrada.getID());

        TextView texto_coordenadas = (TextView) view.findViewById(R.id.textView_coordenadas);
        if (texto_coordenadas != null)
            texto_coordenadas.setText("COORDENADAS: " +entrada.getCOORDENADAS());

        Button detalleButton = (Button) view.findViewById(R.id.detalleButton);
        if (detalleButton != null) {
            detalleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onZoneClickListener.onZoneClick(entrada);
                }
            });
        }else {
            Log.e("Lista_adaptador", "detalleButton is null");
        }

        return view;
    }

    @Override
    public int getCount() {
        return entradas.size();
    }

    @Override
    public Object getItem(int posicion) {
        return entradas.get(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public abstract void onEntrada (Object entrada, View view);

    public void remove(Object entrada) {
        if (entradas != null && entradas.contains(entrada)) {
            entradas.remove(entrada);
            notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
        }
    }
    public interface OnZoneClickListener {
        void onZoneClick(Zona zona);
    }
    public void updateData(List<Zona> newZones) {
        this.entradas = newZones;
        notifyDataSetChanged();
    }

}
