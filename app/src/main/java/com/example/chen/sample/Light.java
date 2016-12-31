package com.example.chen.sample;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Administrator on 2016/9/22.
 */
public class Light {
    private URL _url;
    private int _port;
    private boolean WIFIMode;

    public final boolean AP = true;
    public final boolean STA = false;

    public final static int OFF = 0;
    public final static int STATIC = 1;
    public final static int FLOW = 2;
    public final static int FLOW_DEFAULT_INTERVAL = 1000;
    public final static int STATIC_DEFAULT_INTERVAL = 0;
    final int ERROR = -1;

    final String TAG = "TAG";


    public void setLight()
    {
        try {
            final URL SERVER_URL =  new URL("http://192.168.222.223/light");
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }

    }

    public Light(String host, int port){
        String tmpurl = "http://" + host + ":" + port;
        try {
            this._url = new URL(tmpurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this._port = port;
        this.WIFIMode = AP;
    }

    public Light(String host, int port, boolean wifimode){
        String tmpurl = "http://" + host + ":" + port;
        try {
            this._url = new URL(tmpurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this._port = port;
        this.WIFIMode = wifimode;
    }

    public void setLight(String host, int port) throws MalformedURLException {
        String tmpUrl = "http://" + host + ":" + port;
        this._url = new URL(tmpUrl);
        _port = port;
    }

    public void setWIFIMode(boolean wifiMode){
        this.WIFIMode = wifiMode;
    }

    public boolean getWIFIMode(){
        return this.WIFIMode;
    }

    private JSONArray prepareJsonArray(String val){
        String[] strArr = null;
        strArr  = val.split(" ");
        JSONArray jsonArray = new JSONArray();

        int[] intArr = null;
        intArr = new int[strArr.length];

        for (int i = 0; i < strArr.length; i++){
            intArr[i] = Integer.parseInt(strArr[i].replace("0x", ""), 16);
        }

        for (int i = 0; i < strArr.length; i++){
            try {
                jsonArray.put(i, intArr[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private JSONArray prepareJsonArray(int mSec, long[] color) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0,mSec);
        for (int i = 0; i < color.length; i++){
            jsonArray.put(i+1, color[i]);
        }
        return jsonArray;
    }

    /*
    * prepareJsonString for Timer
    * jsonArray:颜色RGB数组
    * mode:选择模式
    * return:返回发送的json字符串
    * */
    private String prepareJsonString(JSONArray jsonArray, int mode){
        JSONStringer jsonStringer = new JSONStringer();
        String strMode;
        switch (mode)
        {
            case STATIC: strMode = "static"; break;
            case FLOW: strMode = "flow"; break;
            default: strMode = "error";
        }

        try {
            jsonStringer.object();
            jsonStringer.key("params");
            jsonStringer.value(jsonArray);
            jsonStringer.key("mode");
            jsonStringer.value(strMode);
            jsonStringer.endObject();
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return jsonStringer.toString();
    }

    /*
    * postJson
    * url:发送地址
    * jsonString:发送的json字符串
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    private String postJson(URL url, String jsonString){
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        Log.i(TAG, "postJson: " + url.toString());
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
//            urlConnection.setRequestProperty("Accept","application/json");
//            urlConnection.setRequestProperty("charset","utf-8");

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonString);
            dataOutputStream.flush();
            dataOutputStream.close();
            int statusCode = urlConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == statusCode)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = inputStream2String(inputStream);
                return response;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private  String inputStream2String(InputStream is){
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null){
                stringBuffer.append(line);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    private String postRequest(String strColor, int mode){
        if (mode == OFF) {
            strColor = "0 0";
            mode = STATIC;
        }
        else{
            strColor = "0 " + strColor;
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray = prepareJsonArray(strColor);
        //prepare json string
        String strJson = null;
        strJson = prepareJsonString(jsonArray, mode);
        System.out.println(strJson);
        //post http request
        String res = postJson(_url, strJson);
        // System.out.println("response is " + res);
        return res;
    }


    /*
    * setMode
    * mode:选择模式
    * color:设置的颜色
    * mSec:流光间隔
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    private String setMode(int mode, int mSec, long[] color) throws JSONException {
        JSONArray jsonArray;
        String strJson;
        jsonArray = prepareJsonArray(mSec,color);
        if (mode == STATIC)
            strJson = prepareJsonString(jsonArray, STATIC);
        else if (mode == FLOW)
            strJson = prepareJsonString(jsonArray, FLOW);
        else
            return null;
        Log.i(TAG, "setMode: " + strJson);
        //post http request
        String res = null;
        try {
            res = postJson(urlAddMode(_url), strJson);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
    * setStatic
    * color:设置的颜色
    * mSec:设置间隔时间（默认0）
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    public String setStatic(int mSec, long[] color) throws JSONException { return setMode(STATIC, mSec, color);}

    /*
    * setStatic
    * color:设置的颜色
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    public String setStatic(long[] color) throws JSONException { return setMode(STATIC, STATIC_DEFAULT_INTERVAL, color);}

    /*
    * setFlow
    * color:设置的颜色
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    public String setFlow(long[] color) throws JSONException { return setMode(FLOW, FLOW_DEFAULT_INTERVAL, color);}

    /*
    * setFlow
    * color:设置的颜色
    * mSec:设置时间间隔
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    public String setFlow(int mSec, long[] color) throws JSONException {return setMode(FLOW, mSec, color);}

    public void changeWIFIModeToAP(){
        String tmp = _url.toString() + "/setting";
        URL sendurl = null;
        try {
            sendurl = new URL(tmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.object();
            jsonStringer.key("mode");
            jsonStringer.value("ap");
            jsonStringer.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("change to AP mode: " + jsonStringer.toString());

        postJson(sendurl, jsonStringer.toString());
    }

    public void changeWIFIModeToSTA(String ssid, String passwd){
        String tmp = _url.toString() + "/setting";
        URL url = null;
        try {
            url = new URL(tmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.object();
            jsonStringer.key("mode");
            jsonStringer.value("sta");
            jsonStringer.key("wifi_ssid");
            jsonStringer.value(ssid);
            jsonStringer.key("wifi_passwd");
            jsonStringer.value(passwd);
            jsonStringer.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("change to STA send: " + jsonStringer.toString());

        postJson(url, jsonStringer.toString());
    }

    public void updateURL(URL url){
        this._url = url;
    }

    public void updateURL(String host){
        String tmpurl = "http://" + host + ":" + _port;
        URL  newurl = null;
        try {
            newurl = new URL(tmpurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this._url = newurl;
    }

    private URL urlAddMode(URL url) throws MalformedURLException {
        String str = url.toString() + "/mode";
        URL tmpURL = new URL(str);
        return tmpURL;
    }

    /*
    * 设置定时开灯
    * mode:选择模式（static or flow）
    * color:根据不同模式选择颜色
    * timer:设置开灯定时器
    * */
    public void setTimer(int mode, long[] color, int timer) throws JSONException {
        switch (mode){
            case STATIC: setTimerMode(STATIC, color, timer); break;
            case FLOW: setTimerMode(FLOW, color, timer); break;
            default:
                Log.i("Error", "setTimer: " + "wrong mode!");
                return;
        }
    }

    /*
    * prepareJsonString for Timer
    * jsonArray:颜色RGB数组
    * mode:选择模式
    * timer:设置定时器
    * return:返回发送的json字符串
    * */
    private String prepareTimerJsonString(JSONArray jsonArray, int mode, int timer){
        JSONStringer jsonStringer = new JSONStringer();
        String strMode;
        switch (mode)
        {
            case STATIC: strMode = "static"; break;
            case FLOW: strMode = "flow"; break;
            default: strMode = "error";
        }

        try {
            jsonStringer.object();
            jsonStringer.key("params");
            jsonStringer.value(jsonArray);
            jsonStringer.key("mode");
            jsonStringer.value(strMode);
            jsonStringer.key("after");
            jsonStringer.value(timer);
            jsonStringer.endObject();
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return jsonStringer.toString();
    }

    /*
    * setMode
    * mode:选择模式
    * color:设置的颜色
    * mSec:流光间隔
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    private String setTimerMode(int mode, int mSec,  long[] color, int timer) throws JSONException {
        JSONArray jsonArray;
        String strJson;
        jsonArray = prepareJsonArray(mSec,color);
        if (mode == STATIC)
            strJson = prepareTimerJsonString(jsonArray, STATIC, timer);
        else if (mode == FLOW)
            strJson = prepareTimerJsonString(jsonArray, FLOW, timer);
        else
            return null;
        Log.i(TAG, "setTimerMode: " + strJson);
        //post http request
        String res = null;
        try {
            res = postJson(urlAddMode(_url), strJson);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // System.out.println("response is " + res);
        return res;
    }

    /*
    * setMode
    * color:设置的颜色
    * mSec:默认值（1000）
    * return:若发送成功，返回接受到的消息；否则，返回NULL
    * */
    public String setTimerMode(int mSec, long[] color, int timer) throws JSONException {return setTimerMode(STATIC, mSec, color, timer);}


}

