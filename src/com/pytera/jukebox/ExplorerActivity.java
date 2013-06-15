package com.pytera.jukebox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import java.util.Stack;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;

/**
 * ExplorerActivity is the main activity after we find the server.
 */
public class ExplorerActivity extends SherlockListActivity {
    private JukeboxServer mServer;
    private Container currentDirectory;
    private Stack<Container> pastDirectories;
    private ExplorerListAdapter listAdapter;
    private ProgressDialog mDialog;
    private SearchView searchView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pastDirectories = new Stack<Container>();
        mServer = MainActivity.getCurrentServer();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);

        browseTo("");
    }

    private void browseTo(String path){
        new GetDirectoryTask().execute(path);
    }
    
    private void saveCurrentDirectory(){
        if(this.currentDirectory != null)
            pastDirectories.add(this.currentDirectory);
    }

    private void browseBack(){
        if(this.pastDirectories.empty()) {
        	mDialog.dismiss();
            finish();
        } else {
            this.currentDirectory = this.pastDirectories.pop();
            this.fill(this.currentDirectory);
        }
    }

    private void fill(Container dir) {
        listAdapter = new ExplorerListAdapter(this);

        if(dir.getMusics().size() > 0)
            listAdapter.addSeparator("Musicas");
        for(Music music: dir.getMusics()) {
            listAdapter.addItem(music);
        }

        if(dir.getDirectories().size() > 0)
            listAdapter.addSeparator("Diretórios");
        for(Path path: dir.getDirectories()) {
            listAdapter.addItem(path);
        }

        this.setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);
        Path p = (Path) getListAdapter().getItem(position);
        if(p.getClass() == Path.class)
            browseTo(p.getId());
        else if(p.getClass() == Music.class) {
            Intent intent = new Intent(this, MusicActivity.class);
            intent.putExtra("path", p.getId());
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    browseBack();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case android.R.id.home:
	            browseBack();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Create the search view
        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search for music...");
        searchView.setIconified(true);
        searchView.setPadding(10, 0, 10, 0);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				new SearchTask().execute(query);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

        menu.add("Search")
                .setIcon(R.drawable.abs__ic_search)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    class SearchTask extends AsyncTask<String, Void, Search>{
        @Override
        protected void onPreExecute(){
            mDialog.show();
        }
        @Override
        protected Search doInBackground(String... strings) {
            return mServer.getSearchResults(strings[0]);
        }
        @Override
        protected void onPostExecute(Search d) {
        	saveCurrentDirectory();
            currentDirectory = d;
            fill(d);
            mDialog.dismiss();
            searchView.clearFocus();
        }
    }
    
    class GetDirectoryTask extends AsyncTask<String, Void, Directory>{
        @Override
        protected void onPreExecute(){
            mDialog.show();
        }
        @Override
        protected Directory doInBackground(String... strings) {
            return mServer.getDirectory(strings[0]);
        }
        @Override
        protected void onPostExecute(Directory d) {
        	saveCurrentDirectory();
            currentDirectory = d;
            fill(d);
            mDialog.dismiss();
        }
    }
}