package com.rdeveloper.paviliong6.kheti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.midi.MidiOutputPort;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationChangeListener ,GoogleMap.OnMapLongClickListener {

    static final String MyPREFERENCES = "MapPrefs";

    ArrayList<LatLng> points;
    private GoogleMap mMap;
    Polyline polyline;
    LatLng change_location;
    float wid=15;
    EditText width;
    TextView area;

    Location loc=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (isNetworkAvailable()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Error")
                    .setIcon(R.drawable.nointernet_icon)
                    .setMessage("No Internet Connection.")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        width=(EditText) findViewById(R.id.map_width);
        area=(TextView) findViewById(R.id.map_out);

        points = new ArrayList<LatLng>();


    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public void onClickStart(View v){
        if(!(width.getText().toString().isEmpty())) {
            Toast.makeText(this,"Measurement Started",Toast.LENGTH_SHORT).show();
            mMap.setOnMyLocationChangeListener(MapsActivity.this);
            wid = Float.parseFloat(width.getText().toString());
            Location location = mMap.getMyLocation();
            change_location = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(change_location));
        }else{
            Toast.makeText(this,"Please Enter valid width",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        RadioGroup rgViews = (RadioGroup) findViewById(R.id.rg_views);
        rgViews.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_normal) {

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (checkedId == R.id.rb_satellite) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (checkedId == R.id.rb_terrain) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (checkedId == R.id.rb_hybrid) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });



    }



        @Override
        public void onMyLocationChange(Location location) {

            if(location!=null){
                loc=location;

                float distance_location = distFrom(location.getLatitude(),location.getLongitude(), change_location.latitude, change_location.longitude);
                polyline=mMap.addPolyline(new PolylineOptions().add(change_location, new LatLng((double)location.getLatitude(),(double)location.getLongitude())).zIndex(1).width(5).color(Color.RED).width(wid));
                float output= ((wid)*((float)distance_location/1000));
                String res=Float.toString(output);
                Log.d("TAG", res);
                area.setText(res);
            }
        }





    public void onClickStop(View v){

        Toast.makeText(this,"Measurement Stopped",Toast.LENGTH_SHORT).show();
        mMap.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public  float distFrom(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (int) (earthRadius * c);

        return (dist);
    }

    @Override
    public void onMapClick(LatLng point) {


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title("Position");
        markerOptions.snippet("Latitude:"+point.latitude+","+"Longitude:"+point.longitude);
        PolylineOptions polylineOptions = new PolylineOptions();
        // Setting the color of the polyline
        polylineOptions.color(Color.RED);
        // Setting the width of the polyline
        polylineOptions.width(3);
        // Adding the taped point to the ArrayList
        points.add(point);
        // Setting points of polyline
        polylineOptions.addAll(points);
        // Adding the polyline to the map
        mMap.addPolyline(polylineOptions);
        // Adding the marker to the map
        mMap.addMarker(markerOptions);



        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
        String result=null;
        List<Address> address=null;
        try{
            address=geocoder.getFromLocation((double)point.latitude,(double) point.longitude,3);

            result="";
            if(address.size()>0) {
                for (int i = 0; i <address.get(0).getMaxAddressLineIndex();i++){
                    result=result+address.get(0).getAddressLine(i)+"\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(MapsActivity.this, result, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();

        // Empty the array list
        points.clear();
    }












}
