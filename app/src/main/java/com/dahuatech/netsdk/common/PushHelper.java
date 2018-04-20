package com.dahuatech.netsdk.common;

import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;



/**
 * push helper
 * 有关推送功能的帮助类
 * @author 18047
 */
public class PushHelper implements C2DMRegisterListener {
    private static final String   TAG = "PushHelper";
    private static final String   PREFERENCE = "com.google.android.c2dm";
    private static final String   PREKEY     = "dm_registration";
    private static final String   PREREPUSHDID  = "repush_devid";
    private static final String   PRENOTICEREPUSH  = "notice_repush";
    private static final String   PRETIME    = "time";
    private static final String   PUSH_SERVER_ADDRESS       = "https://cellphonepush.quickddns.com/gcm/send";
    private static final String   PUSH_SERVER_ADDRESS_MAIN  = "https://android.googleapis.com/gcm/send";
    private final static String   PUSH_TYPE_VIDEO_MOTION        		= "VideoMotion";
    private final static String   PUSH_TYPE_VIDEO_BLIND         		= "VideoBlind";
    private final static String   PUSH_TYPE_ALARM_LOCAL         		= "AlarmLocal";
    private final static String   PUSH_TYPE_NOANSWER_CALL       		= "NoAnswerCall";
    private final static String   PUSH_TYPE_CALL_NOANSWERED     		= "CallNoAnswered";
    private final static String   PUSH_TYPE_STORAGE_NOT_EXIST   		= "StorageNotExist";
    private final static String   PUSH_TYPE_STORAGE_LOW_SPACE   		= "StorageLowSpace";
    private final static String   PUSH_TYPE_STORAGE_FAILURE     		= "StorageFailure";

    /// API KEY get from google
    /// The mApiKey is not "api_key":"current_key" form google-services.json
    private String mApiKey = "AIzaSyAiPvjX0qzfdq72G41CssfGHQAIea7tl4g";
    private String mGcmSenderId = "163569807662";
    
    private final static String[] PUSHTYPE = {PUSH_TYPE_VIDEO_MOTION,PUSH_TYPE_VIDEO_BLIND,PUSH_TYPE_ALARM_LOCAL,
        PUSH_TYPE_NOANSWER_CALL,PUSH_TYPE_STORAGE_NOT_EXIST,PUSH_TYPE_STORAGE_LOW_SPACE,PUSH_TYPE_STORAGE_FAILURE};
    private static PushHelper  pushHelper;
    private final Boolean mEvent = new Boolean(false);

    public static synchronized PushHelper instance(){
        if(pushHelper == null){
            pushHelper = new PushHelper();
        }
        return pushHelper;
    }
    
    private PushHelper() {
    }

    public String getApiKey() {
        return mApiKey;
    }

    public String getGcmSenderId() {
        return mGcmSenderId;
    }

    /**
     * 获取registerID，30天过期需要重新从google获取
     * Get registerID, 30 days later, should get from google again.
     * @param context
     * @return  null: error 获取失败
     */  
    public  String  getRegisterID(Context context) {
        String registerID = null;
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        registerID = preference.getString(PREKEY, null);
        long regTime = preference.getLong(PRETIME, System.currentTimeMillis());
        long extTime = System.currentTimeMillis() - regTime;
        long time = (long)3600*24*30*1000;//30天,过期
        if( extTime - time > 0) {
            registerID = null;
        }
        if(registerID == null || registerID.equals("")) {
            // 异常捕获，防止部分国内手机不支持谷歌框架，导致崩溃
        	try	{
        		 Log.d(TAG, "go to regsister");
        		 C2DMessaging.register(context, mGcmSenderId, this);
			}catch (Exception e) {
				return null;
			}
             
            try {
                 synchronized (mEvent) {
                     Log.d(TAG, "wait.");
                     mEvent.wait(10000);
                     Log.d(TAG, "notify.");
                 }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            registerID = C2DMessaging.getRegistrationId(context);
            if(registerID == null || registerID.equals("")) {
                registerID = null;
            }
        }
        return registerID;
    }

    /**
     * 获取是否需要显示重新订阅的提示（更新程序后第一次进入监视界面时的提示框）
     * @param context
     * @return
     */
    public boolean  isNoticeRePush(Context context) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return preference.getBoolean(PRENOTICEREPUSH, true);
    }

