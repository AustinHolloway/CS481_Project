package com.example.chatapp;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.tabs.TabLayout;


public class Tabs
{

    TabLayout tabLayout;
    Context context;

    public Tabs(TabLayout tabs, Context context)
    {
        this.tabLayout = tabs;
        this.context = context;
    };

    public TabLayout addTabs(int index)
    {
        tabLayout.addTab(tabLayout.newTab().setText("Local Map"));
        tabLayout.addTab(tabLayout.newTab().setText("World Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Local Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Find Peers"));
        tabLayout.addTab(tabLayout.newTab().setText("About Me"));

        //makes chat that good purple
        tabLayout.getTabAt(index).select();
        return tabLayout;
    }
}




