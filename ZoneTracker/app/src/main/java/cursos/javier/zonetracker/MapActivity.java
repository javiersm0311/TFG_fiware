package cursos.javier.zonetracker;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class MapActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        SensorEventListener,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 50;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long mShakeTime = 0;
    private LocationManager locManager;
    private LocationListener locListener;
    DecimalFormat formato = new DecimalFormat("0.0");
    private float lastY = 0;
    private Integer num_cambios = 0;
    private float latitud;
    private float longitud;
    private static final long TIMER_DURATION = 20000; // Duración del temporizador en milisegundos

    private CountDownTimer countDownTimer;
    private CountDownTimer countDownTimer2;
    private GoogleMap map;
    private JSONObject listaZonas;

    private Button atrasButton;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String valor = getIntent().getExtras().getString("unidad");
        OrionClient orionClient = new OrionClient(this);
        orionClient.createEntity(valor);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startTimer2();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        atrasButton = (Button) findViewById(R.id.atrasButton);
        atrasButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onBackPressed();

            }
        });
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // Nada que hacer
    }

    private void detectShake(SensorEvent event) {
        long now = System.currentTimeMillis();
        if ((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;
            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD) {
                if(num_cambios == 0) {
                    num_cambios += 1;
                    startTimer1();
                }
                else{
                    num_cambios +=1;
                    if(num_cambios == 6) {
                        detectLocalization();
                        num_cambios = 0;
                        showAlertDialog(this, "¡Precaucion!", "Zona en mal estado");
                    }
                }
            }
        }

    }

    private void startTimer1() {
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Este método se llama cada segundo mientras el temporizador esté en marcha
            }
            @Override
            public void onFinish() {
                // Este método se llama cuando el temporizador ha expirado
                num_cambios = 0;
            }
        };

        countDownTimer.start();
    }

    private void startTimer2() {
        countDownTimer2 = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                detectLocalization();
                LatLng localizacionActual = new LatLng(latitud,longitud);
                map.animateCamera(CameraUpdateFactory.newLatLng(localizacionActual));
                startTimer2();
            }
        };
        countDownTimer2.start();
    }


    private void detectLocalization() {
        String valor = getIntent().getExtras().getString("unidad");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String [] { android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                    1000
            );
            return;
        }
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitud = (float) loc.getLatitude();
        longitud = (float) loc.getLongitude();

        if(num_cambios == 6){
            OrionClient orionClient = new OrionClient(this);
            orionClient.updateEntityAttribute((float) loc.getLatitude(),(float) loc.getLongitude(), valor);
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitud, longitud)));
        }
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }
        };
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0, locListener);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor misensor = event.sensor;
        if (misensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float currentY = event.values[1];
            if (Math.abs(currentY - lastY) > 0.5f) { //umbral de cambio
                detectShake(event);
            }
            lastY = currentY;

        }
    }
    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false); // Esto previene que el usuario cierre el diálogo tocando fuera de él

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {

                if(alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss(); // Cerrar el AlertDialog
                }
            }
        }.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(20.5937, 78.9629))
                .title("Marker"));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setTrafficEnabled(true);

        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();

    }


    @SuppressLint("MissingPermission")
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }
    }

    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}

