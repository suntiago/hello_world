package com.suntiago.mytest.util;

import android.text.InputFilter;
import android.text.Spanned;
/**
 * Created by yu.zai on 2016/2/19.
 */
public class MyInputFilter implements InputFilter {
    int maxLen = 10;
    int maxLenHanzi = 4;
    int maxlenChar = 10;
    CallBackOverLength mCallBack;


    public interface CallBackOverLength{
        /**
         * when edit text changes, it will callback
         * @param now size of the edit text now
         * @param attempt size that user want to change to
         * @return
         * @throws
         */
        void lengthChange(int now, int attempt);
    }

    public MyInputFilter() {
    }
    public MyInputFilter(int lenChar, int lenHanzi) {
        this.maxLenHanzi = lenHanzi;
        this.maxlenChar = lenChar;
        this.maxLen = maxlenChar;
    }

    /**
     *
     * @param lenChar asc words max size
     * @param lenHanzi chinese words max size
     * @param mCallBack ..
     * @return
     * @throws
     */
    public MyInputFilter(int lenChar, int lenHanzi, CallBackOverLength mCallBack) {
        this.maxLenHanzi = lenHanzi;
        this.maxlenChar = lenChar;
        this.maxLen = maxlenChar;
        this.mCallBack = mCallBack;
    }

    @Override
    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
        int dindex = 0;
        int count = 0;
        int attempt = 0;

        maxLen = maxlenChar;
        while(dindex < src.length() ) {
            char c = src.charAt(dindex++);
            count++;
            if(c >= 128) {
                maxLen = maxLenHanzi;
            }
        }
        dindex = 0;
        while(dindex < dest.length() ) {
            char c = dest.charAt(dindex++);
            count++;
            if(c >= 128) {
                maxLen = maxLenHanzi;
            }
        }


        attempt = count;
        if (count > maxLen) {
            //more words input than you want ,give up the redundant character
            count = maxLen;
            src = "";
        }
        if(mCallBack!=null){
            mCallBack.lengthChange(count, attempt);
        }
        return  src;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public int getMaxlenChar() {
        return maxlenChar;
    }

    public int getMaxLenHanzi() {
        return maxLenHanzi;
    }
}