    /**
     * 设置重新订阅的提示已显示过，下次不再提示
     * @param context
     */
    public void setHasNoticedRePush(Context context) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean(PRENOTICEREPUSH, false);
        editor.apply();
    }
    
    /**
     * 获取需要重新订阅的设备ID集合    组装格式：设备ID：设备ID
     * @param context
     * @return  返回null:表示第一次更新订阅，需要设置待更新的设备ID集合  空集合：表示待更新的设备已更新完毕   有元素的集合：表示余下待更新的设备ID
     */
    public ArrayList<Integer>  getRePushDeviceID(Context context) {
        ArrayList<Integer> devIds = null;
        String rePushDeviceID = null;
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        rePushDeviceID = preference.getString(PREREPUSHDID, null);
        if (rePushDeviceID == null) {
            devIds = null; 
        } else if(rePushDeviceID.equals("")) {
            devIds = new ArrayList<Integer>();
        } else {
            devIds = new ArrayList<Integer>();
            String[] ids = rePushDeviceID.split(":");
            for(int i =0;i< ids.length;i++) {
              devIds.add(Integer.parseInt(ids[i]));
            }
        }
        return devIds;
    }
    
    
    /**
     * 更新需要重新订阅的设备集合
     * @param context
     * @param hasPushedDid
     */
    public void updateRePushDeviceID(Context context,ArrayList<Integer> hasPushedDid)
    {
        if(hasPushedDid == null || hasPushedDid.isEmpty())
        {
            return;
        }
        String rePushDeviceID = null;
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        try
        {
            rePushDeviceID = preference.getString(PREREPUSHDID, null);
            if(rePushDeviceID == null)
            {
                editor.putString(PREREPUSHDID, "");
                editor.apply();
            }
            else
            {
                StringBuffer sb = new StringBuffer();
                ArrayList<Integer>  allPushDid = getRePushDeviceID(context);
                if(allPushDid != null)
                {
                	allPushDid.removeAll(hasPushedDid);
	                for(Integer did : allPushDid)
	                {
	                    sb.append(did).append(":");
	                }
	                String remainDid = sb.substring(0, sb.length() -1);
	                editor.putString(PREREPUSHDID, remainDid);
	                editor.apply();
                }
            } 
        }
        catch (Exception e)
        {
            editor.putString(PREREPUSHDID, "");
            editor.apply();
        }
    }
    
    
    /**
     * 设置需要重新订阅的设备ID
     * @param context
     * @param rePushedDids
     */
    public void setRePushDeviceID(Context context,ArrayList<Integer> rePushedDids)
    {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        if(rePushedDids == null || rePushedDids.isEmpty())
        {
            editor.putString(PREREPUSHDID, "");
            editor.apply();
            return;
        }
        StringBuffer sb = new StringBuffer();  
        for(Integer did : rePushedDids)
        {
            sb.append(did).append(":");
        }
        String pushDids = sb.substring(0, sb.length() -1);
        editor.putString(PREREPUSHDID, pushDids);
        editor.apply();
    }

    /**
     * 获取订阅时设置的有效时间天数
     * @param context
     * @param devID
     */
    public int getPeriodDay(Context context,int devID) {
        SharedPreferences preference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        String day = preference.getString(String.valueOf(devID), null);
        if(day != null && !day.equals("")) {
            return Integer.parseInt(day);
        } else  {
            return 0;
        }
    }

    /**
     * 从google获取registerID成功后的回调通知
     */
    @Override
    public void getRegisiterId(String id)
    {
        synchronized (mEvent) {
            mEvent.notify();
        }
    }
}
