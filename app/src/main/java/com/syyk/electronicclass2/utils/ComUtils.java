package com.syyk.electronicclass2.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.syyk.electronicclass2.ElectronicApplication;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by fei on 2017/12/5.
 */

public class ComUtils {

    /**
     * 十六进制字符串转换成字节数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.replace(" ", "");
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    /**
     * 字节数组转换成十六进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src, int actualNumBytes) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || actualNumBytes <= 0) {
            return null;
        }
        for (int i = 0; i < actualNumBytes; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase());
        }
        return stringBuilder.toString();
    }

    public static String byteToString(byte[] src,int num){
        byte[] ress = new byte[num];
        for(int i=0;i<num;i++){
            ress[i] = src[i];
        }
        String socketRes = null;
        try {
            socketRes = new String(ress,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return socketRes;
    }

    /**
     * 判断设备是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                //mNetworkInfo.isAvailable();
                return true;//有网
            }
        }
        return false;//没有网
    }

    //是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
        return false ;
    }


    /**
     * 提示框
     * @param context
     * @param title
     * @param message
     */
    public static void showTipsDialog(Context context, String title, String message, final int type) {
//        type = 1;跳转到设置界面
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        switch(type){
                            case 1:
                                intent.setAction(Settings.ACTION_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ElectronicApplication.getmIntent().startActivity(intent);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getSave(String flag){
        SharedPreferences preferences = ElectronicApplication.getmIntent().getSharedPreferences("Setting",0);
        switch (flag){
            case "ip" :
                return preferences.getString("ip","192.168.1.198");
            case "vip" :
                return preferences.getString("vIp","192.168.1.208");
            case "vport" :
                return preferences.getString("vPort","37777");
            case "vuser" :
                return preferences.getString("vUser","admin");
            case "vpass" :
                return preferences.getString("vPass","admin");
        }
        return null;
    }


    public static String changeString(String flag,int num){
        if(!StringUtils.isEmpty(flag)){
            if(flag.length() < num){
                flag = "0"+flag;
                return changeString(flag,num);
            }else{
                return flag;
            }
        }else{
            return null;
        }
    }

    /**
     * 获取当前系统连接网络的网卡的mac地址
     * @return
     */
    @SuppressLint("NewApi")
    public static final String getMac() {
        if(ElectronicApplication.getmIntent().getMac() == null) {
            byte[] mac = null;
            StringBuffer sb = new StringBuffer();
            try {
                Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface ni = netInterfaces.nextElement();
                    Enumeration<InetAddress> address = ni.getInetAddresses();
                    while (address.hasMoreElements()) {
                        InetAddress ip = address.nextElement();
                        if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
                            continue;
                        if (ip.isSiteLocalAddress())
                            mac = ni.getHardwareAddress();
                        else if (!ip.isLinkLocalAddress()) {
                            mac = ni.getHardwareAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

            if (mac != null) {
                for (int i = 0; i < mac.length; i++) {
                    sb.append(parseByte(mac[i]));
                }
                ElectronicApplication.getmIntent().setMac(sb.substring(0, sb.length() - 1));
                return sb.substring(0, sb.length() - 1);
            } else {
                return null;
            }
        }else{
            return ElectronicApplication.getmIntent().getMac();
        }
    }

    //获取当前连接网络的网卡的mac地址
    private static String parseByte(byte b) {
        String s = "00" + Integer.toHexString(b)+":";
        return s.substring(s.length() - 3);
    }

    /**
     * 获取版本号
     * @param ctx
     * @return
     */

    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

}
