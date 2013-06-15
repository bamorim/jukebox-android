package com.pytera.jukebox;

import java.util.List;

public class Search implements Container {
    private List<Path> directories;
    private List<Music> musics;

    public List<Path> getDirectories(){return directories;}
    public List<Music> getMusics(){return musics;}
}
