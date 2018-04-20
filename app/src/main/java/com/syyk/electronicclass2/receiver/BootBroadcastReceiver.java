package com.syyk.electronicclass2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.syyk.electronicclass2.MainActivity;

/**
 * Created by xiaowei on 2017/5/31.
 * 开机自启
 */

public class BootBroadcastReceiver extends BroadcastReceiver{
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
