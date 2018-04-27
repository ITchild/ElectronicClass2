package com.syyk.electronicclass2.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wugang on 2016/6/20.
 */

public class ToastUtil {
    /**
     * 显示toast
     *
     * @param context
     * @param text
     */
    private static android.widget.Toast mToast;

    public static void showToast(Context context, String text) {
        if (context != null && text != null) {
            try {
                if (mToast == null) {
                    mToast = android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(text);
                    mToast.setDuration(android.widget.Toast.LENGTH_SHORT);
                }
                mToast.show();
            } catch (NullPointerException e) {
            }
        }
    }

    public static Toast getmToast(Context context,String text){
        if (context != null && text != null) {
            try {
                if (mToast == null) {
                    mToast = android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(text);
                    mToast.setDuration(android.widget.Toast.LENGTH_SHORT);
                }
                return mToast;
            } catch (NullPointerException e) {
                return null;
            }
        }else{
            return null;
        }
    }

//    public class ToastUtile {
//
//        // 构造方法私有化 不允许new对象
//        private ToastUtile() {
//        }
//
//        // Toast对象
//        private static Toast toast = null;
//
//        /**
//         * 显示Toast
//         */
//        public static void showText(Context context, String text) {
//            if (toast == null) {
//                toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
//            }
//            toast.setText(text);
//            toast.show();
//        }
//    }
}
