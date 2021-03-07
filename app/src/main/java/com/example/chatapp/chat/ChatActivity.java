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

import com.example.chatapp.MapsActivity;
import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.example.chatapp.Tabs;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.google.firebase.auth.FirebaseAuth.*;

public class ChatActivity extends AppCompatActivity// implements View.OnClickListener
{
private ImageView iconSend;
private EditText msgText;
private ListView msgList;

private double [] currLocation = new double [2];

   private Tabs tbs;
   private TabLayout tabs;
  private  FirebaseListAdapter<ChatMessages> adapter;


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

        tbs = new Tabs ( findViewById(R.id.tabBarGroupChat), this);
        tabs = tbs.addTabs(1);

       // iconAttach = findViewById(R.id.iconAttachment); //will be for attaching pics
        iconSend = findViewById(R.id.iconSend); //send arrow
        msgText = findViewById(R.id.messageInputTxt); //message you want to send
        msgList = findViewById(R.id.listViewMsg); //chat msg list



        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        iconSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ChatMessages msg = new ChatMessages(msgText.getText().toString(), userInformation[1]
                        ,userInformation[0], userInformation[2],  System.currentTimeMillis());

                FirebaseDatabase.getInstance()
                        .getReference("Group_Msgs")//.child(userId)
                        .push()
                        .setValue(msg);

                msgText.setText("");

            }
        });

        FirebaseListOptions<ChatMessages> options =new FirebaseListOptions.Builder<ChatMessages>().setQuery(FirebaseDatabase.getInstance()
                           .getReference("Group_Msgs"), ChatMessages.class)
                            .setLayout(R.layout.fragment_chat_message)
                            .build();
         adapter = new FirebaseListAdapter<ChatMessages>(options) {

        @Override
        protected void populateView(@NonNull View v, @NonNull ChatMessages model, int position)
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

        if (adapter != null)
         msgList.setAdapter(adapter); //sets the messages to the chat

        //TODO:Set up remove it when done
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
                        startActivity(new Intent(ChatActivity.this, MapsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatActivity.this, ChatActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatActivity.this, ChatRegionalActivity.class));
                        break;
                    }
                    case 3:{break;}
                    case 4: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
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
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}






