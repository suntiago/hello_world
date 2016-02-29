/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

/*
 *     zaiyu 2016 modified
 *
 */
package com.suntiago.mytest.view;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import com.suntiago.mytest.R;

public class HorizontalListView extends AdapterView<ListAdapter> {

	private final String TAG = getClass().getName();
	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mMinX = 0;
	private int mDisplayOffset = 0;

	private int mStartIndex = 0;
	private int mEndIndex = 0;

	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;

	private CurrentXChangeCallBack mChangeCallBack;
	private int mChildWidth = -1;
	private int mChildCount = -1;
	private int mMinGap = -1;
	private int mChildParties = 10;
	private int defaultValue = 4;

	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalListView);
		mStartIndex = a.getInteger(R.styleable.HorizontalListView_startIndex, -1);
		mEndIndex = a.getInteger(R.styleable.HorizontalListView_endIndex, -1);
		a.recycle();

		initView();
	}
	
	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	}
	
	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}
	
	@Override
	public void setOnItemClickListener(OnItemClickListener listener){
		mOnItemClicked = listener;
	}
	
	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}

	public int getStartIndex() {
		return mStartIndex;
	}

	public void setStartIndex(int mStartIndex) {
		this.mStartIndex = mStartIndex;
	}

	public int getEndIndex() {
		return mEndIndex;
	}

	public void setEndIndex(int mEndIndex) {
		this.mEndIndex = mEndIndex;
	}

	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized(HorizontalListView.this){
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}
		
	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		//TODO: implement
		return null;
	}

	public  interface CurrentXChangeCallBack {
		void getCurrentX(float x);
	}
	public void setOnChangeCallBack(CurrentXChangeCallBack changeCallBack) {
		mChangeCallBack = changeCallBack;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if(mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();


	}
	
	private synchronized void reset(){
		initView();
		removeAllViewsInLayout();
        requestLayout();
	}
	boolean havesetMaXMin = false;

	/**
	 * 初始化 单个child宽度，总数，左右边界，最小间隔
	 *		滑动到默认值位置处
	 * @paam a
	 * @return
	 * @throws
	 */
	public void initMaxMin() {
		if (havesetMaXMin) {
			return;
		}
		View view = getChildAt(0);
		if (view != null) {
			mChildWidth = view.getWidth();
			if (mChildWidth == 0) {
				return;
			}
			mChildCount = mAdapter.getCount();
			Log.d(TAG, "initMaxMin:"+"child != null");
		} else {
			Log.d(TAG, "initMaxMin:"+"child null");
			return;
		}
		havesetMaXMin = true;
		Log.d(TAG, "c:"+mChildWidth + " mChildCount:"+mChildCount);

		if (mEndIndex >= mAdapter.getCount()) {
			mEndIndex = mAdapter.getCount()-1;
		}
		mMaxX = mChildWidth*mEndIndex + (mChildWidth/2 - getWidth()/2);
		if (mMaxX < 0) {
			mMaxX = 0;
		}

		Log.d(TAG, "mMAxx:"+mMaxX);
		if (mStartIndex < 0) {
			mStartIndex = 0;
		}
		mMinX = (mStartIndex * mChildWidth) + (mChildWidth/2 - getWidth()/2);
		if (mMinX > mMaxX) {
			mMaxX = mMinX;
		}
		mMinGap = mChildWidth/ mChildParties;
		Log.d(TAG, "mMinx:"+mMinX);

		post(new Runnable() {
			@Override
			public void run() {
				if (defaultValue >= mStartIndex && defaultValue <= mEndIndex) {
					scrollTo((defaultValue * mChildWidth) + (mChildWidth / 2 - getWidth() / 2));
				} else {
					scrollTo(mMinX);
				}
			}
		});
	}

	/**
	 * 设置单个child 均分格数
	 *
	 * @parm c 格数
	 * @return
	 * @throws
	 */
	public void setmChildParties(int c) {
		if (c > 0) {
			mChildParties = c;
		} else {
			mChildParties = 10;
		}
		mMinGap = mChildWidth/ mChildParties;
	}

	@Override
	public void setSelection(int position) {
		//TODO: implement
	}
	
	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	boolean keydown = false;

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(mAdapter == null) {
			return;
		}
		initMaxMin();
		if(mDataChanged){
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		if(mScroller.computeScrollOffset()){
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}
		
//		if(mNextX <= 0){
//			mNextX = 0;
//			mScroller.forceFinished(true);
//		}
//		if(mNextX >= mMaxX) {
//			mNextX = mMaxX;
//			mScroller.forceFinished(true);
//		}
		
		int dx = mCurrentX - mNextX;
		
		removeNonVisibleItems(dx);

		fillList(dx);

		positionItems(dx);
		
		mCurrentX = mNextX;

		if(!mScroller.isFinished()){
			Log.d(TAG, "!    mScroller.isFinished");
			post(new Runnable(){
				@Override
				public void run() {
					requestLayout();
				}
			});
		} else {
			Log.d(TAG, "mScroller.isFinished"+ "mCurrentX%min"+mCurrentX+" "+mMinGap+" "+ mCurrentX%mMinGap );
			int gap = (mCurrentX+this.getWidth()/2)%mMinGap;
			if (gap > mMinGap/2) {
				gap = gap - mMinGap;
			}
			final int fGap = gap;
			if (!keydown) {
				onUp(fGap);
			}
		}
		if (mChangeCallBack != null) {
			 if (mChildWidth < 0) {
//				 mChangeCallBack.getCurrentX(defaultValue);
			 } else {
				 mChangeCallBack.getCurrentX((mCurrentX + this.getWidth() / 2 - mChildWidth / 2) / (float) mChildWidth);
			 }
		}
	}
	
	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount()-1);
		if(child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if(child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
	}
	
	private void fillListRight(int rightEdge, final int dx) {
		Log.d(TAG, "rightEdge:"+rightEdge+" dx:"+dx);
		while(rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
			Log.d(TAG, "mRightViewIndex:"+mRightViewIndex);
			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();
			mRightViewIndex++;
		}
	}
	
	private void fillListLeft(int leftEdge, final int dx) {
		Log.d(TAG, "leftEdge:"+leftEdge+" dx:"+dx);
		while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			Log.d(TAG, "mLeftViewIndex:"+mLeftViewIndex);
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}
	
	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while(child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);
		}
		
		child = getChildAt(getChildCount()-1);
		while(child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount()-1);
		}
	}
	
	private void positionItems(final int dx) {
		if(getChildCount() > 0){
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
				left += childWidth + child.getPaddingRight();
			}
		}
	}
	
	public synchronized void scrollTo(int x) {
		mScroller.startScroll(mNextX, 0, x - mNextX, 0);
		requestLayout();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_HOVER_EXIT:
			case MotionEvent.ACTION_OUTSIDE:
				keydown = false;
				post(new Runnable(){
					@Override
					public void run() {
						requestLayout();
					}
				});
				break;
			case MotionEvent.ACTION_DOWN:
				keydown = true;
				break;
			default:
				break;
		}
		return handled;
	}
	
	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
		synchronized(HorizontalListView.this){
			mScroller.fling(mNextX, 0, (int)-velocityX, 0, mMinX, mMaxX, 0, 0);
		}
		requestLayout();
		return true;
	}
	
	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}

	protected boolean onUp(final int fGap) {
		if (mCurrentX < mMinX) {
			post(new Runnable(){
				@Override
				public void run() {
					scrollTo(mMinX);
				}
			});
		} else if (mCurrentX > mMaxX) {
			post(new Runnable(){
				@Override
				public void run() {
					scrollTo(mMaxX);
				}
			});
		} else {
			if (fGap != 0) {
				post(new Runnable() {
					@Override
					public void run() {
						scrollTo(mCurrentX - fGap);
					}
				});
			}
		}
		return true;
	}

	boolean isFling = false;
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			Log.d(TAG, "onDown");
			isFling = false;
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.d(TAG, "onSingleTapUp");
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.d(TAG, "onFling");
			isFling = true;
			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.d(TAG, "onScroll, distanceX:"+distanceX);
			synchronized(HorizontalListView.this){
				mNextX += (int)distanceX;
			}
			requestLayout();
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.d(TAG, "onSingleTapConfirmed");
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if(mOnItemClicked != null){
						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					break;
				}
			}
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			Log.d(TAG, "onLongPress");
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					break;
				}

			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
			Log.d(TAG, "isEventWithinView");
            Rect viewRect = new Rect();
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            int left = childPosition[0];
            int right = left + child.getWidth();
            int top = childPosition[1];
            int bottom = top + child.getHeight();
            viewRect.set(left, top, right, bottom);
            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
        }

	};
}
