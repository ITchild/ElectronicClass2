package com.dahuatech.netsdk.common;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.syyk.electronicclass2.MainActivity;
import com.syyk.electronicclass2.R;


public class C2DMReceiver extends C2DMBaseReceiver{
	private static String TAG = "C2DMReceiver";
	private static final String MESSAGE_KEY_ONE = "msg";
	private static final String SENDER_ID = PushHelper.instance().getGcmSenderId();

	public C2DMReceiver()
	{
		super(SENDER_ID);
	}
	public C2DMReceiver(String senderId) {
		super(senderId);
		Log.d(TAG, "C2DMReceiver constructor ");
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if(extras!=null){
			try
			{
				String msg = (String)extras.get(MESSAGE_KEY_ONE);
				Log.d(TAG, "C2DMReceiver message : " + msg);

				/// message format 设备名称::设备ID::通道号::报警类型::报警时间
				/// deviceName::deviceID::ChannelID::AlarmType::AlarmTime
				int alarmTypeResId = R.string.alarm_push_unknown_type;
				String strs[] = msg.split("::");
				String alarmType = "";
				String alarmTime = "";
				String channelNum = "";
				String deviceId = "";
				if (strs.length == 5 ) {
					deviceId = strs[1];
					channelNum = strs[2];
					alarmType = strs[3];
					alarmTime = strs[4];

					/// only deal with video-motion-event here.
					if (alarmType.equals("VideoMotion")) {
						alarmTypeResId = R.string.alarm_push_video_motion;
					}
				}

                Resources r = getResources();
				Intent intent1 = new Intent(Intent.ACTION_MAIN);
				intent1.addCategory(Intent.CATEGORY_LAUNCHER);
				intent1.setClass(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, 0);

				String message =  r.getString(R.string.channel) + "-" + channelNum + " "	+ r.getString(alarmTypeResId);
				String title = r.getString(R.string.alarm_push_title) + " " +alarmTime;

				Log.d(TAG, "Notification message: " + message);

                Notification notification = new NotificationCompat.Builder(this)
                        .setTicker(r.getString(R.string.activity_p2plogin_device_id))
                        .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification);
			}
			catch(Exception e)
			{
				Log.d(TAG, "deal push message error");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "C2DMReceiver error");
	}
	
	@Override
	public void onRegistered(Context context, String registrationId)
			throws IOException {
		// TODO Auto-generated method stub
		super.onRegistered(context, registrationId);
		Log.d(TAG, "C2DMReceiver Register");
	}
	@Override
	public void onUnregistered(Context context) {
		// TODO Auto-generated method stub
		super.onUnregistered(context);
		Log.d(TAG, "C2DMReceiver UnRegister");
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
