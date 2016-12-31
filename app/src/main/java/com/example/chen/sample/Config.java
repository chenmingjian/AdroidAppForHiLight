package com.example.chen.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.chen.sample.R.id.editText;

/**
 * Created by chen on 2016/11/30.
 */
public class Config extends AppCompatActivity {
    String TAG = "Config";
    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private EditText et;
    private Button bt,bt2 ;
    ScanResult scanResult;
    private FrameLayout frameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        wifiAdimin = new WifiAdmin(this);
        data_list = new ArrayList<String>();
        WifiAdmin wifiAdmin = new WifiAdmin(Config.this);
        wifiAdmin.OpenWifi();
        wifiAdmin.startScan();
        List<ScanResult> scanResultList;
        scanResultList = wifiAdmin.getWifiList();
        String[] SSIDArray = new String[scanResultList.size()];
        for (int i = 0; i < scanResultList.size(); i++) {
            scanResult = (ScanResult) scanResultList.get(i);
            data_list.add(scanResult.SSID.toString());
        }

        bt = (Button) findViewById(R.id.bt_ture);
        bt2 = (Button) findViewById(R.id.bt_fault);
        et = (EditText) findViewById(R.id.editText);
        frameLayout = (FrameLayout) findViewById(R.id.hiden);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = et.getText().toString();
                setPassword(password);
                if ((targerSSID != null) && (targerPassword != null)) {
                    connectTargerWIFI();
                } else {
                    Toast.makeText(Config.this, "无法连接此无线网络", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.this.finish();
            }
        });
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSSID(arr_adapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    String targerPassword = null;

    public void setPassword(String password) {
        targerPassword = password;

    }

    String targerSSID = null;

    public void setSSID(String SSID) {
        targerSSID = SSID;
    }

    WifiAdmin wifiAdimin ;

    public void connectTargerWIFI()  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Light light = new Light("192.168.4.1", 23333);
                light.changeWIFIModeToSTA(targerSSID, targerPassword);
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.post(start_runnable);
        wifiAdimin.OpenWifi();
        wifiAdimin.wifiManager.disconnect();
        wifiAdimin.Connect(targerSSID, targerPassword, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
        findInLanThread.start();
        frameLayout.setVisibility(View.VISIBLE);

    }

    private List<LightDevice> Divices;

    Thread findInLanThread =  new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Divices = findInLan();
        }
    };

    public List<LightDevice> findInLan(){
        UDPBroadcast udpBroadcast = new UDPBroadcast(wifiAdimin.wifiManager, 25187);
        List<LightDevice> lightDevices = new ArrayList<LightDevice>();
        try {
            lightDevices = udpBroadcast.Send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(lightDevices.size() == 0) {
            findInLanThread.start();
            return null;
        }
        WorkActivity.setLightIP(lightDevices.get(0).getIp());
        Intent intent = new Intent(Config.this, WorkActivity.class);
        startActivity(intent);
        Config.this.finish();
        return lightDevices;
    }


    Handler handler = new Handler();

    private float hsv[] = {0, 1, 0.6f};

    private int h_index = 360;

    Runnable start_runnable = new Runnable() {
        @Override
        public void run() {
        h_index %= 360;
        h_index += 1;
        hsv[0] = 360 - h_index;
        frameLayout.setBackgroundColor(Color.HSVToColor(hsv));
        if(h_index >= 360) {
            h_index -= 360;
        }
        handler.postDelayed(start_runnable,0);
        }
    };
}
