package com.test.wu.remotetest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;

import static com.test.wu.remotetest.LinkCloud.request;

public class TabFragment3 extends Fragment {

    //擷取畫面按鈕
    private Button screenShot;
    //截圖的畫面
    private ImageView screenImage;

    private Socket socket;
    private PrintWriter out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        //取得Button與ImageView元件
        screenShot = (Button) view.findViewById(R.id.ScreenShot);
        screenImage = (ImageView) view.findViewById(R.id.ScreenImage);

        try {
            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_RECVIMAGE); // Open socket on server IP and port

            Timer timer = new Timer();
            int frames = 1000 / framesPerSecond;

            timer.scheduleAtFixedRate(getImageTask, 0, frames);
        } catch (Exception e) {
            Log.e("ClientActivity", "Client Connection Error", e);
            isConnected = false;
        }

        //點擊按鈕觸發
        screenShot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //將截圖Bitmap放入ImageView
                screenImage.setImageBitmap(getScreenShot());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Msg", request("index.php").toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        return view;
    }

    //將全螢幕畫面轉換成Bitmap
    private Bitmap getScreenShot()
    {
/*
        //藉由View來Cache全螢幕畫面後放入Bitmap
        View view = getActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap fullBitmap = view.getDrawingCache();

        //取得系統狀態列高度
        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        //取得手機螢幕長寬尺寸
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int phoneWidth = size.x;
        int phoneHeight = size.y;

        //將狀態列的部分移除並建立新的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(fullBitmap, 0, statusBarHeight, phoneWidth, phoneHeight - statusBarHeight);
        //將Cache的畫面清除
        view.destroyDrawingCache();
*/
        screenImage.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenImage.getDrawingCache());

        return bitmap;
    }
}
