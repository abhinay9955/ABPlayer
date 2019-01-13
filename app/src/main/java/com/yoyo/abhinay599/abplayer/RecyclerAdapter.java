package com.yoyo.abhinay599.abplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {


     static ArrayList<Songs>  songs;
    Context context;
    MediaPlayer mediaPlayer;
    Loader loader;
    ViewGroup parent;


    public RecyclerAdapter(ArrayList<Songs> songs, Context context, MediaPlayer mediaPlayer,Loader loader)
    {
        this.songs=songs;
        this.context=context;
        this.mediaPlayer=mediaPlayer;
        this.loader=loader;

    }
    public interface Loader{
        public void Loadd(Songs s);
    }


    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        this.parent=parent;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        MyViewHolder myviewholder=new MyViewHolder(view,context,songs,mediaPlayer);
        Log.i( "onCreateViewHolder: ",songs.get(0).getTitle());
        return myviewholder;
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
       holder.title.setText(songs.get(position).getTitle());
        Log.i("onBindViewHolder: ",songs.get(position).getTitle());
       holder.artist.setText(songs.get(position).getArtist());
       if(songs.get(position).getAlbumart() != null)
       holder.imgview.setImageDrawable(Drawable.createFromPath(songs.get(position).getAlbumart()));
       else
           holder.imgview.setImageResource(R.drawable.music);
       holder.loader=loader;
    }
   /* public void setLoader(Loader loader)
    {
        this.loader=loader;
    }*/

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         TextView title,artist;
         ImageView imgview;
         Context context;
         ArrayList<Songs> songs;
         MediaPlayer mediaPlayer;
           protected Loader loader;

        public MyViewHolder(View itemView, Context context, ArrayList<Songs> songs,MediaPlayer mediaPlayer) {
            super(itemView);

            title=itemView.findViewById(R.id.songname);
            artist=itemView.findViewById(R.id.songartist);
            imgview=itemView.findViewById(R.id.imageView2);
            this.context=context;
            this.songs=songs;
            this.mediaPlayer=mediaPlayer;
             //his.loader=loader;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i("onClick: ",songs.get(getAdapterPosition()).getTitle());

           // ((songslist)context).set_every(songs.get(getAdapterPosition()));
            if(loader!=null)
            loader.Loadd(RecyclerAdapter.songs.get(getAdapterPosition()));


        }

    }

    public  void updatelist(ArrayList<Songs> newlist)
    {

        RecyclerAdapter.songs=new ArrayList<>();
        RecyclerAdapter.songs.addAll(newlist);
        

    }





}
