package com.syyk.electronicclass2.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by fei on 2018/5/9.
 */

public class LongTouchBtn extends Button {

    /**
     * 记录当前自定义Btn是否按下
     */
    private boolean clickdown = false;

    /**
     * 下拉刷新的回调接口
     */
    private LongTouchListener mListener;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100 :
                    clickdown = true;
                    if(mListener != null){
                        mListener.onLongTouch();
                    }
                    break;
            }
        }
    };

    /**
     * 构造函数
     * @param context
     * @param attrs
     */
    public LongTouchBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * 处理touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            handler.sendEmptyMessageDelayed(100,1500);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            handler.removeMessages(100);
            if(clickdown){
                clickdown = false;
                if(mListener != null){
                    mListener.onLongUp(true);
                }
            }else{
                if(mListener != null){
                    mListener.onLongUp(false);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 给长按btn控件注册一个监听器。
     * @param listener 监听器的实现。
     */
    public void setOnLongTouchListener(LongTouchListener listener) {
        mListener = listener;
    }

    /**
     * 长按监听接口，使用按钮长按的地方应该注册此监听器来获取回调。
     */
    public interface LongTouchListener {
        /**
         * 处理长按的回调方法
         */
        void onLongTouch();
        void onLongUp(boolean isLong);
    }
}
