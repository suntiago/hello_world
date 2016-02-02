package com.suntiago.mytest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.suntiago.mytest.R;

/**
 * Created by yu.zai on 2015/11/26.
 */
public class PickProgressView extends View implements View.OnTouchListener {
    private static final String TAG = "PickProgressView";
    private Bitmap mThumb;
    private int mNodes;
    private int value = 0;
    private int xStart;
    private int xEnd;
    private int yStart;
    private int yEnd;
    private float nowX;

    /**
     * 记录用户是否在滑动
     */
    private boolean onSlip = false;

    /**
     * 监听接口
     */
    private OnChangedListener listener;
    public interface OnChangedListener {
        void OnChanged(int id, boolean CheckState);
    }
    public PickProgressView(Context context) {
        super(context);
        init();
    }

    public PickProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PickProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private int thumbw;
    private int thumbh;
    private void init(){
        mThumb = BitmapFactory.decodeResource(getResources(), R.drawable.switch_btn_thumb);
//        setMinimumHeight(500);
//        setMinimumWidth(300);
        mNodes = 5;
        setOnTouchListener(this);
        thumbw = mThumb.getWidth();
        thumbh = mThumb.getHeight();

    }
    Paint paint = new Paint();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int storkWidth = 5;
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(storkWidth);

        //View的高宽， 高度为thumb的高度
        int width = this.getWidth();
        int height = mThumb.getHeight();//this.getHeight();
        Log.d(TAG, "width:"+width+" heigt:"+height+"");
        int[] location = new  int[2] ;
        this.getLocationInWindow(location);
        this.getLocationOnScreen(location);

        //View 的开始位置
        float x = 0;//getX();
        float y = 0;//getY();
        Log.d(TAG, "x:"+x+" y:"+y+"");
        xStart = location[0];
        xEnd = location[0]+getWidth();
        yStart = location[1];
        yEnd = location[1]+getHeight();
        Log.d(TAG, "xStart:"+xStart+ " xEnd:"+xEnd+" yStart:"+yStart+" yEnd:"+yEnd+"");

        canvas.drawLine(x+thumbw/2,y+height/2, x+width-thumbw/2, y+height/2, paint);
        int i=0;
        while(i < mNodes) {
            canvas.drawLine(x+thumbw/2+(width - thumbw)*i/(mNodes-1),y+height/2-10,
                            x+thumbw/2+(width - thumbw)*i/(mNodes-1), y+height/2+10, paint);
            i++;
        }

        if(!onSlip) {
            canvas.drawBitmap(mThumb, (width - thumbw)/(mNodes-1)*value, y+height/2-thumbw/2, paint);
            return;
        }
        if(nowX <= xStart+thumbw/2) {
            canvas.drawBitmap(mThumb, x, y+height/2-thumbw/2, paint);
        } else if(nowX >= xEnd - thumbw/2){
            canvas.drawBitmap(mThumb, x+width-thumbw, y+height/2-thumbw/2, paint);
        } else {
            canvas.drawBitmap(mThumb, (nowX - xStart) - thumbw/2, y + height / 2 - thumbw / 2, paint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                    onSlip = true;
                float downX = event.getRawX();
                    nowX = downX;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                nowX = event.getRawX();
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{

                onSlip = false;
//                float x =event.getRawX() - location[0];
//                if (event.getRawX()>=xStart && event.getRawX()<=xEnd
//                        && event.getRawY()>=yStart &&event.getRawY()<=yEnd){
                    value = ((int)event.getRawX() - xStart)/((xEnd - xStart)/5);
                    nowX = xStart+value*((xEnd - xStart)/5);
//                }
//                if(listener != null){
//                    listener.OnChanged(WiperSwitch.this, nowStatus);
//                }
                break;
            }
        }
        invalidate();
        return true;
    }
}
