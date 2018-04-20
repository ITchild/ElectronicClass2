package com.dahuatech.netsdk.module;

import com.company.NetSDK.EM_LOGIN_SPAC_CAP_TYPE;
import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.dahuatech.netsdk.common.ToolKits;

/**
 * Created by 29779 on 2017/4/8.
 */
public class IPLoginModule {
    public static final int NET_USER_FLASEPWD_TRYTIME = FinalVar.NET_USER_FLASEPWD_TRYTIME;
    public static final int NET_LOGIN_ERROR_PASSWORD = FinalVar.NET_LOGIN_ERROR_PASSWORD;
    public static final int NET_LOGIN_ERROR_USER = FinalVar.NET_LOGIN_ERROR_USER;
    public static final int NET_LOGIN_ERROR_TIMEOUT = FinalVar.NET_LOGIN_ERROR_TIMEOUT;
    public static final int NET_LOGIN_ERROR_RELOGGIN = FinalVar.NET_LOGIN_ERROR_RELOGGIN;
    public static final int NET_LOGIN_ERROR_LOCKED = FinalVar.NET_LOGIN_ERROR_LOCKED;
    public static final int NET_LOGIN_ERROR_BLACKLIST = FinalVar.NET_LOGIN_ERROR_BLACKLIST;
    public static final int NET_LOGIN_ERROR_BUSY = FinalVar.NET_LOGIN_ERROR_BUSY;
    public static final int NET_LOGIN_ERROR_CONNECT = FinalVar.NET_LOGIN_ERROR_CONNECT;
    public static final int NET_LOGIN_ERROR_NETWORK = FinalVar.NET_LOGIN_ERROR_NETWORK;

    private long mLoginHandle;
    private NET_DEVICEINFO_Ex mDeviceInfo;
    private int mLoginType = 0;
    private int mErrorCode = 0;

    enum LOGIN_TYPE {
        IP, P2P
    }

    public IPLoginModule() {
        mLoginHandle = 0;
        setLoginType(LOGIN_TYPE.IP);
    }

    public void setLoginType(LOGIN_TYPE type) {
        if (LOGIN_TYPE.IP == type ) {
            mLoginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_MOBILE;
        } else if (LOGIN_TYPE.P2P == type) {
            mLoginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_P2P;
        }
    }

    public NET_DEVICEINFO_Ex getDeviceInfo() {
        return mDeviceInfo;
    }

    public long getLoginHandle() {
        return this.mLoginHandle;
    }

    public boolean login(String address, String port, String username, String password) {
        Integer err = new Integer(0);
        mDeviceInfo = new NET_DEVICEINFO_Ex();
        mLoginHandle = INetSDK.LoginEx2(address, Integer.parseInt(port), username, password, mLoginType, null, mDeviceInfo, err);
        if (0 == mLoginHandle) {
            mErrorCode = INetSDK.GetLastError();
            ToolKits.writeErrorLog("Failed to Login Device " + address);
            return false;
        }
        return true;
    }

    public boolean logout() {
        if (0 == mLoginHandle) {
            return  false;
        }

        boolean retLogout = INetSDK.Logout(mLoginHandle);
        if (retLogout) {
            mLoginHandle = 0;
        }

        return  retLogout;
    }

    public int errorCode() {
        return mErrorCode;
    }
}
