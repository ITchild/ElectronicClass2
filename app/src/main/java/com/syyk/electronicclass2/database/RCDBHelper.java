package com.syyk.electronicclass2.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2017/9/2.
 */
public class RCDBHelper {

    // 数据库名称
    private String DB_NAME = "roomevn.db";
    // 数据库版本
    private int DB_VERSION = 1;

    private SQLiteDatabase db;
    private SqliteHelper dbHelper;

    public RCDBHelper(Context context) {
        dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 保存刷卡信息
     * @param attenBean
     */
    public void saveCardId(AttenBean attenBean) {
        if(attenBean == null) {
            StringUtils.showToast("窗帘的设置参数为空");
            return;
        }
        db.execSQL("insert into atten (cardid, time,flag) values(?,?,?)",
                new String[]{attenBean.getCardid(), attenBean.getDatetime(),attenBean.getName()});
    }
    /**
     * 删除对应刷卡信息
     * @param time
     */
    public void deleteCardId(String time) {
        db.execSQL("delete from atten where time=?", new Object[]{time});
    }
    public void deleteAllCardId(){
        db.execSQL("delete from atten");
    }
    /**
     * 更新刷卡信息
     */
    public void upDareCardId(AttenBean attenBean){
        if(attenBean == null) {
            StringUtils.showToast("窗帘的设置参数为空");
            return;
        }
        db.execSQL("update atten set flag=? where time=?",
                new String[]{attenBean.getName(),attenBean.getDatetime()});
        Log.i("TEST","数据保存成功");
    }
    /**
     * 获取所有窗帘配置
     * @return
     */
    public List<AttenBean> getAllCardId() {
        List<AttenBean> attenBeans = new ArrayList<AttenBean>();
        Cursor cursor = db.rawQuery("select * from atten", null);
        cursor.moveToLast();
        if(cursor.getCount() != 0)
            do {
                AttenBean attenBean = new AttenBean();
                attenBean.setCardid(cursor.getString(cursor.getColumnIndex("cardid")));
                attenBean.setDatetime(cursor.getString(cursor.getColumnIndex("time")));
                attenBean.setName(cursor.getString(cursor.getColumnIndex("flag")));
                attenBeans.add(attenBean);
            }while (cursor.moveToPrevious());
        cursor.close();
        return attenBeans;
    }
    /**
     * 获取UID对应的窗帘配置
     * @return
     */
    public AttenBean getTimeCardIds(String time) {
        List<AttenBean> attenBeans = new ArrayList<AttenBean>();
        Cursor cursor = db.rawQuery("select * from atten where time=?",new String[]{time});
        while (cursor.moveToNext()) {
            AttenBean attenBean = new AttenBean();
            attenBean.setCardid(cursor.getString(cursor.getColumnIndex("cardid")));
            attenBean.setDatetime(cursor.getString(cursor.getColumnIndex("time")));
            attenBean.setName(cursor.getString(cursor.getColumnIndex("flag")));
            attenBeans.add(attenBean);
        }
        cursor.close();
        if(attenBeans != null && attenBeans.size()>0){
            return attenBeans.get(0);
        }else{
            return null;
        }
    }

    /**
     * 关闭资源
     */
    public void Close() {
        if (db != null)
            db.close();
        if (dbHelper != null)
            dbHelper.close();
    }
}
