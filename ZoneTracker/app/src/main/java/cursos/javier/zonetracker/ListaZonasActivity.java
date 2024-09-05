package cursos.javier.zonetracker;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class ListaZonasActivity extends Activity implements Lista_adaptador.OnZoneClickListener {

    private RequestQueue mRequestQueue;
    private ZonesClient zonesClient;
    private ListView lista;
    private Button atrasButton;
    private static final int REQUEST_CODE_DETALLE = 1;

    private Lista_adaptador adapter;
    private String valor;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        valor = getIntent().getExtras().getString("unidad");
        mRequestQueue = Volley.newRequestQueue(this);

        lista = (ListView) findViewById(R.id.listado);

        zonesClient = new ZonesClient(mRequestQueue);
        zonesClient.getZones("unidad" + valor, new ZonesCallback(){
            @Override
            public void onSuccess(List<Zona> zonas) {
                System.out.println(zonas);
                if(!zonas.isEmpty() ) {
                    adapter = new Lista_adaptador(ListaZonasActivity.this, R.layout.zona, zonas, valor,ListaZonasActivity.this) {
                        @Override
                        public void onEntrada(Object entrada, View view) {
                            if (entrada != null) {
                                Zona zona = (Zona) entrada;

                                TextView texto_id = (TextView) view.findViewById(R.id.textView_id);
                                if (texto_id != null)
                                    texto_id.setText(zona.getID());

                                TextView texto_coordenadas = (TextView) view.findViewById(R.id.textView_coordenadas);
                                if (texto_coordenadas != null)
                                    texto_coordenadas.setText(zona.getCOORDENADAS());

                            }
                        }
                    };
                    lista.setAdapter(adapter);
                }else{
                    showAlertDialog(ListaZonasActivity.this,"","No hay zonas en esta unidad");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error getting zones: " + error);
            }
        });
        atrasButton = (Button) findViewById(R.id.atrasButton);
        atrasButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onBackPressed();

            }
        });


    };

    public void onZoneClick(Zona zona) {
        Intent intent = new Intent(this, DetalleZonaActivity.class);
        intent.putExtra("zonaId", zona.getID());
        intent.putExtra("coordenadas", zona.getCOORDENADAS());
        intent.putExtra("unidadId", valor);
        startActivityForResult(intent, REQUEST_CODE_DETALLE);
    }

    protected void onResume() {
        super.onResume();
        loadZones();
    }

    private void loadZones() {
        // Método para cargar las zonas desde el servidor
        zonesClient.getZones("unidad" + valor, new ZonesCallback() {
            @Override
            public void onSuccess(List<Zona> zonas) {
                if (adapter == null) {
                    adapter = new Lista_adaptador(ListaZonasActivity.this, R.layout.zona, zonas, valor, ListaZonasActivity.this) {
                        @Override
                        public void onEntrada(Object entrada, View view) {
                            if (entrada != null) {
                                Zona zona = (Zona) entrada;

                                TextView texto_id = view.findViewById(R.id.textView_id);
                                if (texto_id != null)
                                    texto_id.setText(zona.getID());

                                TextView texto_coordenadas = view.findViewById(R.id.textView_coordenadas);
                                if (texto_coordenadas != null)
                                    texto_coordenadas.setText(zona.getCOORDENADAS());
                            }
                        }
                    };
                    lista.setAdapter(adapter);
                } else {
                    adapter.updateData(zonas);
                }
            }

            @Override
            public void onError(String error) {

                Log.e(TAG, "Error cargando zonas: " + error);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETALLE && resultCode == RESULT_OK) {
            if (data != null) {
                String zonaIdEliminada = data.getStringExtra("zonaIdEliminada");
                if (zonaIdEliminada != null) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        Zona zona = (Zona) adapter.getItem(i);
                        if (zona.getID().equals(zonaIdEliminada)) {
                            adapter.remove(zona);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }
    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNeutralButton("Volver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (context instanceof Activity) {

                    ((Activity) context).finish();
                } else {
                    Toast.makeText(context, "No es posible volver a la actividad anterior", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
};
