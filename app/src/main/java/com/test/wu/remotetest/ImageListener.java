package com.test.wu.remotetest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
public class ImageListener implements Runnable {

    private RemoteActivity main;
    private InetAddress serverAddr;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private DataInputStream in;
    byte[] buf = new byte[65000];
    private int framesPerSecond = 1;
    public boolean isConnected = false;

    public static int DeviceWidth = 100;
    public static int DeviceHeight = 100;

    public ImageListener(int port, int fps, RemoteActivity activity) {
        framesPerSecond = fps;
        main = activity;

        try {
            serverAddr = InetAddress.getByName(Constants.SERVER_IP);
        } catch (Exception e) {
            Log.e("ClientListener", "C: Error", e);
        }
        serverPort = port;
    }

    public void run() {
        try {
            isConnected = true;
            socket = new Socket(serverAddr, serverPort); // Open socket on server IP and port

            Timer timer = new Timer();
            int frames = 5000 / framesPerSecond;

            timer.scheduleAtFixedRate(getImageTask, new Date(), frames);
        } catch (Exception e) {
            Log.e("ClientActivity", "Client Connection Error", e);
            isConnected = false;
        }

        try {
            if (isConnected) {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                        .getOutputStream())), true); // Create output stream to send data to server
                in = new DataInputStream(socket.getInputStream());

                listen();
            }
        } catch (IOException e) {
            Log.e("remotedroid", "Error while creating OutWriter", e);
        }
    }

    private TimerTask getImageTask = new TimerTask() {
        @Override
        public void run() {
            String message = new String("" +
                    Constants.REQUESTIMAGE +
                    Constants.DELIMITER +
                    DeviceWidth +
                    Constants.DELIMITER +
                    DeviceHeight);

            sendMessage(message);
        }
    };

    private void sendMessage(String message) {
        if (isConnected && out != null) {
            // Send message to server
            out.println(message);
        }
    }

    private void listen() {
        while (isConnected) {
            try {
                in.readFully(buf);
                //Bitmap bm = BitmapFactory.decodeByteArray(buf, 0, buf.length);
                String data = new String(buf);
                main.leftButton.setText(data);
                Toast.makeText(main, "" + buf.length, Toast.LENGTH_LONG).show();
                //Log.e("REQUESTINGSIZE", "SIZERECV: " + bm.getWidth() + bm.getHeight());
                //main.setImage(bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}