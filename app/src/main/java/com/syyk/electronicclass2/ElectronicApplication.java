package com.syyk.electronicclass2;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.syyk.electronicclass2.bean.ScheduleBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2017/12/29.
 */

public class ElectronicApplication extends Application {

    private static ElectronicApplication mIntent;
    private boolean isDebug = true;
    private NET_DEVICEINFO_Ex mDeviceInfo;
    private long mloginHandle;
    private String Mac = null;

    public List<ScheduleBean> todaySchedule = new ArrayList<>();

    public String SyllabusId;

    public long timeMulis = 0;
    public String date;
    public String week;

    @Override
    public void onCreate() {
        super.onCreate();
        mIntent = this;
        //初始化ImageLoader
        initImageLoader();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    DHActivityManager.getManager().setCurrentActivity(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
        setLoginHandle(0);
        setmDeviceInfo(null);
    }

    private void initImageLoader(){
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public static ElectronicApplication getmIntent(){
        return mIntent;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
    public NET_DEVICEINFO_Ex getmDeviceInfo() {
        return mDeviceInfo;
    }

    public void setmDeviceInfo(NET_DEVICEINFO_Ex mDeviceInfo) {
        this.mDeviceInfo = mDeviceInfo;
    }

    public long getLoginHandle() {
        return mloginHandle;
    }

    public void setLoginHandle(long loginHandle) {
        this.mloginHandle = loginHandle;
    }


    public static class DHActivityManager{
        private static DHActivityManager manager = new DHActivityManager();
        private WeakReference<Activity> current;
        private DHActivityManager(){

        }
        public static DHActivityManager getManager(){
            return manager;
        }

        public Activity getTopActivity(){
            if (current!=null)
                return current.get();
            return null;
        }
        public void setCurrentActivity(Activity obj){
            current = new WeakReference<Activity>(obj);
        }
    }
}
