package com.suntiago.mytest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;

import com.suntiago.mytest.R;
import com.suntiago.mytest.util.MyInputFilter;
import com.suntiago.mytest.view.ShakeLinearLayout;


/**
 * Created by yu.zai on 2016/2/1.
 */
public class MyActivity extends Activity{
    public static final String TAG = "MyActivity";
    ShakeLinearLayout shakeLinearLayout1;
    EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initView();
    }

    public void initView() {
        editText1 = (EditText) findViewById(R.id.et_test1);
        editText1.setFilters( new InputFilter[]{new MyInputFilter(5, 5, new MyInputFilter.CallBackOverLength() {
            @Override
            public void lengthChange(int now, int attempt) {
                if (attempt > 5) {
                    if (shakeLinearLayout1 != null) {
                        shakeLinearLayout1.shake();
                    }
                }
            }
        })});
        shakeLinearLayout1 = (ShakeLinearLayout) findViewById(R.id.sl_test1);
    }

}
