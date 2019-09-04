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

    //startMeasurements
    private boolean startMeasurements = false;
    private Button mButton;


    private Vector<Double> zValues = new Vector<>();


    private Location lastLocation = null;


    private boolean mLocationPermissionsGranted = false;



    private int zoom = 15;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        getLocationPermission();


        mButton = (Button) findViewById(R.id.button);
        mButton.setEnabled(false);



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;







    }




    private SensorEventListener createAccelerometerListener(){

        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if(startMeasurements==true) {

                    Double z= Double.valueOf(sensorEvent.values[2]);
                    zValues.add(z);
                }

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


                currentLatLang = new LatLng(location.getLatitude(),location.getLongitude());



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
                mLocationPermissionsGranted = true;
                initMap();
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                locationListener = createLocationListener();



                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

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


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        accelerometerListener = createAccelerometerListener();




    }


    private void moveCamera(LatLng latLng, float zoom){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    public void CurrentLocation(View v)
    {
        if(currentLatLang!=null) {
            LatLng current = currentLatLang;
            moveCamera(currentLatLang, zoom);
            mMap.addMarker(new MarkerOptions().position(current)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            mButton.setEnabled(true);

        }
        else
        {
            Toast.makeText(this,"not connected!", Toast.LENGTH_SHORT );
        }
    }









}





