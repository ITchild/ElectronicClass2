package com.syyk.electronicclass2.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.ActionBar.LayoutParams;

import com.syyk.electronicclass2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fei on 2018/4/27.
 */

public class UpdateManger {
    /**
     * 版本更新
     */
    private Context mContext;
    private String apkUrl;
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private TextView tv;
    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE://下载进度条的更新
                    tv.setText(progress + "%");
                    mProgress.setProgress(progress);
                    if (progress == 99) {
                        downloadDialog.dismiss();
                    }
                    break;
                case DOWN_OVER://下载完成
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    public UpdateManger(Context mcontext, String url) {
        mContext = mcontext;
        apkUrl = url;
    }


    public void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.app_name);
        builder.setMessage("发现最新版本，请更新！");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.app_name);

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        tv = (TextView) v.findViewById(R.id.tv);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(Catition.UpDateApkUrl);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = Catition.UpDateApkName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(Catition.UpDateApkName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);// 显示用户数据
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    public static void removeApk() {
        File file = new File(Catition.UpDateApkName);
        if (null != file && file.exists()) {
            file.delete();
        }
    }

}
