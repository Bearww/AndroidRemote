package com.test.wu.remotetest;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Wu on 2016/6/27.
 */
public class AppDelegate extends Application {

    private ClientThread client;
    private ClientListener listener;
    private RemoteController controller;

    public void onCreate() {
        super.onCreate();
    }

    public void setController(RemoteController c) {
        controller = c;
    }

    public RemoteController getController() {
        return controller;
    }

    /***********************************************************************************
        *Connect to server
        ***********************************************************************************/

    public void createClientThread(String ipAddress, int port) {

        client = new ClientThread(ipAddress, port);

        Thread cThread = new Thread(client);
        cThread.start();
    }

    public void createScreenCaptureThread(int listenerPort, int fps) {
        listener = new ClientListener(listenerPort, fps, this);

        Thread cThread = new Thread(listener);
        cThread.start();
    }

    public void sendMessage(String message) {
        if(client != null)
            client.sendMessage(message);
    }

    public void stopServer() {
        if(client != null && client.connected) {
            client.closeSocket();
        }
        client = null;

        if(listener != null) {
            listener.closeSocket();
        }
        listener = null;
    }

    /***********************************************************************************
        *Test connection
        ***********************************************************************************/
    public boolean connected() {
        if(client != null)
            return client.connected;

        return false;
    }

    public boolean canAccessNetwork() {
        final ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifi.isAvailable())
            return true;

		/*
		 * For now the mobile connectivity does not matter
		 * the scale of this project is local wireless networks
		 *
		android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(mobile.isAvailable())
			return true;
		*/

        return false;
    }
}
