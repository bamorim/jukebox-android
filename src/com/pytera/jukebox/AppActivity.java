package com.pytera.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AppActivity extends SherlockActivity {
	private TextView txtTitle;
	private TextView txtArtist;
	private TextView txtFilename;
	
	private ListView listView;
    private ExplorerListAdapter listAdapter;
	
    private Handler handler = new Handler();
    
    private Context mContext;
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        txtTitle = (TextView) findViewById(R.id.txtNowPlayingTitle);
        txtArtist = (TextView) findViewById(R.id.txtNowPlayingArtist);
        txtFilename = (TextView) findViewById(R.id.txtNowPlayingFilename);
        listView = (ListView) findViewById(R.id.listView);
        mContext = this;
        update();
        handler.postDelayed(new Runnable(){
        	public void run(){
        		update();
        		handler.postDelayed(this, 10000);
        	}
        }, 1000);
	}
	
	private void update(){
		new UpdateNowPlaying().execute();
		new UpdatePlaylist().execute();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("Add")
    		.setIcon(R.drawable.abs__ic_search)
    		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getTitle().equals("Add")) {
            Intent intent = new Intent(this, ExplorerActivity.class);
            startActivity(intent);
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
	private class UpdateNowPlaying extends AsyncTask<Void, Void, Music>  {
		@Override
		protected Music doInBackground(Void... arg0) {
			return MainActivity.getCurrentServer().getNowPlaying();
		}
		
		@Override
		protected void onPostExecute(Music m) {
			if(m == null){
				txtTitle.setText("PAUSADO");
				txtArtist.setText("");
				txtFilename.setText("");
			} else {
				txtTitle.setText(m.getTitle());
				txtArtist.setText(m.getArtist());
				txtFilename.setText(m.getName());
			}
		}
	}
	
	private class UpdatePlaylist extends AsyncTask<Void, Void, Playlist> {

		@Override
		protected Playlist doInBackground(Void... params) {
			return MainActivity.getCurrentServer().getPlaylist();
		}
		
		protected void onPostExecute(Playlist p) {
	        listAdapter = new ExplorerListAdapter(mContext);
	        listAdapter.addSeparator("Seus Pedidos");
	        if(p != null) for(Music m : p.getMusics()) {
				listAdapter.addItem(m);
			}
			listView.setAdapter(listAdapter);
		}
		
	}
}
