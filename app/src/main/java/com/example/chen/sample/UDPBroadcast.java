package com.example.chen.sample;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BBlackB on 2016/10/9.
 */
public class UDPBroadcast {
    private int serverPort;
    private WifiManager wifiManager;
    private MulticastSocket multicastSocket;
    private static final int RESPONSE_DEVICE_MAX = 3;
    private static final int TIME_OUT = 200;

    public UDPBroadcast(WifiManager wifiManager,int serverPort){
        this.serverPort = serverPort;
        this.wifiManager = wifiManager;
    }


   public List<LightDevice> Send(byte[] data) throws IOException {
        byte[] revData = new byte[1024];
        DatagramPacket revDataPacket = new DatagramPacket(revData, revData.length);
        List<LightDevice> list = new ArrayList<LightDevice>();
        multicastSocket = new MulticastSocket();
        InetAddress inetAddress = getBroadcastAddress();
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length,getBroadcastAddress(), serverPort);
        multicastSocket.send(datagramPacket);
       for (int i = 0; i < RESPONSE_DEVICE_MAX; i++){
           try{
               multicastSocket.receive(revDataPacket);
               String revIp = revDataPacket.getAddress().toString().substring(1);
               String revStr = new String(revDataPacket.getData(), revDataPacket.getOffset(), revDataPacket.getLength());
               String name = revStr.substring(24, 55);
               LightDevice lightDevice = new LightDevice(revIp, name);
               if (list.indexOf(lightDevice) == -1)
                   list.add(lightDevice);
               System.out.println("UDP recevied!");
           }catch (Exception e){
               e.printStackTrace();
               System.out.println("UDP receive time out!");
           }
       }
       return list;
    }

    public List<LightDevice> Send() throws IOException {
        byte[] revData = new byte[1024];
        DatagramPacket revDataPacket = new DatagramPacket(revData, revData.length);
        List<LightDevice> list = new ArrayList<LightDevice>();
        multicastSocket = new MulticastSocket();
        InetAddress inetAddress = getBroadcastAddress();
        System.out.println("broadcast: " + inetAddress);
        DatagramPacket datagramPacket = getData();
        multicastSocket.setSoTimeout(TIME_OUT);
        multicastSocket.send(datagramPacket);
        for (int i = 0; i < RESPONSE_DEVICE_MAX; i++){
            try{
                multicastSocket.receive(revDataPacket);
                String revIp = revDataPacket.getAddress().toString().substring(1);
                System.out.println(revIp);
                String revStr = new String(revDataPacket.getData(), revDataPacket.getOffset(), revDataPacket.getLength());
                String name = revStr.substring(24, 55);
                LightDevice lightDevice = new LightDevice(revIp, name);
                if (list.indexOf(lightDevice) == -1)
                    list.add(lightDevice);
            }catch (Exception e){
               // e.printStackTrace();
                System.out.println("UDP receive time out!");
            }
        }
        return list;
    }

    private DatagramPacket getData() throws IOException {
        byte[] data = {
                0x44, 0x49, 0x53, 0x43, 0x4F, 0x56, 0x45, 0x52, 0x4C, 0x69, 0x67, 0x68, 0x74, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };
        System.out.println("broadcast is " + getBroadcastAddress());
        InetAddress inetAddress = getBroadcastAddress();

        return new DatagramPacket(data, data.length, inetAddress, serverPort);
    }

    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo myDhcpInfo = wifiManager.getDhcpInfo();
        if (myDhcpInfo == null) {
            System.out.println("Could not get broadcast address");
            return null;
        }

        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
    public WifiManager getWifiManager(){
        return wifiManager;
    }
}
