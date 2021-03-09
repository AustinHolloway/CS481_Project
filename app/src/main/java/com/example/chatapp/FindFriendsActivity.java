package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatapp.chat.ChatActivity;
import com.example.chatapp.chat.ChatRegionalActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class FindFriendsActivity extends AppCompatActivity {

  //  Tabs tbs;
    TabLayout tabs;
    TabLayout tabLayout;
    PeersTab tbsa;

    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        UsersRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

 //       tbs = new Tabs ( findViewById(R.id.tabBarFriends), this);
//        tabs = tbs.addTabs(3);

        tbsa = new PeersTab ( findViewById(R.id.tabBarFriends), this);
        tabs = tbsa.addTabs(0);


//        //  TODO:Set up remove it when done
//        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
//        {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab)
//            {
//                int tabPos = tabs.getSelectedTabPosition();
//                switch (tabPos)
//                {
//                    case 0:
//                    {
//                        tabs.clearOnTabSelectedListeners();
//                        startActivity(new Intent(FindFriendsActivity.this, MapsActivity.class));
//                        break;
//                    }
//                    case 1:{
//                        tabs.clearOnTabSelectedListeners();
//                        startActivity(new Intent(FindFriendsActivity.this, ChatActivity.class));
//                        break;
//                    }
//                    case 2:{
//                        tabs.clearOnTabSelectedListeners();
//                        startActivity(new Intent(FindFriendsActivity.this, ChatRegionalActivity.class));
//                        break;
//                    }
//                    case 3:{
//                        tabs.clearOnTabSelectedListeners();
//                        startActivity(new Intent(FindFriendsActivity.this, FindFriendsActivity.class));
//                        break;
//                    }
//                    case 4:
//                    {
//                        tabs.clearOnTabSelectedListeners();
//                        startActivity(new Intent(FindFriendsActivity.this, ProfileActivity.class)) ;
//                        break;
//                    }
//
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {}
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {}
//        });
//    }
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
                        startActivity(new Intent(FindFriendsActivity.this, FindFriendsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(FindFriendsActivity.this, ContactsActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(FindFriendsActivity.this, ChatRegionalActivity.class));
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

        FirebaseRecyclerOptions<UserInfo> options =
                new FirebaseRecyclerOptions.Builder<UserInfo>()
                .setQuery(UsersRef, UserInfo.class)
                .build();

        FirebaseRecyclerAdapter<UserInfo, FindFriendViewHolder> adapter =
              new FirebaseRecyclerAdapter<UserInfo, FindFriendViewHolder>(options) {
                  @Override
                  protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull UserInfo model) {
                      holder.userName.setText(model.getUsername());
                      holder.userStatus.setText(model.getName());
                      // image up
   // ///////////////////////////
                      //ref to the storage bucket
                      FirebaseStorage storage = FirebaseStorage.getInstance();

                      StorageReference picsChild = storage.getReference().child((String) getRef(position).getKey()+".jpg");

                      picsChild.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
                      {
                          @Override
                          public void onSuccess(byte[] bytes)
                          {
                              Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                             // getUserNameAndSetNameAndMarker(key,bitmap, currentInRange);
                              holder.profileImage.setImageBitmap(bitmap);
                          }
                      }).addOnFailureListener(new OnFailureListener()
                      {
                          @Override
                          public void onFailure(@NonNull Exception e)
                          {
                              Drawable draw = getResources().getDrawable(R.drawable.default_picture);
                              Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
                              holder.profileImage.setImageBitmap(bitmap);
                                                        }
                      });
////////////////////////////////////////
                      holder.itemView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              String visit_user_id = getRef(position).getKey();

                              Intent peerIntent = new Intent(FindFriendsActivity.this, PeerActivity.class);
                              peerIntent.putExtra("visit_user_id", visit_user_id);
                              startActivity(peerIntent);
                          }
                      });

                  }

                  @NonNull
                  @Override
                  public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                      FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                      return viewHolder;
                  }
              };

        FindFriendsRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircularImageView profileImage;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.profile_name);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }
}