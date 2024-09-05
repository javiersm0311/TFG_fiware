package cursos.javier.zonetracker;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import android.widget.TextView;

public class DetalleZonaActivity extends AppCompatActivity
        implements
        OnMapReadyCallback {
    private Button atrasButton;
    private TextView textViewId;
    private TextView textViewCoordenadas;

    private Button googleMapsButton;
    private Button deleteZoneButton;
    private ZonesClient zoneClient;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        textViewId = findViewById(R.id.textView_id);
        textViewCoordenadas = findViewById(R.id.textView_coordenadas);

        String id = getIntent().getStringExtra("zonaId");
        String coordenadas = getIntent().getStringExtra("coordenadas");

        String[] parts = coordenadas.split(",");
        double latitude = Double.parseDouble(parts[0].trim());
        double longitude = Double.parseDouble(parts[1].trim());

        textViewId.setText("ID: " + id);
        textViewCoordenadas.setText("COORDENADAS: " + coordenadas);

        googleMapsButton = findViewById(R.id.openGoogleMapsButton);
        googleMapsButton.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Label+Name)");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        atrasButton = (Button) findViewById(R.id.atrasButton);
        atrasButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onBackPressed();

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        zoneClient = new ZonesClient(requestQueue);

        deleteZoneButton = (Button) findViewById(R.id.deleteZoneButton);
        deleteZoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                showDeleteConfirmationDialog(id);
            }
        });

    }

    private void showDeleteConfirmationDialog(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Está seguro que desea eliminar esta zona?")
                .setPositiveButton("Aceptar", (dialog, which) -> deleteZone(id))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteZone(String id) {
        zoneClient.deleteZone(id, new DeleteZoneCallback() {
            @Override
            public void onSuccess() {
                String unidadId = getIntent().getStringExtra("unidadId");
                OrionClient orionClient = new OrionClient(DetalleZonaActivity.this);
                orionClient.deleteZoneFromOrion(id, unidadId, new DeleteZoneCallback() {
                    @Override
                    public void onSuccess() {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("zonaIdEliminada", id);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error eliminando la zona en Orion: " + error);
                    }
                });
                Intent returnIntent = new Intent();
                returnIntent.putExtra("zonaIdEliminada",id);
                setResult(RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error eliminando la zona: " + error);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        String coordenadas = getIntent().getExtras().getString("coordenadas");
        double latitudCast = 0.0;
        double longitudCast = 0.0;
        if (coordenadas != null) {
            String[] parts = coordenadas.split(",");

            // Convierte cada parte en un double
            try {
                double latitude = Double.parseDouble(parts[0].trim());
                double longitude = Double.parseDouble(parts[1].trim());
                latitudCast = latitude;
                longitudCast = longitude;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        LatLng location = new LatLng(latitudCast, longitudCast);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudCast, longitudCast))
                .title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setTrafficEnabled(true);
    }
}
