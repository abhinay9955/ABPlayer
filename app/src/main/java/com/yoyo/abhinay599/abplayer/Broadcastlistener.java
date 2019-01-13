package com.yoyo.abhinay599.abplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Broadcastlistener extends BroadcastReceiver {


    SharedPreferences.Editor editor;


    @Override
    public void onReceive(Context context, Intent intent) {

        String state=intent.getAction();


        if(state.equals(Myservice.NOTIFY_NEXT))
        {
            Myservice.next();

        }
        else if(state.equals(Myservice.NOTIFY_PLAY))
        {
            if(Myservice.mediaPlayer.isPlaying())
            {Myservice.mediaPlayer.pause();



             Myservice.remoteViews.setImageViewResource(R.id.play,R.drawable.plays);
             Myservice.builder.setCustomContentView(Myservice.remoteViews);
            Myservice.notificationManagerCompat.notify(Myservice.NOTIFICATION_ID,Myservice.builder.build());
            }
            else
            {    Myservice.mediaPlayer.start();


                Myservice.remoteViews.setImageViewResource(R.id.play,R.drawable.pauses);
               Myservice.builder.setCustomContentView(Myservice.remoteViews);
                Myservice.notificationManagerCompat.notify(Myservice.NOTIFICATION_ID,Myservice.builder.build());

            }
        }
        else if(state.equals(Myservice.NOTIFY_PREVIOUS))
        {
            Myservice.previous();
        }
    }
}
