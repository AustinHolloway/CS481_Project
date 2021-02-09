package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity// implements View.OnClickListener
{
private ImageView iconAttach, iconSend;
private TextView msgText;
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

        //makes chat that good purple
        tabLayout.getTabAt(1).select();

        iconAttach = findViewById(R.id.iconAttachment);
        iconSend = findViewById(R.id.iconSend);
        msgText = findViewById(R.id.messageInputTxt);

      //  iconSend.setOnClickListener(this);
      //  firebaseAuth = FirebaseAuth.getInstance();
    //    mRootRef = FirebaseDatabase.getInstance().getReference();

    //    currUsrId = firebaseAuth.getCurrentUser().getUid();



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
/*
    //pushId is the id for the message
    public void sendMessage(String message, String messageType, String idToPush)
    {
try{
    if (!message.equals("")){

        HashMap hashMapMessage = new HashMap();
        hashMapMessage.put("MessageID", idToPush);
        hashMapMessage.put("Message", message);
        hashMapMessage.put("MessageType", messageType);
        //might need to timestamp

        String currUsrRf = "Conversation" + "/" + currUsrId + "/" + chatUsrId;
        String chatUsrRf = "Conversation" + "/" + chatUsrId + "/" + currUsrId;

        HashMap msgUsrMap = new HashMap();
        msgUsrMap.put(currUsrRf + "/" + idToPush, hashMapMessage);
        msgUsrMap.put(chatUsrRf + "/" + idToPush, hashMapMessage);

        msgText.setText("...");

        mRootRef.updateChildren(msgUsrMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null){
                    Toast.makeText(ChatActivity.this, "Database error. Resend message.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ChatActivity.this, "Message has been sent.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}catch(Exception e){
    Toast.makeText(ChatActivity.this, "Database error. Resend message.", Toast.LENGTH_LONG).show();
}


    }


    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.iconSend:{
        //TODO: add Util.connection avalible
        DatabaseReference usrMsgPsh = mRootRef.child("Conversation").child(currUsrId).child(chatUsrId).push();
        String pshId = usrMsgPsh.getKey();
        sendMessage(msgText.toString().trim(), "text", pshId);
    }

}
    }


}*/