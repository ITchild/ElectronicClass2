package com.syyk.electronicclass2.serialport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.friendlyarm.AndroidSDK.HardwareControler;

import java.io.UnsupportedEncodingException;

/**
 * 窗口通信类 注意：需先setMessageListener，然后connect Created by fei on 2017/6/1
 */
public class SerialPortConnection {
    private ISerialPortConnection iSPConnection;
    private int fd;
    private Thread mReadThread;
    byte[] buf = new byte[512];
    public static SerialPortConnection mSPConnection;

    public static SerialPortConnection getInstance() {
        if (mSPConnection == null) {
            mSPConnection = new SerialPortConnection();
        }
        return mSPConnection;
    }
    public void setMessageListener(ISerialPortConnection iSPConnection) {
        this.iSPConnection = iSPConnection;
    }
    public void connect(int fda, String devName, long baud, int dataBits, int stopBits,String parityBit,String flowCtrl) {
        this.fd = fda;
        if (fd == -1)
            fd = HardwareControler.openSerialPortEx(devName, baud, dataBits, stopBits,parityBit,flowCtrl);
        if (fd != -1) {
            iSPConnection.onStart("串口启动成功");
        } else {
            iSPConnection.onStart("串口启动失败");
        }
        if (mReadThread == null) {
            final int finalFd = fd;
            mReadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (HardwareControler.select(finalFd, 0, 0) == 1) {
                            int retSize = HardwareControler.read(finalFd, buf, buf.length);
                            if (retSize > 0) {
                                String mStrMessage = new String(buf, 0, retSize);
                                String mHexStrMessage = bytesToHexString(buf, retSize);
                                Message msg = new Message();
                                msg.what = 100;
                                Bundle bundle = new Bundle();
                                bundle.putString("data", mStrMessage);
                                bundle.putString("data_hex", mHexStrMessage);
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                }
            });
            mReadThread.start();
        }
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if (iSPConnection == null)
                        break;
                    iSPConnection.onMessage(msg.getData().getString("data"));
                    iSPConnection.onHexStrMessage(msg.getData().getString("data_hex"));
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 向串口发送数据 wug
     * @param str 字符串
     */
    public void write(String str) {
        try {
            HardwareControler.write(fd, str.getBytes());
            Log.i("TEST","send_success");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TEST","send_feild");
        }
    }
    /**
     * 向串口发送数据 wug
     * @param str 字符串
     */
    public void writeHex(String str) {
        try {
            HardwareControler.write(fd, hexStringToBytes(str));
            Log.i("TEST","send_success");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TEST","send_feild");
        }
    }
    /**
     * 向串口发送数据 wug
     * @param str 字符串
     */
    public void writeByte(String str, int delaytime) {
        try {
            Thread.sleep(delaytime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HardwareControler.write(fd, hexStringToBytes(str));
    }
    /**
     * 延时向串口发送数据
     * @param str
     * @param delaytime
     */
    public void write(final String str, final int delaytime) {
        try {
            Thread.sleep(delaytime);
            HardwareControler.write(fd, str.getBytes("utf-8"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /**
     * 释放串口资源
     */
    public void release() {
        HardwareControler.close(fd);
        fd = -1;
        mReadThread = null;
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
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

    public static String bytesToHexString(byte[] src, int retSize) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < retSize; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
