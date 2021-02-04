package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentLocation;
    private View progressBar;

    @Override
    //called automatically once permissions were accepted or declined
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            //must double check for permission
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            } else //user hates gps. throws error saying user cannot use this app w/o gps.
            {
                Toast.makeText(MapsActivity.this, "GPS is required for chat. Please enable and log in again.",Toast.LENGTH_SHORT).show();
                Intent toMainActivity = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(toMainActivity);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        progressBar = findViewById(R.id.progressBar);
        super.onCreate(savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);

        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location)
            {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude()); //TODO: Set location to database
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Dis you")); //TODO: Change title to the user's username

                mMap.addCircle(new CircleOptions()
                                .center(currentLocation)
                                .radius(3000 )
                                .strokeWidth(0f)
                                .fillColor(0x550000FF));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12.5f));
                progressBar.setVisibility(View.GONE);

                //stop it from regenerating location so location is only found once.
                locationManager.removeUpdates(this);
            }
        };

            //if no permission ask for permission, annoyingly. like keep asking you like a younger sibling
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else //permission granted CONGRATS. update user location.
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //TODO: Retrive previous location of user from database, for initial pan.
        mMap = googleMap;

    }
}