package com.yoyo.abhinay599.abplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;

public class Myservice extends Service {
    @Nullable
    static


    MediaPlayer mediaPlayer;
    static SharedPreferences sharedPreferences;
    String ur;
    String ttl;
    String arst;
    int d;
    static int position;
    int dur;
    static Handler handler;
    static RemoteViews remoteViews;
    static Runnable runnable;
    TelephonyManager telephonyManager;
    PhoneStateListener phoneStateListener;
    static boolean pausemode;
    boolean bl;
    public static final String CHANNEL_ID="personal_notification";
    public static final int NOTIFICATION_ID=1;

    public static final String NOTIFY_PLAY=" com.yoyo.abhinay599.abplayer.play";
    public static final String NOTIFY_PAUSE=" com.yoyo.abhinay599.abplayer.pause";
    public static  final String NOTIFY_NEXT=" com.yoyo.abhinay599.abplayer.next";
    public static final String NOTIFY_PREVIOUS=" com.yoyo.abhinay599.abplayer.previous";
    static Context context;
    static NotificationManager notificationManagerCompat;
    static NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Myservice() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       context=this;
        mediaPlayer=new MediaPlayer();
        notificationManagerCompat=(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        builder=new NotificationCompat.Builder(context,CHANNEL_ID);
        sharedPreferences=getSharedPreferences("LastData", Context.MODE_PRIVATE);
        ur=sharedPreferences.getString("songuri",null);
        ttl=sharedPreferences.getString("songtitle"," ");
        arst=sharedPreferences.getString("songartist"," ");
       d=sharedPreferences.getInt("seeker",0);
       bl=sharedPreferences.getBoolean("pausemode",true);

           remoteViews=new RemoteViews(getPackageName(),R.layout.notification);
       position=sharedPreferences.getInt("position",0);
        dur=sharedPreferences.getInt("duration",0);
        Log.i("onStartCommand: ","hey");




        createnotifi();


        if(ur!=null)
        {
            Uri urii=Uri.parse(ur);

            try {
                mediaPlayer.setDataSource(this,urii);
                Log.i("onStartCommand: ","songloaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(d);
                    if(!bl)
                    mediaPlayer.start();



                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                  next();
                }
            });
        }


        telephonyManager= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener=new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch(state)
                {
                    case TelephonyManager.CALL_STATE_RINGING :
                    {
                        if(mediaPlayer.isPlaying())
                        {
                            pausemode=true;
                            mediaPlayer.pause();
                            break;
                        }

                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    {
                        if(mediaPlayer.isPlaying())
                        {
                            mediaPlayer.pause();
                            pausemode=true;

                        }
                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE:
                    {
                        if(mediaPlayer!=null)
                        {
                            if(pausemode)
                            {
                                pausemode=false;
                                mediaPlayer.start();

                            }
                        }
                        break;
                    }
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


        save();

        return START_STICKY;
    }


    public static void save()
    {
        final SharedPreferences.Editor editor=sharedPreferences.edit();

       handler= new Handler();
       runnable=new Runnable() {
           @Override
           public void run() {
               editor.putInt("seeker",mediaPlayer.getCurrentPosition());

               editor.apply();
               Log.i("run: ","run");
               save();
           }
       };
       handler.postDelayed(runnable,1000);

    }



   @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.pause();
        handler.removeCallbacks(runnable);
        NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

    }

    public static void next()
    {
        if(position==songslist.songs.size()-1)
            position=0;
        else
            position++;
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("position",position);
        editor.putString("songuri",songslist.songs.get(position).getLocation());
        editor.putString("songtitle",songslist.songs.get(position).getTitle());
        editor.putString("songartist",songslist.songs.get(position).getArtist());

        editor.apply();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(context,Uri.parse(songslist.songs.get(position).getLocation()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                editor.putInt("duration",mediaPlayer.getDuration());
                editor.apply();
                save();
            }
        });
       // RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.textView,sharedPreferences.getString("songtitle","Audio"));
        remoteViews.setImageViewResource(R.id.imageView2,R.drawable.musicaa);
        setlistener(remoteViews,context);
        Intent inttent=new Intent(context,songslist.class);
        inttent.putExtra("check",1);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,inttent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.musicaa);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setShowWhen(false);
        builder.setAutoCancel(true);
        // builder.setContentTitle("My Notification");
        //builder.setContentText("Hey There I Am Abhinay");
        builder.setContentIntent(pendingIntent);
        builder.setCustomContentView(remoteViews);




        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());



    }



    public static void previous()
    {
        if(position==0)
            position=songslist.songs.size()-1;
        else
            position--;
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("position",position);
        editor.putString("songuri",songslist.songs.get(position).getLocation());
        editor.putString("songtitle",songslist.songs.get(position).getTitle());
        editor.putString("songartist",songslist.songs.get(position).getArtist());
        editor.apply();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(context,Uri.parse(songslist.songs.get(position).getLocation()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                editor.putInt("duration",mediaPlayer.getDuration());
                editor.apply();
                save();
            }
        });
        // RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.textView,sharedPreferences.getString("songtitle","Audio"));
        remoteViews.setImageViewResource(R.id.imageView2,R.drawable.musicaa);
        setlistener(remoteViews,context);
        Intent inttent=new Intent(context,songslist.class);
        inttent.putExtra("check",1);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,inttent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.musicaa);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setShowWhen(false);
        builder.setAutoCancel(true);
        // builder.setContentTitle("My Notification");
        //builder.setContentText("Hey There I Am Abhinay");
        builder.setContentIntent(pendingIntent);
        builder.setCustomContentView(remoteViews);



        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());



    }


    public void createnotifi()
    {

        //RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notification);
        remoteViews.setImageViewResource(R.id.imageView2,R.drawable.musicaa);
        remoteViews.setTextViewText(R.id.textView,ttl);
        setlistener(remoteViews,this);
        Intent inttent=new Intent(this,songslist.class);
        inttent.putExtra("check",1);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,inttent,PendingIntent.FLAG_ONE_SHOT);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setShowWhen(false);
        // builder.setContentTitle("My Notification");
        //builder.setContentText("Hey There I Am Abhinay");
       builder.setContentIntent(pendingIntent);
        builder.setCustomContentView(remoteViews);
        builder.setAutoCancel(true);


        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());

    }
    public static void setlistener(RemoteViews views, Context context)
    {
        Intent iplay=new Intent(NOTIFY_PLAY);
        Intent inext=new Intent(NOTIFY_NEXT);
        Intent iprev=new Intent(NOTIFY_PREVIOUS);

        PendingIntent pplay=PendingIntent.getBroadcast(context,0,iplay,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.play,pplay);

        PendingIntent pnext=PendingIntent.getBroadcast(context,0,inext,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.next,pnext);

        PendingIntent pprev=PendingIntent.getBroadcast(context,0,iprev,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.previous,pprev);


    }


}
