package com.test.wu.remotetest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

/**
 * Created by Wu on 2016/6/27.
 */
public class ClientThread implements Runnable {

    private InetAddress serverAddr;
    private int serverPort;
    private DatagramSocket socket;
    byte[] buf = new byte[65000];

    public boolean connected = false;

    public ClientThread(String ip, int port) {
        try {
            serverAddr = InetAddress.getByName(ip);
        }
        catch (Exception e) {
            Log.e("ClientActivity", "C: Error", e);
        }
        serverPort = port;
    }

    //Opens the socket and output buffer to the remote server
    public void run() {
        try {
            socket = new DatagramSocket();
            //socket.setSoTimeout(1000);

            connected = true;

            connected = testConnection();
            if(connected)
                surveyConnection();
        }
        catch (Exception e) {
            Log.e("ClientThread", "Client Connection Error", e);
        }
    }

    public void sendMessage(String message) {
        try {
            buf = message.getBytes();
            DatagramPacket out = new DatagramPacket(buf, buf.length, serverAddr, serverPort);
            socket.send(out);
        }
        catch (Exception e) {
            closeSocketNoMessage();
        }
    }

    public void closeSocketNoMessage() {
        if(socket != null) {
            socket.close();
        }
        connected = false;
    }

    public void closeSocket() {
        sendMessage(new String("Close"));

        if(socket != null) {
            socket.close();
        }

        connected = false;
    }

    /***********************************************************************************
        *Test connection with server
        ***********************************************************************************/
    private boolean testConnection() {
        try {
            if(!connected)
                buf = new String("Connectivity").getBytes();
            else
                buf = new String("Connected").getBytes();

            DatagramPacket out = new DatagramPacket(buf, buf.length, serverAddr, serverPort);
            socket.send(out);
        }
        catch(Exception e) {
            return false;
        }

        try {
            DatagramPacket in = new DatagramPacket(buf, buf.length);
            socket.receive(in);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private void surveyConnection() {
        int count = 0;
        while(connected) {
            try {
                Thread.sleep(1000);
            } catch(Exception e) {}

            if(!testConnection())
                count++;
            else
                count = 0;

            // Keep connection alive 2 minutes
            if(count == 120) {
                closeSocket();
                return;
            }
        }
    }
}
