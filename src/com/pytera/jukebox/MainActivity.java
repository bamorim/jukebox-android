package com.pytera.jukebox;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity implements Discoverer.Receiver {
    private TextView statusText;
    private Button findServer;
    private Context mContext;
    private static JukeboxServer currentServer;
    private static String mToken;

    public static JukeboxServer getCurrentServer(){ return currentServer; }
    public static String getToken(){ return mToken; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        statusText = (TextView) findViewById(R.id.textViewStatus);
        findServer = (Button) findViewById(R.id.buttonFindServer);
        mContext = this;

        SecureRandom random = new SecureRandom();
        mToken = new BigInteger(130, random).toString(32);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.contains("token")) {
        	mToken = sharedPref.getString("token", mToken);
        } else {
        	sharedPref.edit().putString("token", mToken).commit();
        }
    }

    public void findServer(View v) {
        Discoverer discoverer = new Discoverer((WifiManager) getSystemService(Context.WIFI_SERVICE), this);
        statusText.setText("Procurando servidor...");
        findServer.setEnabled(false);
        discoverer.start();
    }

    public void updateDiscoveryStatus(final String s){
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                statusText.setText(s);
            }
        });
    }

    //TODO: Let user choose server.
    public void updateFoundServers(final JukeboxServer server){
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                findServer.setEnabled(true);
                statusText.setText("");
                currentServer = server;
                server.setToken(mToken);
                Intent intent = new Intent(mContext, AppActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    public void updateFoundServers(){
        getWindow().getDecorView().post(new Runnable() {
            public void run() {
                findServer.setEnabled(true);
                statusText.setText("Servidor Não Encontrado!");
            }
        });
    }
    
}