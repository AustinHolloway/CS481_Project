package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.google.firebase.auth.FirebaseAuth.*;

public class ChatActivity extends AppCompatActivity// implements View.OnClickListener
{
private ImageView iconAttach, iconSend;
private EditText msgText;
private ListView msgList;
private FirebaseListAdapter<ChatMessages> fbListAdapter;
private double [] currLocation = new double [2];
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentLocation;
    GeoQuery query;

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        tabLayout = (TabLayout) findViewById(R.id.tabBar);

        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Alerts"));
        tabLayout.addTab(tabLayout.newTab().setText("Find"));
        tabLayout.addTab(tabLayout.newTab().setText("About"));

        //makes chat that good purple
        tabLayout.getTabAt(1).select();

        iconAttach = findViewById(R.id.iconAttachment); //will be for attaching pics
        iconSend = findViewById(R.id.iconSend); //send arrow
        msgText = findViewById(R.id.messageInputTxt); //message you want to send
        msgList = findViewById(R.id.listViewMsg); //chat msg list

        ///////////////////////////////////////////////////////////////////////////////////

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference refGPS = FirebaseDatabase.getInstance().getReference("GPS_COOR");

        //setup geofire (it is to fid w/in given location)
        GeoFire geoFire = new GeoFire(refGPS);

        final String[] geoKey = {""};


       geoFire.getLocation(uId, new LocationCallback()
        {
           @Override

            public void onLocationResult(String key, GeoLocation location)
            {
                //all users in a given range
               if (location != null)

                {
                    geoKey[0] = key;
                   currLocation[0] = location.latitude;
                   currLocation[1] = location.longitude;
                    System.out.println("LOCATION NOT NULL");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        DatabaseReference usrInfo  = FirebaseDatabase.getInstance().getReference("UserInfo");

        //[0] name [1]username [2] userId
        final String[] userInformation = new String[3];
        String userId =  getInstance().getCurrentUser().getUid();

        usrInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInformation[0] = snapshot.child(userId).child("name").getValue().toString();
                userInformation[1] = snapshot.child(userId).child("username").getValue().toString();
                userInformation[2] = uId;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        iconSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ChatMessages msg = new ChatMessages(msgText.getText().toString(), userInformation[1]
                        ,userInformation[0], userInformation[2], currLocation, System.currentTimeMillis());

                FirebaseDatabase.getInstance()
                        .getReference("Group_Msgs")//.child(userId)
                        .push()
                        .setValue(msg);

                msgText.setText("");

            }
        });

        fbListAdapter = new FirebaseListAdapter<ChatMessages>(ChatActivity.this,
                ChatMessages.class, R.layout.fragment_chat_message, FirebaseDatabase.getInstance()
               .getReference("Group_Msgs"))
        {

            @Override
            protected void populateView(View v, ChatMessages model, int position)
            {

                TextView msgTxt = v.findViewById(R.id.msgText);
                TextView usrName = v.findViewById(R.id.msguserID);
                TextView msgTime = v.findViewById(R.id.msgTime);


                if (model.getUserId().equals(uId))
                {
                    usrName.setTextColor(Color.BLUE);
                }

               Date date = new Date( model.getMsgTime());
                msgTxt.setText(model.getMessageText());
                usrName.setText(model.getUserName());
                msgTime.setText(date.toString());
            }
        };

        if (fbListAdapter != null)
        msgList.setAdapter(fbListAdapter); //sets the messages to the chat

        //TODO:Set up remove it when done
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int tabPos = tabLayout.getSelectedTabPosition();
                switch (tabPos)
                {
                    case 0:
                     {
                         tabLayout.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatActivity.this, MapsActivity.class));
                    }
                    case 1:{break;}
                    case 2:{break;}
                    case 3:{break;}
                    case 4:
                    {
                        tabLayout.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}
/* This is trash buy might be useful


 //geoFire.setLocation(uId, new GeoLocation(currLocation[0],currLocation[1]));

//////////////////////////////////////////////////////////GET LOCATION AGAIN
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener()
                {
@Override
public void onLocationChanged(@NonNull Location location) {
        // currentLocation = new LatLng(location.getLatitude(), location.getLongitude()); //TODO: Set location to database
        currLocation[0] = location.getLatitude();
        currLocation[1] = location.getLongitude();

        //stop it from regenerating location so location is only found once.
        locationManager.removeUpdates(this);
        //       geoFire.setLocation(uId, new GeoLocation(currLocation[0],currLocation[1]));
        //       query = geoFire.queryAtLocation(new GeoLocation(currLocation[0],currLocation[1]), 51);
        }};

/*

          //save user id's of thoes within range
          ArrayList<String> userIDs = new ArrayList<>();
        GeoQuery query = geoFire.queryAtLocation(new GeoLocation(currLocation[0],currLocation[1]), 51);
        System.out.println("GEO KEYyyyyyyyyyyyy");
           query.addGeoQueryEventListener(new GeoQueryEventListener() {

                @Override
               public void onKeyEntered(String key, GeoLocation loc) //key is userID
                {

                    System.out.println(key+"GEO KEYyyyyyyyyyyyy22222222222");
                    userIDs.add(key);

                }

               @Override
              public void onKeyExited(String key) {}

                @Override
                public void onKeyMoved(String key, GeoLocation location) {}

                @Override
             public void onGeoQueryReady() {}

               @Override
               public void onGeoQueryError(DatabaseError error) {
                   System.out.println("DATA BASE ERROR   " + error.getMessage());
               }
           });


*/






