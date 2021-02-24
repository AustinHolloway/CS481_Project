package com.example.chatapp.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.chatapp.chat.ChatMessages;

public class ChatMessagesAdapter extends ArrayAdapter<ChatMessages> {

    public ChatMessagesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public View getView (int position, View convertView, ViewGroup parent){
       // Button iconSend =(Button) findViewById(R.id.iconSend);
        return convertView;
    }
}
