package com.example.chatapp;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.tabs.TabLayout;


public class PeersTab
{

    TabLayout tabLayout;
    Context context;

    public PeersTab(TabLayout tabs, Context context)
    {
        this.tabLayout = tabs;
        this.context = context;
    };

    public TabLayout addTabs(int index)
    {
        tabLayout.addTab(tabLayout.newTab().setText("Find Peers"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.addTab(tabLayout.newTab().setText("Requests"));

        //makes chat that good purple
        tabLayout.getTabAt(index).select();
        return tabLayout;
    }
}