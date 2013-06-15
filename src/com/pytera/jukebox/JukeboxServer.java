package com.pytera.jukebox;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Class to hold JukeboxServer information such as address and port.
 */
public class JukeboxServer {
    private InetAddress mAddr;
    private int mPort;
    private static String apiPath = "api/v1/"; //When we release newer api version we would probably want to make it not static
    private String mToken;
    private RestAdapter restAdapter;
    private Jukebox jukebox;
    
	interface Jukebox {
		@GET("/dir/{path}")
		Directory directory(
				@Path("path") String path
		);
		
		@GET("/music/{path}")
		Music music(
				@Path("path") String path
		);

		@FormUrlEncoded
		@POST("/playlist")
		Response queue(
				@Field("path") String path,
				@Field("token") String token
		);
		
		@GET("/now_playing")
		Music now_playing();
		
		@GET("/playlist")
		Playlist playlist(
				@Query("token") String token
		);
		
		@GET("/search")
		Search search(
				@Query("q") String q
		);
	}

    public JukeboxServer(InetAddress a, int p) {
        mAddr = a;
        mPort = p;
        restAdapter = new RestAdapter.Builder().setServer(getWebUrl()+apiPath).build();
		jukebox = restAdapter.create(Jukebox.class);
    }

    public String getMusicImagePath(Music m) {
        return getWebUrl()+apiPath+"music/"+m.getId()+"/image";
    }

    public String getWebUrl() {
        return "http://" + mAddr.getHostAddress() + ":" + mPort + "/";
    }

    public InetAddress getAddr(){return mAddr;}
    public int getPort(){return mPort;}

    public void setToken(String token){
        mToken = token;
    }

    public Music getNowPlaying() { return jukebox.now_playing(); }
    
    public Playlist getPlaylist() { return jukebox.playlist(mToken); }

    public Directory getDirectory(String path){ return jukebox.directory(path); }
    
    public Search getSearchResults(String query) { return jukebox.search(query); }
    
    public Music getMusic(String path){
    	return jukebox.music(path);
    }

    public boolean queueMusic(Music music){
    	Response r = jukebox.queue(music.getId(), mToken);
    	if((r.getStatus() / 100) == 2)
    		return true;
    	return false;
    }
    
    

}
