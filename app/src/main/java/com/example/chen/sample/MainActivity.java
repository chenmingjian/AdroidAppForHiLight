package com.example.chen.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    String TAG = "MainActivity";

    private float hsv[] = {0, 1, 0.6f};

    private int h_index = 360;

    FrameLayout frameLayout;

    ImageView imageView, imageView2;

    Start_Runnable start_runnable = new Start_Runnable();

    Handler handler = new Handler();

    WifiManager wifiManager;
 /*   Light light = new Light("192.168.4.1", 23333);
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            light.setStatic(new int[3]{255,255,255});
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);
        frameLayout = (FrameLayout) findViewById(R.id.FrameLayout);
        handler.postDelayed(start_runnable, 0);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE) ;
        Log.i(TAG, "onCreate: "+wifiManager.getDhcpInfo().ipAddress);
        findInLanThread.start();



    }

    //启动函数实现启动界面颜色的变化。
    class Start_Runnable implements Runnable{
        @Override
        public void run() {
            h_index %= 360;
            h_index += 4;
            hsv[0] = 360 - h_index;
            frameLayout.setBackgroundColor(Color.HSVToColor(hsv));
            if(h_index >= 360) {
                beginScale();
                //此处是第一个对话执行的位置
                discoverByUDPDialog();
                return;
            }
            handler.postDelayed(start_runnable,0);
        }
    }
    /*
    加载放大动画
     */
    private void beginScale(){
        imageView = (ImageView) findViewById(R.id.imageview0);
        Animation loadAnimation;
        loadAnimation = AnimationUtils.loadAnimation(this,R.anim.scale);
        imageView.startAnimation(loadAnimation);
        loadAnimation.setAnimationListener(new Animation.AnimationListener() {

                                               @Override
                                               public void onAnimationStart(Animation animation) {

                                               }

                                               @Override
                                               public void onAnimationEnd(Animation animation) {
                                                   frameLayout.setVisibility(View.GONE);
                                               }

                                               @Override
                                               public void onAnimationRepeat(Animation animation) {

                                               }});

    }
    /*
    获取定位权限
     */
    public void getPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    /*
    扫描Wifi名，查看是否有以AP模式存在的设备
     */
    public String[] ScanWifi(){
        int j = 0;
        WifiAdmin wifiAdmin = new WifiAdmin(MainActivity.this);
        wifiAdmin.OpenWifi();
        wifiAdmin.startScan();
        List<ScanResult> scanResultList;
        scanResultList = wifiAdmin.getWifiList();
        String[] SSIDArray = new String[scanResultList.size()];
        for(int i = 0; i < scanResultList.size(); i++){
            ScanResult scanResult = (ScanResult)scanResultList.get(i);
            String tmp = new String(scanResult.SSID);
            if(tmp.length()>7 && tmp.substring(0,7).equals(getString(R.string.app_name))) {
                System.out.println(tmp.substring(0,7));
                SSIDArray[j] = tmp;
                j++;
                System.out.println(j);
            }
        }
        String[]  SSIDArray2 = new String[j];
        for(int i =0 ; i < j ; i++)
        {
            SSIDArray2[i] = SSIDArray[i];
        }
        return SSIDArray2;
    }
    private List<LightDevice> Divices;
    Thread findInLanThread =  new Thread() {
      @Override
      public void run() {
          super.run();
          Divices = findInLan();
      }
    };

    public List<LightDevice> findInLan(){
        UDPBroadcast udpBroadcast = new UDPBroadcast(wifiManager, 25187);
        List<LightDevice> lightDevices = new ArrayList<LightDevice>();
        try {
            lightDevices = udpBroadcast.Send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lightDevices;
    }

    public void saveTheScanWifiResult() {
        SharedPreferences preferences = getSharedPreferences("user", MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String name = "1";
        editor.putString("Device", name);
        editor.commit();
    }
    String[] DeviceList;
    private Handler chooseDeviceHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(DeviceList.length != 0) {
                chooseDevice(DeviceList);
            }
            else {
                Toast.makeText(getApplicationContext(), "请检查设备已连接电源！！！", Toast.LENGTH_SHORT).show();
                scanDevice();
                //discoverByUDPDialog();
            }
        }
    };
    public void connectTheDevice(){
        getPermission();

        DeviceList = ScanWifi();
        System.out.println(DeviceList);
        chooseDeviceHandler.sendMessage(new Message());
        if(DeviceList.length != 0) {
            String passwd = PasswWd.stringMD5(DeviceList[0]);
            WifiAdmin wifiAdmin = new WifiAdmin(MainActivity.this);
            wifiManager.disconnect();
            wifiAdmin.Connect(DeviceList[0],passwd, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
            //List<LightDevice> lightDevices = findInLan();
            //String IP = lightDevices.get(0).getIp();//此处在多个设备时会出现bug
            //System.out.println(IP);
            WorkActivity.setLightIP("192.168.4.1");
        }
    }
    //弹出是否发现设备的对话对话框
    public void scanDevice(){
        new AlertDialog.Builder(this).setTitle("是否发现新设备？")
                .setCancelable(false)
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "正在寻找附近设备...", Toast.LENGTH_SHORT).show();
                    new Thread(){
                        @Override
                        public void run() {
                            connectTheDevice();
                        }
                    }.start();
                    }
                })
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“否”后的操作
                        MainActivity.this.finish();
                    }
                }).show();
    }
    public void starControlActivity(){
        while(WorkActivity.lightIP == null);

        Intent intent = new Intent(MainActivity.this, WorkActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    public void startConfig(){
        Intent intent = new Intent(MainActivity.this, Config.class);
        startActivity(intent);
    }

    public void ApOrStation( ){
        new AlertDialog.Builder(this).setTitle("是否配置连接路由器？")
                .setCancelable(false)
                .setNegativeButton("配置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startConfig();
                        MainActivity.this.finish();
                    }
                })
                .setPositiveButton("不使用路由器", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        starControlActivity();
                    }
                }).show();
    }
    //完成对设备的选择
    public void chooseDevice( final String[] Devices){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setIcon(R.drawable.ic_launcher);  //可以在这里添加一个灯泡的图标
        builder.setCancelable(false);
        builder.setTitle("请选择一个设备");
        builder.setItems(Devices, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
              //完成对Device[which]的选定与连接
                //starControltActivity();
                ApOrStation();
            }
        });
        builder.show();
    }
    //监听对返回键的操作
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         MainActivity.this.finish();
                    }
                })
                .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    String[] lightName;
    String[] lightIP;
    private void discoverByUDPDialog() {


        AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
        listDialog.setCancelable(false);
        if (Divices.size() == 0 ) {
            lightName = new String[]{ "未发现设备"};
            listDialog.setNegativeButton("查找设备", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击“确定”后的操作
                    scanDevice();
                    //MainActivity.this.finish();
                }
            });
            listDialog .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击“返回”后的操作,这里不设置没有任何操作
                    MainActivity.this.finish();
                }
            });
            listDialog.setTitle("发现设备列表：");
            listDialog.setItems(lightName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    discoverByUDPDialog();
                }
            });
        }
        else {
            lightName = new String[Divices.size()];
            lightIP = new String[Divices.size()];
            for(int i = 0 ; i< Divices.size(); i++){
                lightName[i] = Divices.get(i).getName();
                lightIP[i] = Divices.get(i).getIp();
            }
            listDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                }
            });
            /*listDialog .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击“返回”后的操作,这里不设置没有任何操作
                    MainActivity.this.finish();
                }
            });*/
            listDialog.setTitle("发现设备列表：");
            listDialog.setItems(lightName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setIPAndStartWork(which);
                }
            });
        }
        listDialog.show();
    }
    public void setIPAndStartWork(int which) {
        Log.i(TAG, "onClick: "+lightIP[which].toString());
        WorkActivity.setLightIP(lightIP[which].toString());
        starControlActivity();
    }

}

