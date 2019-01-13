package com.yoyo.abhinay599.abplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intent =new Intent(MainActivity.this,songslist.class);
        intent.putExtra("check",0);
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                startActivity(intent);
            }
        };
        handler.postDelayed(runnable,4000);

    }
}
