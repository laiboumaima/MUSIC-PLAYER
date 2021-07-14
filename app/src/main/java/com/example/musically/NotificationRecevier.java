package com.example.musically;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.musically.Appclass.ACTION_NEXT;
import static com.example.musically.Appclass.ACTION_PLAY;
import static com.example.musically.Appclass.ACTION_PREVIOUS;

public class NotificationRecevier extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionname= intent.getAction();
        Intent serviceintent = new Intent(context,MusicService.class);
        if (actionname != null){
            switch (actionname) {

                case   ACTION_PLAY:
                    serviceintent.putExtra("Action","playpause");

                    context.startService(serviceintent);
                    break;
                case   ACTION_NEXT:
                    serviceintent.putExtra("Action","next");
                    context.startService(serviceintent);
                    break;
                case   ACTION_PREVIOUS :
                    serviceintent.putExtra("Action","previous");
                    context.startService(serviceintent);
                    break;

              }
            }
        }
    }

