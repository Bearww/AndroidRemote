package com.test.wu.remotetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.content.SharedPreferences;

/**
 * Created by Wu on 2016/6/28.
 */
public class Touch extends Activity{

    private EditText ipField;
    private SeekBar sensitivity;
    private CheckBox useScreenCap;
    private EditText frameRate;

    public static final String PREFS_NAME 		        = "TouchSettings";

    public static final String IP_PREF 			        = "ip_pref";
    public static final String SENSITIVITY_PREF        = "sens_pref";

    public static final String USE_SCREEN_CAP_PREF 	= "screen_pref";
    public static final String FRAME_RATE_PREF 		= "rate_pref";


    /*********************************************************************************
        *Activity Lifecycle
        *********************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_setting);

        ipField = (EditText) findViewById(R.id.ipAddress);
        sensitivity = (SeekBar) findViewById(R.id.sensitivityBar);

        useScreenCap = (CheckBox) findViewById(R.id.checkBox1);
        frameRate = (EditText) findViewById(R.id.frameRate);

        // Set button listeners
        Button connectbutton = (Button) findViewById(R.id.btnConnect);
        connectbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectToServer();

                //Store used settings
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putInt(SENSITIVITY_PREF, sensitivity.getProgress());
                editor.putString(IP_PREF, ipField.getText().toString());

                editor.putInt(FRAME_RATE_PREF, Integer.parseInt( frameRate.getText().toString()) );
                editor.putBoolean(USE_SCREEN_CAP_PREF, useScreenCap.isChecked());

                editor.commit();
            }
        });

        Button disconnectbutton = (Button) findViewById(R.id.btnDisconnect);
        disconnectbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeConnectionToServer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

        String ip = prefs.getString(IP_PREF, "192.168.1.2");

        boolean useCap = prefs.getBoolean(USE_SCREEN_CAP_PREF, true);
        int framerate = prefs.getInt(FRAME_RATE_PREF, 10);

        int sens = prefs.getInt(SENSITIVITY_PREF, 0);

        ipField.setText(ip);
        sensitivity.setProgress(sens);

        frameRate.setText(framerate + "");
        useScreenCap.setChecked(useCap);

        AppDelegate appDel = ((AppDelegate)getApplicationContext());
        appDel.stopServer();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /***********************************************************************************
        *Network and Server Status Alerts
        ***********************************************************************************/

    private void networkUnreachableAlert() {
        AlertDialog network_alert = new AlertDialog.Builder(this).create();
        network_alert.setTitle("Network Unreachable");
        network_alert.setMessage("Your device is not connected to a network.");
        network_alert.setButton("Ok", new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        network_alert.show();
    }

    private void serverUnreachablealert() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Server Connection Unavailable");
        alert.setMessage("Please make sure the server is running on your computer");
        alert.setButton("Ok", new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alert.show();
    }



    /***********************************************************************************
        *Button Handlers used to connect to the server through the AppDelegate
        ***********************************************************************************/

    private void connectToServer() {
        AppDelegate appDel = ((AppDelegate)getApplicationContext());
        if(!appDel.canAccessNetwork()) {
            networkUnreachableAlert();
            return;
        }

        if(!appDel.connected()) {
            String serverIp = ipField.getText().toString();
            int serverPort = Constants.SERVER_PORT;
            int listenPort = Constants.LISTEN_PORT;
            int fps = Integer.parseInt(frameRate.getText().toString());

            appDel.createClientThread(serverIp, serverPort);

            if(useScreenCap.isChecked()) {
                appDel.createScreenCaptureThread(listenPort, fps);
            }
        }

        //TODO find better way to check for connection to the server
        for(int i = 0; i < 4; i++) {
            // every quarter second for one second check if the server is reachable
            if(appDel.connected()) {
                Intent controller = new Intent(Touch.this, RemoteController.class);
                controller.putExtra("sensitivity" , Math.round( sensitivity.getProgress() / 20) + 1);

                startActivity( controller );
                i = 6;
            }
            try {
                Thread.sleep(250);
            } catch(Exception e) {}
        }

        if(!appDel.connected())
            serverUnreachablealert();
    }

    private void closeConnectionToServer() {
        AppDelegate appDel = ((AppDelegate)getApplicationContext());
        appDel.stopServer();
    }
}