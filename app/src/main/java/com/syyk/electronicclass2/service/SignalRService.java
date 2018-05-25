package com.syyk.electronicclass2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.JsonElement;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.utils.Catition;
import com.syyk.electronicclass2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;


/**
 * Created by fei on 2018/5/14.
 */

public class SignalRService extends Service {

    public static final String serverUrl = "http://122.14.216.65:8090/signalr";//服务器地址

    public static final String SERVER_HUB_CHAT = "serverHub";//与服务器那边的名称一致

    public static final String SERVER_METHOD_SEND = "Connect";//服务器上面的方法
    public static final String CLIENT_METHOD_RECEIVE = "ReciveConnected";//客户端这边的方法，服务器调用时需要根据这个来进行对应的调用

    private static final String TAG = "SignalRService";

    private HubConnection mHubConnection; 
    private HubProxy mHubProxy;

    private final IBinder mBinder = (IBinder) new LocalBinder();
    private Timer Meitimer;
    private int time;

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);
        Log.d(TAG, "Service Created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public IBinder onBind(Intent intent) {
        startSignalR();
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        mHubConnection = new HubConnection(serverUrl);//初始化连接
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
        time = 0;
        connectTimeout();

        if (mHubConnection != null) {
            Log.i("hub", "初始化服务");
            SignalRFuture<Void> awaitConnection = mHubConnection.start();
            Log.i("hub", "启动服务");
            if (mHubProxy != null) {
                try {
                    awaitConnection.get();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
                //Then call on() to handle the messages when they are received.
                mHubProxy.on(CLIENT_METHOD_RECEIVE, new SubscriptionHandler1<String>()//接收返回的数据
                {
                    @Override
                    public void run(String msg) {
                        Log.i("hub", "  接收到的推送  " + msg);
                        if (msg.equals("OK"))
                            StringUtils.showToast("连接成功");
                    }
                }, String.class);

//                    serviceOpatorClient();

                String token = mHubConnection.getConnectionToken();

                if (token != null) {
                    Log.i("hub", "连接·成功");
                    //发送Mac地址给服务器
                    sendData("2e:10:58:f2:e7:4e");
                } else {
                    Log.i("hub", "连接失败");
                }
                Log.i("hub", "连接到服务器");
            } else {
                Log.i("hub", "连接失败 HUB = NULL");
            }
        } else {
            Log.i("hub", "连接失败 CONNETION = NULL");
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void SignalEvent(MessageBean bean) {
        switch (bean.getMsgCode()) {
            case Catition.SIGNALR_SEND://判断连接是否还存在
                String token = mHubConnection.getConnectionToken();
                if (token != null) {
                    Log.i("hub", "连接·成功");
                } else {
                    Log.i("hub", "连接失败正在重新连接");
                    if(mHubConnection != null) {
                        mHubConnection.stop();
                        mHubConnection = null;
                    }
                    startSignalR();
                }
                break;
        }
    }

    public void sendData(String mac) {
        mHubProxy.invoke(String.class, SERVER_METHOD_SEND, mac)
                .done(new Action() {
                    @Override
                    public void run(Object o) throws Exception {
                        Log.i(TAG, "Success");

                    }
                }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
            }
        });
    }


    /**
     * 服务器通知客户端调用方法
     */
    private void serviceOpatorClient() {
        mHubProxy.subscribe("broadcastMessageone").addReceivedHandler(
                new Action<JsonElement[]>() {
                    @Override
                    public void run(JsonElement[] obj)
                            throws Exception {
                        Log.i("hub", "ffffffff" + obj[0].toString() + "长度：" + obj.length);
                        Log.i("hub", "ffffffff" + obj[1].toString() + "长度：" + obj.length);
                        Log.i("hub", "ffffffff" + obj[2].toString() + "长度：" + obj.length);
                        Log.i("hub", "ffffffff" + obj[3].toString() + "长度：" + obj.length);
                        if (obj.length > 3) {
                            String data = obj[2].toString();
                            Log.i("hub", obj[2].toString());
                        }

                    }
                });
    }

    /**
     * 连接服务器超时
     */
    public void connectTimeout() {


        Meitimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mHubConnection != null) {
                        String token = mHubConnection.getConnectionToken();
                        if (token != null) {
                            Meitimer.cancel();
                            time = 0;
                            return;
                        }
                    }
                    if (time > 0) {
                        Log.i("+++connectTimeout", "连接超时");
                        time = 0;
                        Meitimer.cancel();
                        return;
                    }

                    time = time + 1;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        };
        Meitimer.scheduleAtFixedRate(task, 1000 * 20, 1000 * 20);
    }

    @Override
    public void onDestroy() {
        if(mHubConnection != null) {
            mHubConnection.stop();
        }
        super.onDestroy();

    }
}