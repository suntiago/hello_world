package com.suntiago.mytest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suntiago.mytest.R;
import com.suntiago.mytest.view.HorizontalListView;

/**
 * Created by yu.zai on 2016/2/26.
 */
public class NumberPickerActivity  extends Activity{
    HorizontalListView horizontalListView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_picker);

        horizontalListView = (HorizontalListView) findViewById(R.id.listview);
        textView = (TextView) findViewById(R.id.text_current);
        horizontalListView.setAdapter(mAdapter);
        horizontalListView.setOnChangeCallBack(new HorizontalListView.CurrentXChangeCallBack() {
            @Override
            public void getCurrentX(float x) {
//                float c = x+horizontalListView.getWidth()/2;
                textView.setText(x+"");
            }
        });
    }

    private BaseAdapter mAdapter = new BaseAdapter() {

        private View.OnClickListener mOnButtonClicked = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NumberPickerActivity.this);
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
