package com.example.chen.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.chen.sample.ColorPickView.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class getFlowSet extends AppCompatActivity {
    private ColorPickView myView;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private Button bt;
    private int setColor , i = 0;
    List<Integer> list = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_flow_set);
        //Bitmap.createBitmap(view.getDrawingCache())
        myView = (ColorPickView) findViewById(R.id.color_picker_view);
        linearLayout  = (LinearLayout) findViewById(R.id.linearLayout);
        bt = (Button) findViewById(R.id.bt);

        linearLayout.setBackgroundColor(Color.rgb(255,0,0));
        setWindowStatusBarColor(getFlowSet.this,Color.rgb(255,0,0));
        bt.setBackgroundColor(Color.rgb(255,0,0));

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_get_flow_set);
        myView.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChange(int color) {
                linearLayout.setBackgroundColor(color);
                setWindowStatusBarColor(getFlowSet.this,color);
                bt.setBackgroundColor(color);
                setColor =color;
            }

        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setColor == 0) return;
                list.add(setColor);
                Log.i("getFlowSet", "onClick: "+setColor);
            }
        });
    }

    @Override
    public void onBackPressed() {
        myapp app = ((myapp)getApplicationContext());
        app.setlist(list);
        super.onBackPressed();
    }

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(colorResId);

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
