package com.syyk.electronicclass2.serialport;

/**
 * Created by fei on 2017/6/1.
 */
public interface ISerialPortConnection {
    void onStart(String startResult);
    void onMessage(String message);
    void onHexStrMessage(String hexMessage);
}
