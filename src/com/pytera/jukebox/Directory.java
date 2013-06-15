package com.pytera.jukebox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bamorim on 6/11/13.
 */
public class Directory extends Path implements Container {
    private List<Path> directories;
    private List<Music> musics;

    public List<Path> getDirectories(){return directories;}
    public List<Music> getMusics(){return musics;}
}
