package com.example.smartspacesproject1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    //accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener accelerometerListener;


    //location

    private LocationManager locationManager;

    private LocationListener locationListener;

    private LatLng currentLatLang = null;
    private LatLng lastLatLang=null;
    //startMeasurements
    private boolean startMeasurements = false;
    private Button mButton;



    private TextView zText, windowsize, thresh;



    //containers
    private Vector<Double> zValues = new Vector<>();

    private Vector<Marker> markers = new Vector<>();




    //constants
    private final int ZOOM = 15;
    private int WINDOW_SIZE = 5;
    private double THRESHHOLD = 9.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();

        mButton = (Button) findViewById(R.id.button);

        mButton.setEnabled(false);

        zText = (TextView) findViewById(R.id.ztext);

        windowsize = (TextView) findViewById(R.id.windowsize);

        thresh = (TextView) findViewById((R.id.thresh)) ;


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accelerometerListener = createAccelerometerListener();

        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

    }




    private SensorEventListener createAccelerometerListener(){

        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if(startMeasurements) {

                    Double z= Double.valueOf(sensorEvent.values[2]);
                    zValues.add(z);


                }
                zText.setText(""+sensorEvent.values[2]);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }


    private LocationListener createLocationListener(){

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if(currentLatLang!=null)
                    lastLatLang = new LatLng(currentLatLang.latitude, currentLatLang.longitude);

                currentLatLang = new LatLng(location.getLatitude(),location.getLongitude());

                Vector<Double> newdata;

                newdata = averageAndFindAnomalies.movingAverage(WINDOW_SIZE, zValues);

                if(averageAndFindAnomalies.checkForAnomalies(THRESHHOLD, newdata))
                {
                    addMarkerOnLocation(BitmapDescriptorFactory.HUE_MAGENTA,
                            new LatLng((currentLatLang.latitude+lastLatLang.latitude)/2,(currentLatLang.longitude+lastLatLang.longitude)/2));
                }

                zValues.clear();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {



            }

            @Override
            public void onProviderEnabled(String s) {



            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }



    public void StartMeasurements(View v)
    {
        startMeasurements=true;
    }


    private void getLocationPermission(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[1]) == PackageManager.PERMISSION_GRANTED){

                //initialize

                initMap();

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationListener = createLocationListener();

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        1234);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    1234);
        }
    }


    @SuppressLint("MissingPermission")
    private void initMap(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    private void moveCamera(LatLng latLng, float zoom){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    public void CurrentLocation(View v)
    {
        if(currentLatLang!=null) {

            LatLng current = currentLatLang;

            moveCamera(currentLatLang, ZOOM);

            //addMarkerOnLocation(BitmapDescriptorFactory.HUE_AZURE, current);

            mButton.setEnabled(true);

        }
        else
        {
            Toast toast = Toast.makeText(this,"not connected!", Toast.LENGTH_SHORT );

            toast.show();
        }
    }


    private Marker addMarkerOnLocation(float color, LatLng current)
    {
        return mMap.addMarker(new MarkerOptions().position(current)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    public void UpWindow(View v)
    {
        WINDOW_SIZE+=1;
        windowsize.setText(""+WINDOW_SIZE);
    }

    public void DownWindow(View v)
    {
        WINDOW_SIZE-=1;
        windowsize.setText(""+WINDOW_SIZE);
    }

    public void DownThresh(View v)
    {
        THRESHHOLD-=0.5;
        thresh.setText(""+THRESHHOLD);
    }

    public void UpThresh(View v)
    {
        THRESHHOLD+=0.5;
        thresh.setText(""+THRESHHOLD);
    }
}





