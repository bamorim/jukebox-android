package com.pytera.jukebox;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Playlist {
	private Integer position;
	private List<Music> musics;

	public Integer getPosition() {
		return position;
	}
	
	public List<Music> getMusics() {
		return musics;
	}
}
