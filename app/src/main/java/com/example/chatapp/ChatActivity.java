package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
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
private    GeoLocation currLocation;
//private GeoQuery query;
//private DatabaseReference mRootRef;
//private FirebaseAuth firebaseAuth;
//private String currUsrId, chatUsrId;
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

        //makes chat that good purp
        tabLayout.getTabAt(1).select();

        iconAttach = findViewById(R.id.iconAttachment); //will be for attaching pics
        iconSend = findViewById(R.id.iconSend); //send arrow
        msgText = findViewById(R.id.messageInputTxt); //message you want to send
        msgList = findViewById(R.id.listViewMsg); //chat msg list

        ////////////////////////////////////////////////////////////////////////////
        /// models.remove(position);
        // LatLng currentInRange;
        //  TextView msgTxt;
        // TextView usrId;
        // TextView msgTime;

        //    DatabaseReference db = FirebaseDatabase.getInstance()
        //          .getReference("UserInfo");
        ArrayList<String> userIDs = new ArrayList<>();
        DatabaseReference refGPS = FirebaseDatabase.getInstance().getReference("GPS_COOR");

        //setup geofire (it is to fid w/in given location)
        GeoFire geoFire = new GeoFire(refGPS);
        //(getInstance().getCurrentUser().getUid() LatLng currentInRange


        //get userId
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        geoFire.getLocation(uId, new LocationCallback()
        {
            @Override
            //gets user coordinates then finds all users in given range
            public void onLocationResult(String key, GeoLocation location)
            {
                //all users in a given range
                if (location != null)
                {
                    currLocation = location;
                    //  GeoQuery query = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 51);

                    //   query.addGeoQueryEventListener(new GeoQueryEventListener() {

                    //        @Override
                    //       public void onKeyEntered(String key, GeoLocation loc) //key is userID
                    //        {
                    //            userIDs.add(key);
                    //        }

                    //       @Override
                    //      public void onKeyExited(String key) {
                    //       }

                    //        @Override
                    //        public void onKeyMoved(String key, GeoLocation location) {
                    //       }

                    //        @Override
                       //     public void onGeoQueryReady() {
                    //        }

                    //       @Override
                    //       public void onGeoQueryError(DatabaseError error) {
                    //           System.out.println("DATA BASE ERROR   " + error.getMessage());
                    //       }
                    //   });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        iconSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

               String userId =  getInstance()
                        .getCurrentUser()
                        .getUid();

                final String[] nameStr = new String[1];

               Query name = FirebaseDatabase.getInstance().getReference("UserInfo");

               name.addListenerForSingleValueEvent(new ValueEventListener()
               {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                   {
                       for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                       {
                        String temp = postSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue().toString();


                        System.out.println(temp);
                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {}
               });

                FirebaseDatabase.getInstance()
                        .getReference("Group_Msgs")
                        .push()
                        .setValue(new ChatMessages(msgText.getText().toString(),userId
                                , nameStr[0], currLocation));

                msgText.setText("");
            }
        });



///////////////////////////////////////////////////////////////////////////////////

        //fbListAdapter = new FirebaseListAdapter<ChatMessages>(ChatActivity.this,
        //        ChatMessages.class, R.layout.fragment_chat_message, FirebaseDatabase.getInstance()
        //        .getReference("Group_Msgs"))

//FirebaseListOptions <ChatMessages> options;// = new FirebaseListOptions.Builder<ChatMessages>().setQuery("Group_Msgs", ChatMessages.class)
        ;



        DatabaseReference refGroupMsg =   FirebaseDatabase.getInstance().getReference("Group_Msgs");

















        fbListAdapter = new FirebaseListAdapter<ChatMessages>(ChatActivity.this,
                ChatMessages.class, R.layout.fragment_chat_message, FirebaseDatabase.getInstance()
                .getReference("Group_Msgs"))
        {

            @Override
            protected void populateView(View v, ChatMessages model, int position)
            {}


        };

        //LatLng currentInRange = new LatLng(loc.latitude, loc.longitude);
        //  TextView msgTxt = v.findViewById(R.id.msgText);
        //  TextView usrId = v.findViewById(R.id.msguserID);
        //   TextView msgTime = v.findViewById(R.id.msgTime);

        // String date = String.format("MM/dd/yyyy H:mm", model.getMsgTime());


        // long fifteenMin = model.getMsgTime() + 900000;

//key.equals(model.getMsgUsr())&&  (fifteenMin <= System.currentTimeMillis()) check if the user is in the area

        //if (true) {

        //   TextView msgTxt = v.findViewById(R.id.msgText);
        //   TextView usrId = v.findViewById(R.id.msguserID);
        //   TextView msgTime = v.findViewById(R.id.msgTime);
//
        //   msgTxt.setText(model.getMsgText());
        //   usrId.setText(model.getMsgUsr());

        //set message time
        //   SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        //   msgTime.setText(formatter.format(model.getMsgTime()));

        //   }


        //fbListAdapter.forEach(function(){});
      //  DatabaseReference item = fbListAdapter.getRef(1);
      //  item.removeValue();

        if (fbListAdapter != null)
        msgList.setAdapter(fbListAdapter); //sets the messages to the chat

       // msgList.removeViewAt(1);


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
                    case 1: { break; }
                    case 2:{}
                    case 3:{}
                    case 4:
                    {
                     //   startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
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
