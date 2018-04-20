package com.dahuatech.netsdk.common;

/**
 * Created by 29779 on 2017/4/12.
 */
public class PrefsConstants {
    public static final String LOGIN_PREFIX = "login.";
    public static final String P2P_PREFIX = "p2p.";

    /// ip login
    public static final String LOGIN_IP = LOGIN_PREFIX + "ip";
    public static final String LOGIN_PORT = LOGIN_PREFIX + "port";
    public static final String LOGIN_USERNAME = LOGIN_PREFIX + "username";
    public static final String LOGIN_PASSWORD = LOGIN_PREFIX + "password";
    public static final String LOGIN_CHECK = LOGIN_PREFIX + "check";

    ///  p2p login
    public static final String P2P_SERVER_IP = P2P_PREFIX + "server_ip";
    public static final String P2P_SERVER_PORT = P2P_PREFIX + "server_port";
    public static final String P2P_SERVER_USERNAME = P2P_PREFIX + "server_username";
    public static final String P2P_SERVER_PASSWORD = P2P_PREFIX + "server_password";
    public static final String P2P_DEVICE_ID = P2P_PREFIX + "device_id";
    public static final String P2P_DEVICE_PORT = P2P_PREFIX + "device_port";
    public static final String P2P_DEVICE_USERNAME = P2P_PREFIX + "device_username";
    public static final String P2P_DEVICE_PASSWORD = P2P_PREFIX + "device_password";
    public static final String P2P_CHECK = P2P_PREFIX + "check";
}
