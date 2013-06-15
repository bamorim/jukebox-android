package com.pytera.jukebox;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.polidea.webimageview.WebImageView;

/**
 * Created by bamorim on 6/12/13.
 */
public class MusicActivity extends SherlockActivity {
    private Music mMusic;
    private JukeboxServer mServer;
    private ProgressDialog mDialog;
    private TextView txtTitle;
    private TextView txtArtist;
    private TextView txtFilename;
    private Context mContext;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        String path = getIntent().getStringExtra("path");
        txtTitle = (TextView) findViewById(R.id.txtMusicTitle);
        txtArtist = (TextView) findViewById(R.id.txtMusicArtist);
        txtFilename = (TextView) findViewById(R.id.txtFilename);
        
        mServer = MainActivity.getCurrentServer();
        
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
    	mDialog.show();
    	
        new GetMusicInfo().execute(path);
    }

    public void addMusic(View v) {
        new QueueMusic().execute();
    }
    
    private class GetMusicInfo extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            mMusic = mServer.getMusic(strings[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            if(mMusic != null) {
                WebImageView imgCover = (WebImageView) findViewById(R.id.imgCover);
                imgCover.setImageURL(mServer.getMusicImagePath(mMusic));

                txtTitle.setText(mMusic.getTitle());
                txtArtist.setText(mMusic.getArtist());
                txtFilename.setText(mMusic.getName());
            }
            mDialog.dismiss();
        }
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case android.R.id.home:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    private class QueueMusic extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			return mServer.queueMusic(mMusic);
		}
		
		@Override
		protected void onPostExecute(Boolean added){
            mDialog.dismiss();
			if(added) {
				finish();
			} else {
				AlertDialog.Builder mMessage = new AlertDialog.Builder(mContext);
				mMessage.setCancelable(true);
				mMessage.setTitle("Ocorreu um erro.");
				mMessage.setMessage("Tente novamente mais tarde.");
				mMessage.show();
			}
		}
    	
    }
}