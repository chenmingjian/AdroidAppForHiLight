package com.example.chen.sample;

/**
 * Created by BBlackB on 2016/10/9.
 */
public class LightDevice {
    private String ip;
    private String ssid;

    public LightDevice(String ip, String ssid){
        this.ip = ip;
        this.ssid = ssid;
    }

    public String getIp(){ return ip;}

    public String getName(){return ssid;}
}


