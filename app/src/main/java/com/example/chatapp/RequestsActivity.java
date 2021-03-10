package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView myRequestsList;

    private DatabaseReference chatRequestsRef, usersRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

//    private Button accept;
//    private Button decline;

    TabLayout tabs;
    TabLayout tabLayout;
    PeersTab tbsa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        tbsa = new PeersTab ( findViewById(R.id.tabBarRequests), this);
        tabs = tbsa.addTabs(2);

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
                        startActivity(new Intent(RequestsActivity.this, FindFriendsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(RequestsActivity.this, ContactsActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(RequestsActivity.this, RequestsActivity.class));
                        break;
                    }
                    case 3: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(RequestsActivity.this, ProfileActivity.class));
                        break;
                    }

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        myRequestsList = (RecyclerView) findViewById(R.id.chat_requests_list);
//        accept = (Button) findViewById(R.id.request_accept_btn);
//        decline = (Button) findViewById(R.id.request_decline_btn);
        myRequestsList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        chatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserInfo> options =
                new FirebaseRecyclerOptions.Builder<UserInfo>()
                .setQuery(chatRequestsRef.child(currentUserId), UserInfo.class)
                .build();

        FirebaseRecyclerAdapter<UserInfo, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserInfo, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull UserInfo model) {
                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_decline_btn).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String type = snapshot.getValue().toString();
                                    if (type.equals("received")) {
                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChild("image")) {
                                                    final String requestUserName = snapshot.child("username").getValue().toString();
                                                    final String requestName = snapshot.child("name").getValue().toString();
                                                    final String requestProfileImage = snapshot.child("image").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.name.setText(requestName);
                                                    // image
                                                }
                                                else {
                                                    final String requestUserName = snapshot.child("username").getValue().toString();
                                                    final String requestName = snapshot.child("name").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.name.setText(requestName);
                                                    // image up
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();

                                                    StorageReference picsChild = storage.getReference().child((String) getRef(position).getKey()+".jpg");

                                                    picsChild.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                        @Override
                                                        public void onSuccess(byte[] bytes) {
                                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                            // getUserNameAndSetNameAndMarker(key,bitmap, currentInRange);
                                                            holder.profileImage.setImageBitmap(bitmap);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Drawable draw = getResources().getDrawable(R.drawable.default_picture);
                                                            Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
                                                            holder.profileImage.setImageBitmap(bitmap);
                                                        }
                                                    });

                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Accept",
                                                                            "Decline"
                                                                    };
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);
                                                            builder.setTitle(requestUserName + " Contact Request");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {
                                                                    if (i == 0) {
                                                                        contactsRef.child(currentUserId).child(list_user_id).child("Contact")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    contactsRef.child(list_user_id).child(currentUserId).child("Contact")
                                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                chatRequestsRef.child(currentUserId).child(list_user_id)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                                            {
                                                                                                                if (task.isSuccessful())
                                                                                                                {
                                                                                                                    chatRequestsRef.child(list_user_id).child(currentUserId)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                                                {
                                                                                                                                    if (task.isSuccessful())
                                                                                                                                    {
                                                                                                                                        Toast.makeText(RequestsActivity.this, "New Contact Saved", Toast.LENGTH_SHORT).show();
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
                                                                            }
                                                                        });
                                                                    }
                                                                    if (i == 1) {
                                                                        chatRequestsRef.child(currentUserId).child(list_user_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            chatRequestsRef.child(list_user_id).child(currentUserId)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                                        {
                                                                                                            if (task.isSuccessful())
                                                                                                            {
                                                                                                                Toast.makeText(RequestsActivity.this, "Request Declined", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                    });


                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else if(type.equals("sent")) {
                                        Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                                        request_sent_btn.setText("Request Sent");

                                        holder.itemView.findViewById(R.id.request_decline_btn).setVisibility(View.INVISIBLE);

                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChild("image")) {
                                                    final String requestUserName = snapshot.child("username").getValue().toString();
                                                    final String requestName = snapshot.child("name").getValue().toString();
                                                    final String requestProfileImage = snapshot.child("image").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.name.setText(requestName);
                                                    // image
                                                }
                                                else {
                                                    final String requestUserName = snapshot.child("username").getValue().toString();
                                                    final String requestName = snapshot.child("name").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.name.setText("You have sent a request to " + requestName);
                                                    // image up
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();

                                                    StorageReference picsChild = storage.getReference().child((String) getRef(position).getKey()+".jpg");

                                                    picsChild.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                        @Override
                                                        public void onSuccess(byte[] bytes) {
                                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                            // getUserNameAndSetNameAndMarker(key,bitmap, currentInRange);
                                                            holder.profileImage.setImageBitmap(bitmap);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Drawable draw = getResources().getDrawable(R.drawable.default_picture);
                                                            Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
                                                            holder.profileImage.setImageBitmap(bitmap);
                                                        }
                                                    });

                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            CharSequence options[] = new CharSequence[]
                                                                    {
                                                                            "Cancel Request"
                                                                    };
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);
                                                            builder.setTitle("Request Sent Already");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {

                                                                    if (i == 0) {
                                                                        chatRequestsRef.child(currentUserId).child(list_user_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            chatRequestsRef.child(list_user_id).child(currentUserId)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                                        {
                                                                                                            if (task.isSuccessful())
                                                                                                            {
                                                                                                                Toast.makeText(RequestsActivity.this, "Cancelled Request", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                    });

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };

        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, name;
        CircularImageView profileImage;
        Button acceptBtn, declineBtn;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            name = itemView.findViewById(R.id.profile_name);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            acceptBtn = itemView.findViewById(R.id.request_accept_btn);
            declineBtn = itemView.findViewById(R.id.request_decline_btn);

        }
    }

}