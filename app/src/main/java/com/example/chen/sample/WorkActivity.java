package com.example.chen.sample;



import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

import static com.example.chen.sample.R.drawable.background_login;
import static com.example.chen.sample.R.drawable.color_tape;
import static com.example.chen.sample.R.drawable.shutdown;
import static com.example.chen.sample.R.drawable.sun;

public class WorkActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private FrameLayout frameLayout;

    private Handler changeColorHandler = new Handler();

    private float hsv[] = {0, 1, 0.8f}, h = 0f, h_Old, saveHsv[] = {0, 1, 0.8f}, s = 1, s_Old = 1, lum = 1;

    private int XonTouch, YonTouch, X_actionDown, Y_actionDown, shutDownFlag = 1, sunshineFlag = 1, flowFlag = 1, count = 10;

    private int changColorDelay = 50;

    private String ColorHexString;

    private URL url;

    private ImageButton imageButton2, imageButton3_back, imageButton5_shutdown, imageButton6_color, imageButton7_sunshine, imageButton8_flow,
            ib_caihong, ib_ranqing, ib_nuanri, ib_huxi, ib_zidingyi;

    private LinearLayout linearLayout1, linearLayout2;

    private Light light = new Light(lightIP, 23333);

    Drawable d;
    Button bt1 ,bt2;

    static String lightIP;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void setLightIP(String IP) {
        lightIP = IP;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Work Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.chen.sample/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Work Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.chen.sample/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onClick(View view) {

    }


    class LooperThread extends Thread {
        public Handler mHandler;

        public void run() {
            Looper.prepare();

            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    // process incoming messages here

                    int r, g, b;
                    int color = Color.HSVToColor(hsv);
                    r = Color.red(color);
                    g = Color.green(color);
                    b = Color.blue(color);

                    ColorHexString = Integer.toHexString(Color.rgb((int) (r * lum), (int) (g * lum), (int) (b * lum))).substring(2);
                    //light.postRequest("0x00" + ColorHexString, Light.STATIC);
                    long [] staticColor = {(int)(r * lum)*256*256 + (int) (g * lum)*256 + (int) (b * lum)};
//                    int [] staticColor = {Color.rgb((int) (r * lum), (int) (g * lum), (int) (b * lum))};

                    Log.i("cjm", "handleMessage: " + "r = " + Integer.toHexString(r) +"   g = " +Integer.toHexString(g) + "   b = " +Integer.toHexString(b));


                    try {
                        light.setStatic(staticColor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    if (sunshineFlag == 1) {
                        hsv = saveHsv.clone();
                        sunshineFlag = 0;
                    }
                }
            };
            Looper.loop();
        }
    }

    LooperThread looperThread = new LooperThread();

    private Runnable changeColor = new Runnable() {
        public void run() {
            h = (h_Old + (XonTouch - X_actionDown) / 4) % 360;
           /* s = (s_Old - (YonTouch - Y_actionDown) / 1000.0f);
            if (s < 0.2) s = 0.2f;
            if (s > 1) s = 1;
            lum = s;*/

            /*int r, g, b;
            int color = Color.HSVToColor(hsv);
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);*/

            if (h < 0) h = 360 + h;

            hsv[0] = h;
            hsv[2] = (float) (0.4f + 0.5 * lum);
            frameLayout.setBackgroundColor(Color.HSVToColor(hsv));
            if (!looperThread.mHandler.hasMessages(123)) {
                Message msg = new Message();
                msg.what = 123;
                looperThread.mHandler.sendMessage(msg);
            }
            changeColorHandler.postDelayed(this, changColorDelay);
        }
    };

    private Runnable changeColorLum = new Runnable(){

        @Override
        public void run() {
            s = (s_Old - (YonTouch - Y_actionDown) / 1000.0f);
            if (s < 0.2) s = 0.2f;
            if (s > 1) s = 1;
            lum = s;
            hsv[2] = (float) (0.4f + 0.5 * lum);
            frameLayout.setBackgroundColor(Color.HSVToColor(hsv));
            if (!looperThread.mHandler.hasMessages(123)) {
                Message msg = new Message();
                msg.what = 123;
                looperThread.mHandler.sendMessage(msg);
            }
            changeColorHandler.postDelayed(this, changColorDelay);
        }
    };
    //初始化URL
    private void initURL() {
/*        try {
            url = new URL("http://" + lightIP + ":23333/mode");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.FrameLayout0);
        frameLayout.setOnTouchListener(this);
        d = getResources().getDrawable(R.drawable.shutdown, null);
        linearLayout1 = (LinearLayout) findViewById(R.id.LinerLayout1);
        linearLayout2 = (LinearLayout) findViewById(R.id.LinerLayout2);
        imageButton3_back = (ImageButton) findViewById(R.id.imageButton3);
        imageButton3_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageButton5_shutdown = (ImageButton) findViewById(R.id.imageButton5);
        imageButton5_shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shutDownFlag == 1) {
                    shutDown();
                } else {
                    startUpColor();
                }
            }
        });
        imageButton6_color = (ImageButton) findViewById(R.id.imageButton6);
        imageButton6_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpColor();
            }
        });
        imageButton7_sunshine = (ImageButton) findViewById(R.id.imageButton7);
        imageButton7_sunshine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpSunshine();
            }
        });
        imageButton8_flow = (ImageButton) findViewById(R.id.imageButton8);
        imageButton8_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpFlow();
            }
        });
        ib_caihong = (ImageButton) findViewById(R.id.caihong);
        ib_caihong.setOnClickListener(new _onClickListener());
        ib_ranqing = (ImageButton) findViewById(R.id.ranqing);
        ib_ranqing .setOnClickListener(new _onClickListener());
        ib_nuanri = (ImageButton) findViewById(R.id.nuanri);
        ib_nuanri  .setOnClickListener(new _onClickListener());
        ib_huxi = (ImageButton) findViewById(R.id.huxi);
        ib_huxi .setOnClickListener(new _onClickListener());
        ib_zidingyi = (ImageButton) findViewById(R.id.zidingyi);
        ib_zidingyi .setOnClickListener(new _onClickListener());



        imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });
        initURL();
//        newAThreadHandler.post(newATreadRunnable);
        looperThread.start();
 /*       new Thread(){
            @Override
            public void run() {
                float _hsv[] = {0, 1, 0.4f};
                Color.colorToHSV(Color.rgb(253,245,230),_hsv);
                ColorHexString = Integer.toHexString(Color.HSVToColor(_hsv)).substring(2);
                Light.postRequest(url, "0x00" + ColorHexString, 1);
                frameLayout.setBackgroundColor(Color.rgb(255,230,129));
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                frameLayout.setOnTouchListener(null);
            }
        }.start();*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void sendMessageToLooperThread() {
        Message msg = new Message();
        msg.what = 123;
        looperThread.mHandler.sendMessage(msg);
    }

    public void shutDown() {
        if (flowFlag == 0) {
            buttonGroupGONE();
        }
        hsv[2] = 0f;
        sendMessageToLooperThread();
        frameLayout.setBackground(d);
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        frameLayout.setOnTouchListener(null);
        shutDownFlag = 0;
        handler.removeCallbacks(start_runnable);
    }

    public void startUpColor() {
        if (flowFlag == 0) {
            buttonGroupGONE();
        }
        hsv[2] = 0.4f;
        sendMessageToLooperThread();
        frameLayout.setBackgroundColor(Color.HSVToColor(hsv));
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.VISIBLE);
        frameLayout.setOnTouchListener(WorkActivity.this);
        handler.removeCallbacks(start_runnable);
        if (shutDownFlag == 0) {
        }
        shutDownFlag = 1;

    }

    public void startUpSunshine() {
        if (flowFlag == 0) {
            buttonGroupGONE();
        }
        saveHsv = hsv.clone();
        Color.colorToHSV(Color.rgb(253, 245, 230), hsv);
        sendMessageToLooperThread();
        sunshineFlag = 1;
        frameLayout.setBackgroundColor(Color.rgb(255, 230, 100));
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.GONE);
        frameLayout.setOnTouchListener(null);
        handler.removeCallbacks(start_runnable);
        if (shutDownFlag == 0) {
        }
        shutDownFlag = 1;
    }

    public void startUpFlow() {

        hsv[2] = 0.4f;
        if (flowFlag == 1) {
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            frameLayout.setOnTouchListener(null);
            buttonGroupVISIVBLE();
        } else {
            buttonGroupGONE();
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            frameLayout.setOnTouchListener(null);
        }
        shutDownFlag = 1;

    }

    public void buttonGroupVISIVBLE() {
        ib_caihong.setVisibility(View.VISIBLE);
        ib_ranqing.setVisibility(View.VISIBLE);
        ib_nuanri.setVisibility(View.VISIBLE);
        ib_huxi.setVisibility(View.VISIBLE);
        ib_zidingyi.setVisibility(View.VISIBLE);
        flowFlag = 0;
    }

    public void buttonGroupGONE() {
        ib_caihong.setVisibility(View.GONE);
        ib_ranqing.setVisibility(View.GONE);
        ib_nuanri.setVisibility(View.GONE);
        ib_huxi.setVisibility(View.GONE);
        ib_zidingyi.setVisibility(View.GONE);
        flowFlag = 1;
    }

    long color[] = new long[3];
    int ms;
    public class _onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.caihong:{
                    color[0] = Color.RED|0x2020&0xffffff;
                    color[1] = Color.GREEN&0xffffff|0x200020;
                    color[2] = Color.BLUE&0xffffff|0x202000;
                    ms = 900 ;
                    speed = 4;
                    handler.removeCallbacks(start_runnable);
                }break;
                case R.id.nuanri:{
                    color[0] = Color.rgb(127,255,127)&0xffffff;
                    color[1] = Color.rgb(255,255,32)&0xffffff;
                    color[2] = Color.rgb(210,105,32)&0xffffff;
                    ms = 1000 ;
                    speed = 2;
                    handler.removeCallbacks(start_runnable);
                }break;
                case R.id.ranqing:{
                    color[0] = Color.rgb(255,32,32)&0xffffff;
                    color[1] = Color.rgb(128,32,128)&0xffffff;
                    color[2] = Color.rgb(255,32,147)&0xffffff;
                    ms = 1500 ;
                    speed = 2;
                    handler.removeCallbacks(start_runnable);
                }break;
                case R.id.huxi:{

                    color[0] = Color.rgb(200,200,100)&0xffffff;
                    color[1] = Color.rgb(20,20,20)&0xffffff;
                    color[2] = Color.rgb(255,100,100)&0xffffff;
                    ms = 2000; ;
                    speed = 1;
                    handler.removeCallbacks(start_runnable);
                }break;
                case R.id.zidingyi:{
                    Intent intent = new Intent(WorkActivity.this,getFlowSet.class);
                    intent.putExtra("IP",lightIP);
                    startActivityForResult(intent,1);
                }
            }
            handler.postDelayed(start_runnable, 0);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        light.setFlow(ms , color);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            buttonGroupGONE();
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
        }
    }
  //  int r, g, b;
    public void  changeBGfromColorToColor(/*int color1,int  color2*/){
/*        float dr = (Color.red(color1) - Color.red(color2))/1000f;
        float dg = (Color.green(color1) - Color.green(color2))/1000f;
        float db = (Color.blue(color1) - Color.blue(color2))/1000f;

        int color = Color.HSVToColor(hsv);
        r = Color.red(color);
        g = Color.green(color);
        b = Color.blue(color);
        r += dr;
        g += dg;
        b +=db;
        frameLayout.setBackgroundColor(Color.rgb(r,g,b));*/
    }
    int speed = 4;
    Handler handler = new Handler();
    private int h_index = 360;
    class Start_Runnable implements Runnable{
        @Override
        public void run() {
            h_index %= 360;
            h_index += speed;
            hsv[0] = 360 - h_index;
            frameLayout.setBackgroundColor(Color.HSVToColor(hsv));
            handler.postDelayed(start_runnable,0);
        }
    }

    public Start_Runnable start_runnable = new Start_Runnable();

    int flag = 0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        XonTouch = (int) event.getRawX();
        YonTouch = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if(flag ==0) {
                    if (isHLMove(X_actionDown, Y_actionDown, XonTouch, YonTouch) != 0) {
                        flag = 1;

                    }
                    if (isLRMove(X_actionDown, Y_actionDown, XonTouch, YonTouch) != 0) {
                        flag = 2;
                    }
                }
                if(flag == 1) {
                    changeColorHandler.postDelayed(changeColorLum, changColorDelay);
                }
                if(flag == 2){
                    changeColorHandler.postDelayed(changeColor, changColorDelay);
                }
                break;

            case MotionEvent.ACTION_UP:
                h_Old = h;
                s_Old = s;
                changeColorHandler.removeCallbacks(changeColor);
                changeColorHandler.removeCallbacks(changeColorLum);
                flag = 0;
                break;

            case MotionEvent.ACTION_DOWN:
                X_actionDown = XonTouch;
                Y_actionDown = YonTouch;
                break;
        }
        return true;
    }

    private double distance(int x1, int y1, int x2,int y2){
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1-y2), 2));
    }

    private  int  isLRMove(int x1,int y1,int x2, int y2){
        if(distance( x1, y1,x2,y2) > 100 ){
            if(Math.abs(x1-x2)>=Math.abs(y1-y2)){
                return x1-x2;
            }
        }
        return 0;
    }

    private  int isHLMove(int x1,int y1,int x2, int y2){
        if(distance( x1, y1,x2,y2) > 100 ){
            if(Math.abs(x1-x2)<=Math.abs(y1-y2)){
                return y1-y2;
            }
        }
        return 0;
    }
    //监听返回键
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WorkActivity.this.finish();
                    }
                })
                .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    int   userSetMS = 1000;
    Runnable setedColorRunnable = new Runnable() {
        @Override
        public void run() {
            setedColor = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                setedColor[i] = (long) (list.get(i) & 0xffffff);
            }
            try {
                light.setFlow(userSetMS, setedColor);
                handler.postDelayed(setedColorRunnable,500);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    List<Integer> list;
    long [] setedColor;

    @Override
    protected void onResume() {
        super.onResume();
        myapp app = ((myapp)getApplicationContext());
        list = app.getlist();
        if(list != null){
            new Thread(setedColorRunnable).start();
        }
    }


    int hours =0 , mins=0;
    Calendar c = Calendar.getInstance();
    private PopupWindow mPopWindow;
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(WorkActivity.this).inflate(R.layout.popup, null);
        mPopWindow = new PopupWindow(contentView,
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
        bt1 = (Button) contentView.findViewById(R.id.bt1);
        bt2 = (Button) contentView.findViewById(R.id.bt2);
        bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new TimePickerDialog(WorkActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hours = i;
                        mins =i1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int s = (((hours - c.get(Calendar.HOUR_OF_DAY))*60+mins-c.get(Calendar.MINUTE))*60-c.get(Calendar.SECOND))*1000;
                                Log.i("123", "run: "+s);
                                try {
                                    light.setTimer(Light.STATIC,new long[]{0x0},s);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                },1,1,true).show();
             }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(WorkActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hours = i;
                        mins =i1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int s = (((hours - c.get(Calendar.HOUR_OF_DAY))*60+mins-c.get(Calendar.MINUTE))*60-c.get(Calendar.SECOND))*1000;
                                Log.i("123", "run: "+s);
                                try {
                                    //light.setTimer(Light.STATIC,new long[]{0xfffffff},s);
                                    light.setTimerMode(60000,new long[]{0xfffffff},s);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                },1,1,true).show();

            }
        });
                //显示PopupWindow
        mPopWindow.setAnimationStyle(R.style.AnimationFade);

        View rootview = LayoutInflater.from(WorkActivity.this).inflate(R.layout.activity_main, null);
        //mPopWindow.showAtLocation(rootview, Gravity.RIGHT|Gravity.TOP, 50, 230);
        mPopWindow.showAsDropDown(imageButton2);
        bt1.setBackgroundColor(Color.HSVToColor(hsv));
        bt2.setBackgroundColor(Color.HSVToColor(hsv));

    }


}