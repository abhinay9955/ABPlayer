package com.yoyo.abhinay599.abplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class songslist extends AppCompatActivity implements SearchView.OnQueryTextListener{

    static ArrayList<Songs> songs;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter recyclerAdapter;
     static MediaPlayer mediaPlayer;
     SearchView searchView;

     TextView screetitle;
     TextView screeartist;
     Button screeplay;
     TextView playertitle;
    TextView seektime;
    TextView end;
    RecyclerAdapter.Loader loader;
    static int position;
    RecyclerAdapter.Loader load;
    VideoView videoview;
    int d;
    Toolbar toolbar;

    boolean pausemode=true; boolean pausemod;
    PhoneStateListener phoneStateListener;
    TelephonyManager telephonyManager;
    ConstraintLayout relativeLayout;
    ImageButton imgbt;
     ImageButton playerplay;
     String albumm;
    ImageButton playernext;
    ImageButton playerlast;
     ImageButton repsuf;
    ImageButton shufffle;
    SeekBar seekBar;
    Handler handler;
    ImageView screenimage,bigimage;
    Boolean bl;
    Runnable runnable;
   private String[] colors= {"#870000","#bc5100","#524c00","#003d00","#002f6c","#560027"};
   ConstraintLayout container;
   boolean clickedonce=false,lock=false;
   Window window=null;
   MediaController mediaController;
   SharedPreferences sharedPreferences;
   Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songlist);
          toolbar =findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);

        stopService(new Intent(this,Myservice.class));
        if(Build.VERSION.SDK_INT>=21)
        {
            window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
        screenimage=findViewById(R.id.imageView6);
        relativeLayout=findViewById(R.id.playerwindow);
        bigimage=findViewById(R.id.bigimage);

             mediaPlayer=new MediaPlayer();


        recyclerView=findViewById(R.id.redcyclerview);
        repsuf=findViewById(R.id.repsuf);
        shufffle=findViewById(R.id.shufffle);

        imgbt=findViewById(R.id.shutwindow);

        relativeLayout.setTranslationY(5000f);
        layoutManager= new LinearLayoutManager(this);
            screeplay=findViewById(R.id.screeplay);
            screeplay.setVisibility(View.VISIBLE);
            screetitle=findViewById(R.id.screetitle);
            screetitle.setSelected(true);
           // screetitle.setText("WELCOME");
            screeartist=findViewById(R.id.screenartist);
            container=findViewById(R.id.container);
            //screeartist.setText("TO ABPlayer");
              loader= new RecyclerAdapter.Loader() {
                  @Override
                  public void Loadd(Songs s) {
                      update(s);
                  }
              };
           seekBar=findViewById(R.id.seekBar);
           end=findViewById(R.id.end);
           playerplay=findViewById(R.id.playerplay);
           playernext=findViewById(R.id.playernext);
           playerlast=findViewById(R.id.playerlast);
           playertitle=relativeLayout.findViewById(R.id.playertitle);
           mediaController=new MediaController(this);
           playertitle.setSelected(true);
           seektime=findViewById(R.id.seektime);
           //playernext.setEnabled(false);
           //playerlast.setEnabled(false);
           handler=new Handler();

        sharedPreferences=getSharedPreferences("LastData",Context.MODE_PRIVATE);
        String ur=sharedPreferences.getString("songuri",null);
        String ttl=sharedPreferences.getString("songtitle"," ");
        String arst=sharedPreferences.getString("songartist"," ");
        d=sharedPreferences.getInt("seeker",0);
        albumm=sharedPreferences.getString("songalbum","");
        Log.i("lewsin: ",albumm);

        position=sharedPreferences.getInt("position",0);
        final int dur=sharedPreferences.getInt("duration",0);

        if(ur!=null)
        {
            Uri urii=Uri.parse(ur);
            screetitle.setText(ttl);
            screeartist.setText(arst);
            playertitle.setText(ttl);
            bigimage.setImageResource(R.drawable.music);
            screenimage.setImageResource(R.drawable.music);


            try {
                mediaPlayer.setDataSource(songslist.this,urii);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(d);

                    settime(end,dur);

                    seekBar.setMax(dur);
                    seeking(d);

                    if(getIntent().getIntExtra("check",0)==1)
                    {
                        screeplay.setBackgroundResource(R.drawable.pauseb);
                        mediaPlayer.start();
                        playerplay.setImageResource(R.drawable.pause);
                    }

                    //seeking();


                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    next();
                }
            });
        }



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                {
                    mediaPlayer.seekTo(i);
                    if(i==seekBar.getMax())
                    {
                        seekBar.setProgress(0);
                        mediaPlayer.seekTo(0);
                        playerplay.setImageResource(R.drawable.play);
                        screeplay.setBackgroundResource(R.drawable.playb);
                        mediaPlayer.pause();
                        pausemode=true;
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
                                pausemod=true;
                                mediaPlayer.pause();
                                break;
                            }

                        }
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        {
                            if(mediaPlayer.isPlaying())
                            {
                                mediaPlayer.pause();
                                pausemod=true;

                            }
                            break;
                        }
                        case TelephonyManager.CALL_STATE_IDLE:
                        {
                            if(mediaPlayer!=null)
                            {
                                if(pausemod)
                                {
                                    if(!pausemode)
                                    {pausemod=false;
                                    mediaPlayer.start();}

                                }
                            }
                            break;
                        }
                    }
                }
            };
            telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);



        //Permission

        if(ContextCompat.checkSelfPermission(songslist.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(songslist.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(songslist.this,
                       new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

            }
            else
            {
                ActivityCompat.requestPermissions(songslist.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
        }
        else
        {
            createlist();
        }

        if(ContextCompat.checkSelfPermission(songslist.this,
                Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(songslist.this,
                           Manifest.permission.READ_PHONE_STATE))
            {
                ActivityCompat.requestPermissions(songslist.this,
                          new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
            else
            {
                ActivityCompat.requestPermissions(songslist.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }
        else
        {

        }
    }
    // End of OnCreate


    //Oncreate options menu




    public void playpause(View view)
    {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    pausemode=true;
                    screeplay.setBackgroundResource(R.drawable.playb);
                   playerplay.setImageResource(R.drawable.play);
                }
                else
                {  mediaPlayer.start();
                    screeplay.setBackgroundResource(R.drawable.pauseb);
                    playerplay.setImageResource(R.drawable.pause);
                    pausemode=false;
                }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode)
        {
            case  0: {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(songslist.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                        createlist();
                    else
                    { ActivityCompat.requestPermissions(songslist.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                    }
                }
                else
                {
                    finish();
                }
                return;
            }


            case 1:  {

                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(songslist.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)

                    {

                    }
                    else
                    {
                        ActivityCompat.requestPermissions(songslist.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    }
                }
                else
                {
                    finish();
                }
                return;
            }


        }


    }
    //PERMISSION FOR READ_EXTERNAL_STORAGE ENDS




    public void getsongs()
    {
        ContentResolver contentResolver=getContentResolver();
        Uri  uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor=contentResolver.query(uri,null,null,null,null);
        Cursor cursoralbum;

        if(cursor!=null && cursor.moveToFirst() ) {

                int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int songLocation = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int albumid=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

                do {
                    Long allbumid=(cursor.getLong(albumid));

                   cursoralbum=contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM_ART},MediaStore.Audio.Albums._ID+"="+allbumid,null,null);
                    if( cursoralbum.moveToFirst()) {
                        String title = cursor.getString(songTitle);
                        String artist = cursor.getString(songArtist);
                        String location = cursor.getString(songLocation);
                        String albumart=cursoralbum.getString(cursoralbum.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                        Songs s = new Songs(title, artist, location, albumart);
                        songs.add(s);
                    }

                } while (cursor.moveToNext());

            }
        }



    public void createlist()
    {
      songs=new ArrayList<Songs>();
      getsongs();
      sortsongs(songs,0);

      recyclerView.setLayoutManager(layoutManager);
      recyclerAdapter=new RecyclerAdapter(songs, this, mediaPlayer,loader);

      recyclerView.setAdapter(recyclerAdapter);



    }

    public void sortsongs(ArrayList<Songs> songs,int a)
    {   if(a==0) {
        Collections.sort(songs, new Comparator<Songs>() {
            @Override
            public int compare(Songs s, Songs t1) {
                int compared = 0;
                compared = s.getTitle().compareTo(t1.getTitle());
                return compared;
            }
        });
        }
        if(a==1)
        {
            Collections.sort(songs, new Comparator<Songs>() {
                @Override
                public int compare(Songs s, Songs t1) {
                    int comp=0;
                    comp=s.getArtist().compareTo(t1.getArtist());
                    return  comp;
                }
            });
        }
    }

    public void sortbt(View view)
    {
        if(view.getId()==R.id.titlebt)
        {
            sortsongs(songs,0);
            recyclerAdapter.notifyDataSetChanged();
        }
        if(view.getId()==R.id.artistbt)
        {
            sortsongs(songs,1);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    public void player(View view)
    {
        relativeLayout.animate().translationYBy(-5000f).setDuration(100);
           recyclerView.setVisibility(view.INVISIBLE);


    }
    public void lock(View view)
    {
        if(lock)
        {
            imgbt.setImageResource(R.drawable.unlock);
            lock=false;
            playerplay.setEnabled(true);
            playernext.setEnabled(true);
            playerlast.setEnabled(true);
            repsuf.setEnabled(true);
            shufffle.setEnabled(true);
            seekBar.setEnabled(true);
            screeplay.setEnabled(true);
            Toast.makeText(songslist.this,"screen unlocked",Toast.LENGTH_SHORT).show();

        }
        else {
            imgbt.setImageResource(R.drawable.lock);
            lock = true;
            playerplay.setEnabled(false);
            playernext.setEnabled(false);
            playerlast.setEnabled(false);
            repsuf.setEnabled(false);
            shufffle.setEnabled(false);
            seekBar.setEnabled(false);
            screeplay.setEnabled(false);
            Toast.makeText(songslist.this, "screen locked", Toast.LENGTH_SHORT).show();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgbt.setVisibility(View.INVISIBLE);
                    imgbt.setEnabled(false);
                }
            }, 5000);

        }
    }


    public void visi(View view)
    {
        imgbt.setVisibility(View.VISIBLE);
        imgbt.setEnabled(true);

       /* if(lock) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgbt.setVisibility(View.INVISIBLE);
                    imgbt.setEnabled(false);
                }
            }, 5000);
        }*/


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
    public void seeking(int d)
    {
        seekBar.setProgress(d);
        settime(seektime,seekBar.getProgress());

        runnable=new Runnable() {
            @Override
            public void run() {
                seeking(mediaPlayer.getCurrentPosition());
            }
        };
        handler.postDelayed(runnable,1000);

    }

    public  void next()
    {
        if(position==songs.size()-1)
        { position=0;
           update(songs.get(position));}
           else
        {
            position++;
            update(songs.get(position));
        }
    }

    public void previous(View view)
    {
        if(position==0)
        { position=songs.size()-1;
            update(songs.get(position));}
        else
        {
            position--;
            update(songs.get(position));
        }
    }

    public void fornext(View view)
    {
        next();
    }

    public void update(Songs s){
        colorchange();
        //screeplay.setVisibility(View.VISIBLE);
        screetitle.setText(s.getTitle());
        playertitle.setText(s.getTitle());
        if(s.getAlbumart()!=null) {
            screenimage.setImageDrawable(Drawable.createFromPath(s.getAlbumart()));
            bigimage.setImageDrawable(Drawable.createFromPath(s.getAlbumart()));
        }
        else {
            screenimage.setImageResource(R.drawable.music);
            bigimage.setImageResource(R.drawable.music);
        }
        screeartist.setText("ARTIST: "+s.getArtist());
        screeartist.setMovementMethod(new ScrollingMovementMethod());
        screeplay.setBackgroundResource(R.drawable.pauseb);
        playerplay.setImageResource(R.drawable.pause);
        position=songs.indexOf(s);
        mediaPlayer.reset();
        repsuf.setImageResource(R.drawable.repeat_one);
        Uri uri=Uri.parse(s.getLocation());
        try {
            mediaPlayer.setDataSource(songslist.this,uri);
        } catch (IOException e) {
            Toast.makeText(songslist.this,"cn't play song",Toast.LENGTH_LONG).show();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
                settime(end,mediaPlayer.getDuration());
                mediaPlayer.start();
                pausemode=false;

                seeking(mediaPlayer.getCurrentPosition());
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(songslist.this,"can't play song",Toast.LENGTH_SHORT).show();
                next();
                return false;
            }
        });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    next();
                }
            });

    }

    public void colorchange()
    {

        Random rand=new Random();
        int a=rand.nextInt(6);
        relativeLayout.setBackgroundColor(Color.parseColor(colors[a]));
        playerplay.setBackgroundColor(Color.parseColor(colors[a]));
        playernext.setBackgroundColor(Color.parseColor(colors[a]));
        playerlast.setBackgroundColor(Color.parseColor(colors[a]));
        imgbt.setBackgroundColor(Color.parseColor(colors[a]));
        repsuf.setBackgroundColor(Color.parseColor(colors[a]));
        shufffle.setBackgroundColor(Color.parseColor(colors[a]));
        if(window!=null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.parseColor(colors[a]));
            }


    }

    @Override
    public void onBackPressed() {

        if(clickedonce) {
            super.onBackPressed();

            return;
        }
        clickedonce=true;
        if(relativeLayout.getTranslationY()==0f) {
            relativeLayout.animate().translationY(5000f).setDuration(300);
            recyclerView.setVisibility(View.VISIBLE);
        }

        else
        {
            Toast.makeText(songslist.this,"press again to exit",Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clickedonce=false;
            }
        },2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // intent =new Intent(songslist.this,Myservice.class);
       // stopService(intent);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences=getSharedPreferences("LastData",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("songuri",songs.get(position).getLocation());
        editor.putString("songtitle",songs.get(position).getTitle());
        editor.putString("songartist",songs.get(position).getArtist());
        editor.putString("songalbum",songs.get(position).getAlbumart());
        editor.putInt("seeker",mediaPlayer.getCurrentPosition());
        //Toast.makeText(songslist.this,Integer.toString(mediaPlayer.getDuration()),Toast.LENGTH_LONG).show();
        editor.putInt("duration", mediaPlayer.getDuration());
        editor.putInt("position",position);
        editor.putBoolean("pausemode",pausemode);

        editor.apply();
        intent =new Intent(songslist.this,Myservice.class);
        startService(intent);
        mediaPlayer.stop();


    }

    public void repshuf(View view)
    {
        if(mediaPlayer.isLooping()) {
            mediaPlayer.setLooping(false);
            repsuf.setImageResource(R.drawable.repeat_one);
            Toast.makeText(songslist.this,"Repeat all songs",Toast.LENGTH_SHORT).show();
        }
        else {
            mediaPlayer.setLooping(true);
            repsuf.setImageResource(R.drawable.repeat);
            Toast.makeText(songslist.this,"Repeat current song",Toast.LENGTH_SHORT).show();
        }
    }

   public void shuffle(View view)
   {

       Random rand=new Random();

       int a=rand.nextInt(songs.size());

       if(a==position)
       {
           while(a==position)
           {
               a=rand.nextInt(songs.size());
           }
           update(songs.get(a));
       }
       else
           update(songs.get(a));



   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String nn=newText.toLowerCase();
        ArrayList<Songs> arr=new ArrayList<Songs>();
        for(Songs title:songs)
        {
            if(title.getTitle().toLowerCase().trim().contains(nn))
            {
                arr.add(title);
            }
        }
       recyclerAdapter.updatelist(arr);
        recyclerAdapter.notifyDataSetChanged();
        return false;
    }
}
