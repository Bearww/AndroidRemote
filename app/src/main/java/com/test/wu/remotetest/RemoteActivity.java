package com.test.wu.remotetest;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class RemoteActivity extends ActionBarActivity implements View.OnTouchListener, View.OnKeyListener {

    Context context;
    Button leftButton;
    Button rightButton;
    TextView mousePad;

    private boolean isConnected = false;
    private boolean mouseMoved = false;
    private boolean displayKeyboard = false;
    private Socket socket;
    private PrintWriter out;

    private int mouse_sensitivity = 1;
    private float screenRatio = 1.0f;

    private float initX = 0;
    private float initY = 0;
    private float disX = 0;
    private float disY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        // Save the context to show Toast messages
        context = this;

        // Get references of all buttons
        leftButton = (Button)findViewById(R.id.leftButton);
        rightButton = (Button)findViewById(R.id.rightButton);

        // This activity extends View.OnTouchListener, set this as onTouchListener for all buttons
        leftButton.setOnTouchListener(this);
        rightButton.setOnTouchListener(this);

        // Get reference to the EditText acting as editText
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            public void  afterTextChanged (Editable s) {
                sendMessage(Constants.KEYBOARD + s.toString());
                s.clear();
            }

            public void  beforeTextChanged  (CharSequence s, int start, int count, int after) {
            }

            public void  onTextChanged  (CharSequence s, int start, int before, int count) {
            }
        });

        // Get reference to the TextView acting as mousepad
        mousePad = (TextView)findViewById(R.id.mousePad);

        // Capture finger taps and movement on the textview
        mousePad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isConnected && out != null) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Save X and Y positions when user touches the TextView
                            initX = event.getX();
                            initY = event.getY();
                            mouseMoved = false;
                        break;
                        case MotionEvent.ACTION_MOVE:
                            disX = event.getX()- initX; // Mouse movement in x direction
                            disY = event.getY()- initY; // Mouse movement in y direction

                            /* Set init to new position so that continuous mouse movement is captured */
                            initX = event.getX();
                            initY = event.getY();
                            if(disX != 0 || disY != 0) {
                                out.println(disX + "," + disY); //send mouse movement to server
                            }
                            mouseMoved = true;
                        break;
                        case MotionEvent.ACTION_UP:
                            // Consider a tap only if usr did not move mouse after ACTION_DOWN
                            if(!mouseMoved) {
                                out.println(Constants.MOUSE_LEFT_CLICK);
                            }
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void setImageRequestSizes() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        int width, height;
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        //ClientListener.DeviceWidth = (int)(screenRatio * width);
        //ClientListener.DeviceHeight = (int)(screenRatio * height);
        //Log.e("REQUESTINGSIZE", screenRatio + " " + ClientListener.DeviceWidth + " " + ClientListener.DeviceHeight);
    }

    private void sendMessage(String message) {
        if (isConnected && out != null) {
            // Send message to server
            out.println(message);
        }
    }

    private void sendMessage(char c) {
        sendMessage("" + c);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        setImageRequestSizes();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Noinspection SimplifiableIfStatement
        if(id == R.id.action_connect) {
            ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
            connectPhoneTask.execute(Constants.SERVER_IP); //try to connect to server in another thread
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v == leftButton) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:   sendMessage(Constants.LEFTMOUSEDOWN);    break;
                case MotionEvent.ACTION_UP:       sendMessage(Constants.LEFTMOUSEUP);      break;
            }
        }else if(v == rightButton) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:   sendMessage(Constants.RIGHTMOUSEDOWN);    break;
                case MotionEvent.ACTION_UP:       sendMessage(Constants.RIGHTMOUSEUP);      break;
            }
        }
        else
            mousePadHandler(event);

        return true;
    }

    // send a mouse message
    private void mousePadHandler(MotionEvent event) {
        int action = event.getAction();
        int touchCount = event.getPointerCount();

        // if a single touch
        if(touchCount == 1) {
            switch(action) {
                case 0:	// touch down
                    initX = event.getX();
                    initY = event.getY();
                    break;

                case 1:	// touch up
                    long deltaTime = event.getEventTime() - event.getDownTime();
                    if(deltaTime < 250)
                        sendMessage(Constants.LEFTCLICK);
                    break;

                case 2: // moved
                    float deltaX = (initX - event.getX()) * -1;
                    float deltaY = (initY - event.getY()) * -1;

                    sendMessage(Constants.createMoveMouseMessage(deltaX * mouse_sensitivity
                            , deltaY * mouse_sensitivity));

                    initX = event.getX();
                    initY = event.getY();
                    break;

                default: break;
            }
        }

        // if two touches send scroll message
        // based off MAC osx multi touch scrolls up and down
        else if(touchCount == 2) {
            if(action == 2) {
                float deltaY = event.getY() - initY;
                float tolerance = 10;

                if (deltaY > tolerance) {
                    sendMessage(Constants.SCROLLUP);
                    initY = event.getY();
                }
                else if(deltaY < -1 * tolerance) {
                    sendMessage(Constants.SCROLLDOWN);
                    initY = event.getY();
                }
            }
            else
                initY = event.getY();
        }
    }

    // Detect keyboard event, and send message
    @Override
    public boolean onKey(View v, int c, KeyEvent event) {
        // c is the event keycode
        if(event.getAction() == 1) {
            sendMessage("" + Constants.KEYCODE + c);
        }
        // This will prevent the focus from moving off the text field
        return c == KeyEvent.KEYCODE_DPAD_UP ||
                c == KeyEvent.KEYCODE_DPAD_DOWN ||
                c == KeyEvent.KEYCODE_DPAD_LEFT ||
                c == KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    // Show and hide Keyboard by setting the
    // focus on a hidden text field
    public void keyClickHandler(View v) {
        EditText editText = (EditText) findViewById(R.id.editText);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(displayKeyboard) {
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            displayKeyboard = false;
        }
        else {
            mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            displayKeyboard = true;
        }
    }

    public void setImage(final Bitmap bit) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                View layout = findViewById(R.id.mousePad);
                BitmapDrawable drawable = new BitmapDrawable(bit);
                layout.setBackgroundDrawable(drawable);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isConnected && out != null) {
            try {
                out.println("exit"); //tell server to exit
                socket.close(); //close socket
            } catch (IOException e) {
                Log.e("remotedroid", "Error in closing socket", e);
            }
        }
    }

    public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                InetAddress serverAddr = InetAddress.getByName(params[0]);
                Log.d("remotedroid", serverAddr.toString());
                socket = new Socket(serverAddr, Constants.SERVER_PORT); // Open socket on server IP and port
            } catch (IOException e) {
                Log.e("remotedroid", "Error while connecting", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            isConnected = result;
            Toast.makeText(context,isConnected ? "Connected to server!" : "Error while connecting", Toast.LENGTH_LONG).show();
            try {
                if(isConnected) {
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                            .getOutputStream())), true); //create output stream to send data to server
                }
            } catch (IOException e){
                Log.e("remotedroid", "Error while creating OutWriter", e);
                Toast.makeText(context,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }
}
