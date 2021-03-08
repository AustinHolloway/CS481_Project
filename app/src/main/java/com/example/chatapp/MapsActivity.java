package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatapp.chat.ChatActivity;
import com.example.chatapp.chat.ChatRegionalActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
/////////////////////////////////////////////////////////////////////////////////////////////////
                                //ref to the storage bucket
                                FirebaseStorage storage = FirebaseStorage.getInstance();

                                StorageReference picsChild = storage.getReference().child(key+".jpg");

                                //String name = getUserName(key);



                                picsChild.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
                                {
                                    @Override
                                    public void onSuccess(byte[] bytes)
                                    {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        //ivProfile.setImageBitmap(bitmap);
                                       // doesHavePic = true;
                                       // BitmapFromUserPic(bitmap);
                                        getUserNameAndSetNameAndMarker(key,bitmap, currentInRange);
//System.out.println(getUserName(key)+"                    NAME");
//waddMarkers(bitmap, getUserName(key), currentInRange);
//                                        mMap.addMarker(new MarkerOptions().position(currentInRange).icon(BitmapFromUserPic(bitmap)).title(name).anchor(0,0));
                                    }
                                }).addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Drawable draw = getResources().getDrawable(R.drawable.default_picture);
                                        Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
                                      //  ivProfile.setImageResource(R.drawable.default_picture);
                                      //  doesHavePic = false;
                                        getUserNameAndSetNameAndMarker(key, bitmap, currentInRange);
                                       // mMap.addMarker(new MarkerOptions().position(currentInRange).icon(BitmapNoPic()).anchor(0,0));
                                    }
                                });
//////////////////////////////////////////////////////////////////////////////////////////////////////


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

    private BitmapDescriptor BitmapFromUserPic(Bitmap bitmap) {
        // below line is use to generate a drawable.
       // Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        drawable.setBounds(0, 0, 100,100 );
        drawable.setCircular(true);

        Bitmap bitmap2 = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap2);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap2);
    }

    private BitmapDescriptor BitmapNoPic(Bitmap bitmap) {
        // below line is use to generate a drawable.
        // Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
//        Drawable draw = getResources().getDrawable(R.drawable.default_picture);
//        Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        drawable.setBounds(0, 0, 100, 100);
        drawable.setCircular(true);

        Bitmap bitmap2 = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap2);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap2);
    }

    //TODO////////////////User name is not returning

    private String getUserNameAndSetNameAndMarker(String key,Bitmap bitmap,  LatLng currentInRange){
        DatabaseReference refName = FirebaseDatabase.getInstance().getReference("UserInfo");
//String str = refName.child(key).child("name").value().toString();
//System.out.println(str + "STR         RRRRRRRRRRRRRRRRRRRRRRRRR");
                                        final String[] name = new String[1];
                               refName.addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                   {

                                     //  for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                           System.out.println(key + "                       KEYYYYYYYYYYYYYYYYYYYYYYY");
                                           if (snapshot.child(key).child("name").getValue() != null) {
                                               name[0] = snapshot.child(key).child("name").getValue().toString();

                                               System.out.println("NAME SET");



                                           }
                                       if (name[0] != null)
                                       mMap.addMarker(new MarkerOptions().position(currentInRange).icon(BitmapFromUserPic(bitmap)).title(name[0]).anchor(0,0));
        else mMap.addMarker(new MarkerOptions().position(currentInRange).icon(BitmapNoPic(bitmap)).anchor(0,0));
                                     //  }
                                   }

                                  @Override
                                   public void onCancelled(@NonNull DatabaseError error) {}
                             });


        return  name[0];
    }
//    private void addMarkers(Bitmap bitmap,  String nameInput, LatLng currentInRange){
//         System.out.println("NOT NULL NAMEEEEEEEEEEEEEEEEEE"+ nameInput);
//        if (nameInput != null)
//        mMap.addMarker(new MarkerOptions().position(currentInRange).icon(BitmapFromUserPic(bitmap)).title(nameInput).anchor(0,0));
//        else mMap.addMarker(new MarkerOptions().position(currentInRange).icon(BitmapFromUserPic(bitmap)).anchor(0,0));
//    }

}
