package com.syyk.electronicclass2.httpcon;

import android.os.Message;
import android.util.Log;


import com.syyk.electronicclass2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by fei on 2017/7/5.
 * 用于联网的时候使用，基于OKHttp
 */

public class ConnectionClient {

    private static OkHttpClient client;

    /**
     * 使用Post连接
     * @param url
     */
    public static void simplePostCon(RequestBody body, String url, final int resCode){
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("head","123321")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                HttpEventBean bean = new HttpEventBean();
                bean.setResCode(NetCartion.FIAL);
                bean.setBackCode(resCode);
                bean.setRes("未连接到服务器!");
                EventBus.getDefault().post(bean);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                HttpEventBean bean = new HttpEventBean();
                if(res.contains("<!DOCTYPE html>")){
                    bean.setBackCode(NetCartion.FIAL);
                    bean.setRes("服务器错误");
                    StringUtils.showLog(res);
                }else if(res.contains("Err")){
                    bean.setBackCode(NetCartion.FIAL);
                    bean.setRes("未知错误");
                    StringUtils.showLog(res);
                } else {
                    bean.setResCode(NetCartion.SUCCESS);
                    bean.setRes(res);
                    StringUtils.showLog(res);
                }
                bean.setBackCode(resCode);
                EventBus.getDefault().post(bean);
            }
        });
    }

    public static void simpleCon(String url,int resCode){
        StringUtils.showLog(url);
        simpleGetCon(url,resCode);
    }
    public static void simpleCon(RequestBody body,String url,int resCode){
        simplePostCon(body,url,resCode);
    }

    /**
     * 使用Get连接
     * @param url
     */
    public static void simpleGetCon(String url, final int resCode){
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("head","123321")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                HttpEventBean bean = new HttpEventBean();
                bean.setResCode(NetCartion.FIAL);
                bean.setBackCode(resCode);
                bean.setRes("未连接到服务器!");
                EventBus.getDefault().post(bean);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                HttpEventBean bean = new HttpEventBean();
                if(res.contains("<!DOCTYPE html>")){
                    bean.setBackCode(NetCartion.FIAL);
                    bean.setRes("服务器错误");
                    StringUtils.showLog(res);
                }else if(res.contains("Err")){
                    bean.setBackCode(NetCartion.FIAL);
                    bean.setRes("未知错误");
                    StringUtils.showLog(res);
                } else {
                    bean.setResCode(NetCartion.SUCCESS);
                    bean.setRes(res);
                    StringUtils.showLog(res);
                }
                bean.setBackCode(resCode);
                EventBus.getDefault().post(bean);
            }
        });
    }


}
