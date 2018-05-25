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
            StringUtils.showToast("保存刷卡信息不能为空");
            return;
        }
        db.execSQL("insert into atten (cardid, syllabusid) values(?,?)",
                new String[]{attenBean.getCardid(), attenBean.getSyllabusid()});
    }
    /**
     * 删除对应刷卡信息
     * @param cardid
     * @param syllabusid
     */
    public void deleteCardId(String cardid,String syllabusid) {
        db.execSQL("delete from atten where cardid=? and syllabusid=?", new Object[]{cardid,syllabusid});
    }
    /**
     * 获取所有刷卡信息
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
                attenBean.setSyllabusid(cursor.getString(cursor.getColumnIndex("syllabusid")));
                attenBeans.add(attenBean);
            }while (cursor.moveToPrevious());
        cursor.close();
        return attenBeans;
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
