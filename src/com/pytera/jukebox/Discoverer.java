package com.pytera.jukebox;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Discoverer class sends UDP packet and waits for responses to find Jukebox Servers in current network.
 */
public class Discoverer extends Thread {
    private static final String TAG = "Discovery";
    private static final int DISCOVERY_PORT = 5853;
    private static final int TIMEOUT_MS = 5000;

    private WifiManager mWifi;
    private Receiver mReceiver;
    private boolean mFoundUri;

    interface Receiver {
        void updateDiscoveryStatus(String msg);
        void updateFoundServers(JukeboxServer server);
        void updateFoundServers();
    }

    public Discoverer(WifiManager wifi, Receiver receiver){
        mWifi = wifi;
        mReceiver = receiver;
        mFoundUri = false;
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT_MS);

            sendDiscoveryRequest(socket);
            listenForResponses(socket);

            if(!mFoundUri) mReceiver.updateFoundServers();

            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not send discovery request", e);
        }
    }

    private void sendDiscoveryRequest(DatagramSocket socket) throws IOException {
        String data = "com.pytera.jukebox.discovery,"+DISCOVERY_PORT;
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), getBroadcastAddress(), DISCOVERY_PORT);
        socket.send(packet);
    }

    private void listenForResponses(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[1024];
        try {
            while(!mFoundUri) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String s = new String(packet.getData(), 0, packet.getLength());
                String[] data = s.split(",");

                if(data.length >= 2) {
                    if(data[0].equals("com.pytera.jukebox.appaddr")) {
                        mFoundUri = true;

                        mReceiver.updateFoundServers(new JukeboxServer(packet.getAddress(), Integer.parseInt(data[1])));
                    }
                }

                Log.d(TAG, "Received response " + s);
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = mWifi.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
