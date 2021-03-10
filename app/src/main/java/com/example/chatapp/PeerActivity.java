package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class PeerActivity extends AppCompatActivity {

    private String receiverUserId, senderUserId, current_state;

    private CircularImageView peerProfileImage;
    private TextView peerProfileUsername, peerProfileName;
    private Button sendMessageRequestButton, declineRequestButton;

    private DatabaseReference peerRef, chatRequestRef, contactsRef;
    private FirebaseAuth mAuth;

    TabLayout tabs;
    TabLayout tabLayout;
    PeersTab tbsa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        tbsa = new PeersTab ( findViewById(R.id.tabBarPeers), this);
        tabs = tbsa.addTabs(0);

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
                        startActivity(new Intent(PeerActivity.this, FindFriendsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(PeerActivity.this, ContactsActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(PeerActivity.this, RequestsActivity.class));
                        break;
                    }
                    case 3: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(PeerActivity.this, ProfileActivity.class));
                        break;
                    }


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        mAuth = FirebaseAuth.getInstance();
        peerRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = mAuth.getCurrentUser().getUid();

        peerProfileImage = (CircularImageView) findViewById(R.id.visit_peer_image);
        peerProfileUsername = (TextView) findViewById(R.id.visit_peer_username);
        peerProfileName = (TextView) findViewById(R.id.visit_peer_name);
        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        declineRequestButton = (Button) findViewById(R.id.decline_message_request_button);
        current_state = "new";

        RetrievePeerInfo();
    }

    private void RetrievePeerInfo() {
        peerRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image"))) {
                    String peerImage = snapshot.child("image").getValue().toString();
                    String peerUsername = snapshot.child("username").getValue().toString();
                    String peerName = snapshot.child("name").getValue().toString();

                    peerProfileUsername.setText(peerUsername);
                    peerProfileName.setText(peerName);

                    manageChatRequests();
                }
                else {
                    String key = snapshot.getKey();
                    String peerUsername = snapshot.child("username").getValue().toString();
                    String peerName = snapshot.child("name").getValue().toString();

                    peerProfileUsername.setText(peerUsername);
                    peerProfileName.setText(peerName);
                    //image
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference picsChild = storage.getReference().child(key+".jpg");
                    picsChild.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            peerProfileImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Drawable draw = getResources().getDrawable(R.drawable.default_picture);
                            Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
                            peerProfileImage.setImageBitmap(bitmap);
                        }
                    });


                    manageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void manageChatRequests() {
        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserId)) {
                            String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")) {
                                current_state = "request_sent";
                                sendMessageRequestButton.setText("Cancel Request");
                            }
                            else if (request_type.equals("received")) {
                                current_state = "request_received";
                                sendMessageRequestButton.setText("Accept Request");

                                declineRequestButton.setVisibility(View.VISIBLE);
                                declineRequestButton.setEnabled(true);
                                declineRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelChatRequest();
                                    }
                                });

                            }
                        }
                        else {
                            contactsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(receiverUserId)) {
                                                current_state = "friends";
                                                sendMessageRequestButton.setText("Remove Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (!senderUserId.equals(receiverUserId)) {
            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);

                    if (current_state.equals("new")) {
                        sendChatRequest();
                    }
                    if (current_state.equals("request_sent")) {
                        cancelChatRequest();
                    }
                    if (current_state.equals("request_received")) {
                        acceptChatRequest();
                    }
                    if (current_state.equals("friends")) {
                        removeContact();
                    }
                }
            });
        }
        else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void removeContact() {
        contactsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest() {
        contactsRef.child(senderUserId).child(receiverUserId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(receiverUserId).child(senderUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    sendMessageRequestButton.setText("Remove Contact");

                                                                                    declineRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "new";
                                                sendMessageRequestButton.setText("Send Message");

                                                declineRequestButton.setVisibility(View.INVISIBLE);
                                                declineRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageRequestButton.setEnabled(true);
                                                current_state = "request_sent";
                                                sendMessageRequestButton.setText("Cancel Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}