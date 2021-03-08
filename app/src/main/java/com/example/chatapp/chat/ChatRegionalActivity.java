package com.example.chatapp.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chatapp.FindFriendsActivity;
import com.example.chatapp.MapsActivity;
import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.example.chatapp.Tabs;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class ChatRegionalActivity extends AppCompatActivity {
    private Tabs tbs;
    private TabLayout tabs;
    private ImageView iconSend;
    private EditText msgText;
    private ListView msgList;
    private FirebaseListAdapter<RegionalChatMessages> fbListAdapter;
    private int lng, lat;
    private String userId;
    private String combined;
    private FirebaseListAdapter<RegionalChatMessages> adapter;
    private FirebaseListOptions<RegionalChatMessages> options;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_regional);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        tbs = new Tabs ((TabLayout) findViewById(R.id.tabBarChatRegional), this);
        tabs = tbs.addTabs(2);

        iconSend = findViewById(R.id.iconSendRegional); //send arrow
        msgText = findViewById(R.id.messageInputTxtRegional); //message you want to send
        msgList = findViewById(R.id.listViewMsgRegional); //chat msg list

        DatabaseReference usrInfo  = FirebaseDatabase.getInstance().getReference("UserInfo");
        DatabaseReference usrLocation = FirebaseDatabase.getInstance().getReference("GPS_COOR");

        //[0] name [1]username [2] userId
        final String[] userInformation = new String[3];
        userId =  getInstance().getCurrentUser().getUid();

        //get user info
        usrInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInformation[0] = snapshot.child(userId).child("name").getValue().toString();
                userInformation[1] = snapshot.child(userId).child("username").getValue().toString();
                userInformation[2] = userId;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        double [] latLong = new double[2];

        //find the user
        usrLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latLong[0]= (double) snapshot.child(userId).child("l").child("0").getValue();
                latLong[1]=(double)snapshot.child(userId).child("l").child("1").getValue();

                lat = (int)latLong[0];
                lng = (int)latLong[1];

                String la = Integer.toString(lat);
                String lg = Integer.toString(lng);
                combined = la+lg;

                options = new FirebaseListOptions.Builder<RegionalChatMessages>().setQuery(FirebaseDatabase.getInstance()
                        .getReference("Regional_Chat").child(combined), RegionalChatMessages.class)
                        .setLayout(R.layout.fragment_chat_message)
                        .build();

                addfbListAdapter();
                adapter.startListening();

                if (adapter != null)
                    msgList.setAdapter(adapter); //sets the messages to the chat
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        setIconListener(userInformation, latLong);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int tabPos = tabs.getSelectedTabPosition();
                switch (tabPos)
                {
                    case 0: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatRegionalActivity.this, MapsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatRegionalActivity.this, ChatActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatRegionalActivity.this, ChatRegionalActivity.class));
                        break;
                    }
                    case 3:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatRegionalActivity.this, FindFriendsActivity.class));
                        break;
                    }
                    case 4: {
                           tabs.clearOnTabSelectedListeners();
                           startActivity(new Intent(ChatRegionalActivity.this, ProfileActivity.class));
                           break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void setIconListener(String[] userInformation, double [] latLong){

        iconSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                RegionalChatMessages msg = new RegionalChatMessages(msgText.getText().toString(), userInformation[1]
                        ,userInformation[0], userInformation[2],  latLong, System.currentTimeMillis());

                   FirebaseDatabase.getInstance()
                             .getReference("Regional_Chat").child(combined)//.child("Longitude").child(Integer.toString(lng))
                             .push()
                            .setValue(msg);

                msgText.setText("");

            }
        });
    }

public void addfbListAdapter() {

    adapter = new FirebaseListAdapter<RegionalChatMessages>(options) {

        @Override
        protected void populateView(@NonNull View v, @NonNull RegionalChatMessages model, int position) {

            TextView msgTxt = v.findViewById(R.id.msgText);
            TextView usrName = v.findViewById(R.id.msguserID);
            TextView msgTime = v.findViewById(R.id.msgTime);

            if (model.getUserId().equals(userId)) {
                usrName.setTextColor(Color.BLUE);
            }

            Date date = new Date(model.getMsgTime());
            msgTxt.setText(model.getMessageText());
            usrName.setText(model.getUserName());
            msgTime.setText(date.toString());
        }

    };

}
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}