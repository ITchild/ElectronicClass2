package com.dahuatech.netsdk.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.syyk.electronicclass2.ElectronicApplication;

/**
 * Created by 29779 on 2017/4/10.
 */
public class ToolKits {
    public static void showMessage(Context context , String strLog)
    {
        Toast.makeText(context, strLog, Toast.LENGTH_SHORT).show();
    }

    public static void showErrorMessage(Context context , String strLog)
    {
        Toast.makeText(context, strLog +
                        String.format(" [0x%x]" , INetSDK.GetLastError()),
                Toast.LENGTH_SHORT).show();
    }

    public static void writeLog(String strLog)
    {
        Log.d("NetSDK Demo", strLog);
    }

    public static void writeErrorLog(String strLog)
    {
        Log.e("NetSDK Demo", strLog +
                String.format(" Last Error Code [%x]" , INetSDK.GetLastError()) );
    }

    public static boolean SetDevConfig(String strCmd ,  Object cmdObject , long hHandle , int nChn , int nBufferLen )
    {
        boolean result = false;
        Integer error = new Integer(0);
        Integer restart = new Integer(0);
        char szBuffer[] = new char[nBufferLen];
        for(int i=0; i<nBufferLen; i++)szBuffer[i]=0;

        if(INetSDK.PacketData(strCmd, cmdObject, szBuffer, nBufferLen))
        {
            if( INetSDK.SetNewDevConfig(hHandle,strCmd , nChn , szBuffer, nBufferLen, error, restart, 10000))
            {
                result = true;
            }
            else
            {
                writeErrorLog("Set " + strCmd + " Config Failed!");
                result = false;
            }
        }
        else
        {
            writeErrorLog("Packet " + strCmd + " Config Failed!");
            result = false;
        }

        return result;
    }

    public static boolean GetDevConfig(String strCmd ,  Object cmdObject , long hHandle , int nChn , int nBufferLen)
    {
        boolean result = false;
        Integer error = new Integer(0);
        char szBuffer[] = new char[nBufferLen];

        if(INetSDK.GetNewDevConfig( hHandle, strCmd , nChn, szBuffer,nBufferLen,error,10000) )
        {
            if( INetSDK.ParseData(strCmd ,szBuffer , cmdObject , null ) )
            {
                result = true;
            }
            else
            {
                writeErrorLog("Parse " + strCmd + " Config Failed!");
                result = false;
            }
        }
        else
        {
            writeErrorLog("Get" + strCmd + " Config Failed!");
            result = false;
        }
        return result;
    }

    public static String CharArrayToString(char[] szIn , String strMode){
        try {
            byte[] tmpByte = new byte[FinalVar.SDK_NEW_USER_NAME_LENGTH];
            for( int i=0 ; i<szIn.length ; i++)
            {
                tmpByte[i] = (byte) szIn[i];
            }

            return new String(tmpByte , strMode);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static char[] StringToCharArray(String strIn, String strMode) {
        try {
            byte[] tempByte = strIn.getBytes(strMode);
            char[] cOut = new char[FinalVar.SDK_NEW_USER_NAME_LENGTH];
            for (int i = 0; i < tempByte.length; i++) {
                cOut[i] = (char)tempByte[i];
            }
            return cOut;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    public static String ByteArrayToString(byte[] szIn){
        int i = 0;
        for (i = 0; i < szIn.length; i++) {
            if (0 == (byte)szIn[i]) {
                break;
            }
        }

        if (i > 0) {
            return new String(szIn, 0, i);
        }

        return null;
    }

    //createFile
    public static boolean createFile(String strPath, String strFile) {
        File path = new File(strPath);
        if (!path.exists()) {
            try {
                if (!path.mkdirs()) {
                    ToolKits.writeLog("App can't create path " + strPath);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File file = new File(strPath + strFile);
        if (file.exists()) {
            file.delete();
        }

        try {
            if (!file.createNewFile()) {
                ToolKits.writeLog("App can't crete file " + strPath + strFile);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static class SimpleAsyncTask<T> extends AsyncTask<Void, Integer, T>  {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected T  doInBackground(Void... params) {
            return null;
        }
        @Override
        protected void onPostExecute(T result) {
        }
    };

    public static void alertDisconnected(){
        final Context context = ElectronicApplication.DHActivityManager.getManager().getTopActivity();
        if (context == null)
            return;
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(context);
        builder.setMessage("您的设备已断线,确定退出吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(0);
//                Intent intent = new Intent(context,MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(intent);
            }
        });
        builder.show();
    }
}












