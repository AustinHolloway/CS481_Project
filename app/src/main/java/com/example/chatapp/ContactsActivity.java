package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ContactsActivity extends AppCompatActivity {

    private View contactsView;
    private RecyclerView myContactsList;

    private DatabaseReference contactsRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        myContactsList = (RecyclerView) findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<UserInfo>()
                .setQuery(contactsRef, UserInfo.class)
                .build();

        FirebaseRecyclerAdapter<UserInfo, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<UserInfo, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull UserInfo model) {
                String usersIds = getRef(position).getKey();

                usersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("image")) {
                            String userImage = snapshot.child("image").getValue().toString();
                            String profileUsername = snapshot.child("username").getValue().toString();
                            String profileName = snapshot.child("name").getValue().toString();

                            holder.userName.setText(profileUsername);
                            holder.name.setText(profileName);
                            // Picasso.get().load(userImage).into(holder.profileImage)
                        }
                        else {
                            String profileUsername = snapshot.child("username").getValue().toString();
                            String profileName = snapshot.child("name").getValue().toString();

                            holder.userName.setText(profileUsername);
                            holder.name.setText(profileName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, name;
        CircularImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            name = itemView.findViewById(R.id.profile_name);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }
}