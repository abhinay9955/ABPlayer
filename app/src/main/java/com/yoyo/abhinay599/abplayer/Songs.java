package com.yoyo.abhinay599.abplayer;

public class Songs {

    String title;
    String artist;
    String location;
    String albumart;

    public Songs(String title,String artist,String location,String albumart)
    {
        this.title=title;
        this.artist=artist;
        this.location=location;
        this.albumart=albumart;
    }

    public String getTitle()
    { return this.title;}

    public String getArtist()
    {
        return this.artist;
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getAlbumart(){return this.albumart;}

}
