package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private EditText emailXML, nameXML;
    private FirebaseAuth mFirebaseAuth;
    Tabs tbs;
    TabLayout tabs;
   // private ImageView ivProfile;

    //private StorageReference fileStorage;
    private FirebaseUser firebaseUser;
    //private DatabaseReference databaseReference;

    //private Uri localFileUri, serverFileUri;
    TabLayout tabLayout;


    public void btnChangePassword(View view)
    {
        startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        tbs = new Tabs ( findViewById(R.id.tabBarProfile), this);
        tabs = tbs.addTabs(4);

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
                        startActivity(new Intent(ProfileActivity.this, MapsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, ChatActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, ChatRegionalActivity.class));
                        break;
                    }
                    case 3:{break;}
                    case 4: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        /**
         * uncommenting will fuck it all up, profile tab works when all commented out
         * Im leaving it here for now, ill figure it out later but it works
        //emailXML = findViewById(R.id.userEmail);
        //nameXML = findViewById(R.id.legalNameSignUp);
        //ivProfile = findViewById(R.id.ivProfile);

        //fileStorage = FirebaseStorage.getInstance().getReference();

        //mFirebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = mFirebaseAuth.getCurrentUser();
        **/
        if(firebaseUser!=null)
        {
            //display users info
            nameXML.setText(firebaseUser.getDisplayName());
            emailXML.setText(firebaseUser.getEmail());
            /**
            // serverFileUri = firebaseUser.getPhotoUrl();

            //use glide library to show users image, if no picture is select or error happen, defualt picture will show
            if(serverFileUri!=null)
            {
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.default_picture)
                        .error(R.drawable.default_picture)
                        .into(ivProfile);
            }
             **/
        }
   }

    //so user can logout
    public void btnLogoutclick(View view)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        //finish---- cant click back arrow to go back to profile page
        finish();
    }



}