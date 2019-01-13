package com.yoyo.abhinay599.abplayer;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class player extends AppCompatActivity {

    ImageView play,forward,backward;
    MediaPlayer mplayer=null;
    TextView end,seektime;
    SeekBar seekBar;
    Handler handler;
    Runnable runnable;
    String check="no";
    Bundle outState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        play=findViewById(R.id.play);
        end=findViewById(R.id.end);
        seekBar=findViewById(R.id.seekBar3);
        seektime=findViewById(R.id.seektime);
        Uri uri=Uri.parse(getIntent().getStringExtra("songuri"));


            mplayer=new MediaPlayer();
            mplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mplayer.setDataSource(this,uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mplayer.prepareAsync();

            mplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    seekBar.setMax(mplayer.getDuration());
                      settime(end,mplayer.getDuration());
                      mplayer.start();
                      play.setImageResource(R.drawable.pause);
                }
            });

        handler=new Handler();

       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               if(b)
               {
                   mplayer.seekTo(i);
                   if(i==seekBar.getMax())
                   {
                       seekBar.setProgress(0);
                       mplayer.seekTo(0);
                       play.setImageResource(R.drawable.play);
                       mplayer.pause();
                   }
                   settime(seektime,seekBar.getProgress());
               }
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });

        seektime.setText("00:00");
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(mplayer.isPlaying())
                    {
                        mplayer.pause();

                        play.setImageResource(R.drawable.play);

                    }
                    else
                    {
                        mplayer.start();
                        play.setImageResource(R.drawable.pause);
                        seeking();
                    }


            }
        });



    }



    public void seeking()
    {
        seekBar.setProgress(mplayer.getCurrentPosition());
        settime(seektime,seekBar.getProgress());

        runnable=new Runnable() {
            @Override
            public void run() {
                seeking();
            }
        };
        handler.postDelayed(runnable,1000);

    }


    public void settime(TextView view, int time)
    {
        time=time/1000;
        int minute=time/60;
        int second=time%60;
        String min,sec;
        if(minute<10)
            min="0"+Integer.toString(minute);
        else
            min=Integer.toString(minute);
        if(second<10)
            sec="0"+Integer.toString(second);
        else
            sec=Integer.toString(second);

        view.setText(min+":"+sec);
    }

}
