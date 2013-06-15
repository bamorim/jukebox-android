package com.pytera.jukebox;

import org.json.JSONObject;

/**
 * Created by bamorim on 6/11/13.
 */
public class Music extends Path {
    private String title;
    private String artist;
    private String album;

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
}
