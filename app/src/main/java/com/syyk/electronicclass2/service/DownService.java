package com.syyk.electronicclass2.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by fei on 2018/1/17.
 */

public class DownService extends Service {

    private Context mContext ;

    private String  downloadUrl ;

    private String fileName;

    private DownloadManager downloadManager ;
    private long mTaskId ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        fileName = DownConfig.fileName;
        downloadUrl = DownConfig.url;
        //文件存在则删除
        deleteFile();
        //启动下载
        downloadFile(downloadUrl,fileName);

    }

    private void deleteFile(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath()+"/"+fileName);
        if(file.exists()){
            StringUtils.showLog("存在");
            file.delete();
        }
    }

    /**
     * 使用系统的下载下载器的下载
     * @param Url
     * @param namePath
     */
    //使用系统下载器下载
    private void downloadFile(String Url, String namePath) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(namePath));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/download/", namePath);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            MessageBean bean = new MessageBean();
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    StringUtils.showLog(">>>下载暂停");
                    bean = new MessageBean();
                    bean.setMsgCode(DownConfig.DOWNFIAL);
                    EventBus.getDefault().post(bean);
                case DownloadManager.STATUS_PENDING:
                    StringUtils.showLog(">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    StringUtils.showLog(">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL :
                    StringUtils.showLog(">>>下载完成");
                    //下载完成的回调的方法
                    StringUtils.showLog(">>>下载成功");
                    bean.setMsgCode(DownConfig.DOWNSUCCESS);
                    EventBus.getDefault().post(bean);
                    break;
                case DownloadManager.STATUS_FAILED:
                    StringUtils.showLog(">>>下载失败");
                    bean = new MessageBean();
                    bean.setMsgCode(DownConfig.DOWNFIAL);
                    EventBus.getDefault().post(bean);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(receiver);
    }
}
