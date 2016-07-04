package com.test.wu.remotetest;

import android.graphics.Point;

/**
 * Created by Wu on 2016/6/25.
 */
public class Constants {
    //public static final String SERVER_IP = "169.254.156.204";
    public static final String SERVER_IP = "192.168.137.74";
    //public static final String SERVER_IP = "192.168.137.1";
    public static final int SERVER_PORT = 6060;
    public static final int LISTEN_PORT = 6080;

    public static final int FRAMES_PER_SECOND = 10;

    public static final String MOUSE_LEFT_CLICK = "left_click";

    public static final char LEFTMOUSEDOWN = 'a';
    public static final char LEFTMOUSEUP = 'b';

    public static final char RIGHTMOUSEDOWN = 'c';
    public static final char RIGHTMOUSEUP = 'd';

    public static final char LEFTCLICK = 'e';

    public static final char SCROLLUP = 'h';
    public static final char SCROLLDOWN = 'i';

    public static final char KEYBOARD = 'k';
    public static final char KEYCODE = 'l';

    public static final char DELIMITER = '/';

    public static final char MOVEMOUSE = 'p';

    public static final char REQUESTIMAGE = 'I';

    /*
        *  Returns a string in the format that can be laster parsed
        *  format: MOVEMOUSEintxDELIMITERinty
        *  ex: 	p5/6
        */
    public static String createMoveMouseMessage(float x, float y) {
        int intx = Math.round(x);
        int inty = Math.round(y);
        return "" + MOVEMOUSE + intx + DELIMITER + inty;
        //return intx + "," + inty;
    }

    public static Point parseMoveMouseMessage(String message) {
        String[] tokens = message.substring(1).split("" + Constants.DELIMITER);
        return new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
    }
}
