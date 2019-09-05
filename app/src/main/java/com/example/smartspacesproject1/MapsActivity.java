package com.example.smartspacesproject1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

    private Vector<Double> allValues = new Vector<>();




    //constants
    private final int ZOOM = 15;
    private int WINDOW_SIZE = 5;
    private double SMALL_THRESHOLD = 9.0;
    private double BIG_THRESHOLD = 13.0;
    private static final Double CLUSTER_DISTANCE = 0.0001;


    //graph
    GraphView graph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();

        mButton = (Button) findViewById(R.id.button);

        mButton.setEnabled(false);

        zText = (TextView) findViewById(R.id.ztext);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        accelerometerListener = createAccelerometerListener();

        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        graph = (GraphView) findViewById(R.id.graph);




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

                if(currentLatLang!=null && lastLatLang!=null && !zValues.isEmpty()) {
                    Vector<Double> newdata;

                    newdata = averageAndFindAnomalies.movingAverage(WINDOW_SIZE, zValues);

                    for (int i = 0; i < newdata.size(); ++i) {
                        allValues.add(newdata.elementAt(i));
                    }

                    anomalyTypePosition anomalyTypePosition = averageAndFindAnomalies.getAnomalyType(SMALL_THRESHOLD, BIG_THRESHOLD, newdata);

                    LatLng marker = findingThePoint.find(lastLatLang, currentLatLang, newdata.size(), anomalyTypePosition.getPosition());

                    setMarkers(marker, anomalyTypePosition.getType());
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
        graph.removeAllSeries();
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

            mButton.setEnabled(true);

        }
        else
        {
            Toast toast = Toast.makeText(this,"not connected!", Toast.LENGTH_SHORT );

            toast.show();
        }
    }

    //check if this marker is too close to another marker
    private Marker addMarkerOnLocation(float color, LatLng current)
    {
        //assume marker is not too close
        boolean tooClose = false;
        int replacing = -1;
        //for each existing marker
        for(int i = 0; i < markers.size(); i++)
        {
            //get LatLng of marker being checked
            LatLng checking = markers.get(i).getPosition();

            //get the pythagorean distance between the marker and the possible new marker
            Double distance = Math.sqrt(Math.pow((checking.latitude - currentLatLang.latitude),2) +
                    Math.pow((checking.longitude - currentLatLang.longitude),2));

            //if the new marker is too close to the existing one
            if(distance < CLUSTER_DISTANCE)
            {
                if(color == BitmapDescriptorFactory.HUE_YELLOW || markers.get(i).getTitle().equals(""+BitmapDescriptorFactory.HUE_RED))
                {
                    //this marker is too close to be placed
                    tooClose = true;
                    replacing = -1;
                    break;
                }
                else {
                    replacing = i;
                }
            }
        }
        //if this marker is not too close to any markers
        if(!tooClose)
        {
            if(replacing > -1)
            {
                markers.get(replacing).remove();
            }
            //place the marker
            return mMap.addMarker(new MarkerOptions().position(current)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)).title(""+color));
        }
        //don't place any marker
        return null;
    }




    public void ShowGraph(View v)
    {
        startMeasurements = false;
        LineGraphSeries <DataPoint> lineGraphSeries = new LineGraphSeries<>();
        for(int i = 0; i<allValues.size(); ++i)
        {
            lineGraphSeries.appendData(new DataPoint(i,allValues.elementAt(i).doubleValue()),false, allValues.size());
        }
        allValues.clear();
        graph.addSeries(lineGraphSeries);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);


    }

    private void setMarkers(LatLng position, int type)
    {


        if(type==0)
        {
            return;
        }
        else if(type==1)
        {   //small anomaly
            markers.add(addMarkerOnLocation(BitmapDescriptorFactory.HUE_YELLOW,position));

        }
        else if(type==2)
        {
            //big anomaly
            markers.add(addMarkerOnLocation(BitmapDescriptorFactory.HUE_RED,position));

        }


        if(markers.lastElement()==null)
        {
            //data clustering
            markers.remove(markers.size()-1);
        }
    }



}





