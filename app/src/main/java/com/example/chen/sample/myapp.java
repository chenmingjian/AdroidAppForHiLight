package com.example.chen.sample;

import android.app.Application;
import android.content.Intent;

import java.util.List;

/**
 * Created by chen on 2016/12/27.
 */

public class myapp extends Application {

    private List<Integer> list;

    public List<Integer> getlist(){

        return list;

    }

    public void setlist(List<Integer> s){

        list = s;

    }

}