package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class ChatActivity extends AppCompatActivity
{

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