package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentLocation;
    private View progressBar;
    Tabs tbs;
    TabLayout tabs;
    private Boolean moreUsers = true;

    private DatabaseReference mRootRef;
    private FirebaseAuth firebaseAuth;
    private String currUsrId;
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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

       tbs = new Tabs ( findViewById(R.id.tabBarMap), this);
        tabs = tbs.addTabs(0);

     //  TODO:Set up remove it when done
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
       {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int tabPos = tabs.getSelectedTabPosition();
                switch (tabPos)
                {
                    case 0:
                     {
                         tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(MapsActivity.this, MapsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(MapsActivity.this, ChatActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(MapsActivity.this, ChatRegionalActivity.class));
                        break;
                    }
                    case 3:{break;}
                    case 4:
                    {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(MapsActivity.this, ProfileActivity.class)) ;
                        break;
                    }

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(@NonNull Location location)
            {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();

                mMap.addCircle(new CircleOptions()
                                .center(currentLocation)
                                .radius(48280.3)
                                .strokeWidth(0f)
                                .fillColor(0x550000FF));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,8.6f));

                //dont load in africa
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //stop it from regenerating location so location is only found once.
                locationManager.removeUpdates(this);

                //remove spinney wheel
                progressBar.setVisibility(View.GONE);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GPS_COOR");

                //setup geofire (it is to fid w/in given location)
                GeoFire geoFire = new GeoFire(ref);

                //get userId
                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                //set location
                geoFire.setLocation(uId, new GeoLocation(currentLocation.latitude, currentLocation.longitude));

                //qwery database for radius of 51 km?
                GeoQuery query = geoFire.queryAtLocation(new GeoLocation(currentLocation.latitude, currentLocation.longitude), 56);

                        query.addGeoQueryEventListener(new GeoQueryEventListener() {

                            @Override
                            public void onKeyEntered(String key, GeoLocation loc) {

                                //key is userID
                                LatLng currentInRange = new LatLng(loc.latitude, loc.longitude);

                                mMap.addMarker(new MarkerOptions().position(currentInRange));
                            }

                            @Override
                            public void onKeyExited(String key) {}

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {}

                            @Override
                            public void onGeoQueryReady() {}

                            @Override
                            public void onGeoQueryError(DatabaseError error) {}
                        });
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