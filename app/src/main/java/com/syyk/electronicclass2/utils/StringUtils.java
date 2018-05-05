package com.syyk.electronicclass2.utils;

import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;

/**
 * Created by fei on 2017/12/5.
 */

public class StringUtils {

    /**
     * 普通的Toast
     * @param flag
     */
    public static void showToast(String flag){
        ToastUtil.showToast(ElectronicApplication.getmIntent(),flag);
    }
    /**
     * 中部的Toast
     * @param flag
     */
    public static void showCenterToast(String flag){
        Toast toast = ToastUtil.getmToast(ElectronicApplication.getmIntent(),flag) ;
        if(toast == null){
            return;
        }
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    /**
     * 带图片的Toast
     * @param flag
     */
    public static void showImageToast(String flag){
        Toast toast = ToastUtil.getmToast(ElectronicApplication.getmIntent(),flag) ;
        if(toast == null){
            return;
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(ElectronicApplication.getmIntent());
        imageCodeProject.setImageResource(R.mipmap.ic_launcher);
        toastView.addView(imageCodeProject, 0);
        toast.show();
    }

    public static void showLog(String flag){
        if(ElectronicApplication.getmIntent().isDebug()){
            Log.i("fei_ElectronicClass"+DateTimeUtil.getCurFormatStrTime("yyyy-MM-dd HH:mm:ss",
                    ElectronicApplication.getmIntent().timeMulis),flag);
        }
    }

    public static boolean isEmpty(String flag){
        if(flag == null){
            return true;
        }else if(flag.equals("") || flag.equals("null") || flag.equals("NULL")){
            return true;
        }else{
            return false;
        }
    }

}
