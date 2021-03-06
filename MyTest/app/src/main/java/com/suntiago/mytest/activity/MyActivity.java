package com.suntiago.mytest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.suntiago.mytest.R;
import com.suntiago.mytest.util.MyInputFilter;
import com.suntiago.mytest.view.HorizontalListView;
import com.suntiago.mytest.view.ShakeLinearLayout;


/**
 * Created by yu.zai on 2016/2/1.
 */
public class MyActivity extends Activity {
    public static final String TAG = "MyActivity";
    ShakeLinearLayout shakeLinearLayout1;
    EditText editText1;
    TextView editTextCurrent;
    HorizontalListView horizontalListView;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information
    }

    public void initView() {
        editText1 = (EditText) findViewById(R.id.et_test1);
        editText1.setFilters(new InputFilter[]{new MyInputFilter(5, 5, new MyInputFilter.CallBackOverLength() {
            @Override
            public void lengthChange(int now, int attempt) {
                if (attempt > 5) {
                    if (shakeLinearLayout1 != null) {
                        shakeLinearLayout1.shake();
                    }
                }
            }
        })});
        editTextCurrent = (TextView) findViewById(R.id.text_current);
        shakeLinearLayout1 = (ShakeLinearLayout) findViewById(R.id.sl_test1);

        horizontalListView = (HorizontalListView) findViewById(R.id.listview);
        horizontalListView.setAdapter(mAdapter);
        horizontalListView.setOnChangeCallBack(new HorizontalListView.CurrentXChangeCallBack() {
            @Override
            public void getCurrentX(float x) {
//                float c = x+horizontalListView.getWidth()/2;
                editTextCurrent.setText(x+"");
            }
        });
    }

    private BaseAdapter mAdapter = new BaseAdapter() {

        private View.OnClickListener mOnButtonClicked = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                builder.setMessage("hello from " + v);
                builder.setPositiveButton("Cool", null);
                builder.show();

            }
        };

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            Log.d(TAG, horizontalListView.getSelect()+" select");
//            Log.d("getSelect", "getChildAt(0).getWidth():"+getChildAt(0).getWidth()+" mMaxX:"+mMaxX+ " mCurrentX:" + mCurrentX +" count:"+mAdapter.getCount()+"");

//            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewitem, null);
                TextView t = (TextView) convertView.findViewById(R.id.text);
                t.setText(position+"");
//            }

//            float f = horizontalListView.getCurrentX();
//            Log.d(TAG, "position:"+position);
//            Log.d(TAG, "f:"+f);
//            Log.d(TAG, "t.width:"+80);
//            Log.d(TAG, "select:"+ f/((float)80));
            return convertView;
        }

    };

}
