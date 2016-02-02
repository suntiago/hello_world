package com.suntiago.mytest.activity;

import android.app.Activity;
import android.os.Bundle;

import com.suntiago.mytest.R;


/**
 * Created by yu.zai on 2016/2/1.
 */
public class MyActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }
}
