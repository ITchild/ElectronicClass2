package com.syyk.electronicclass2.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
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
     * 保存课表信息
     * @param scheduleBean
     */
    public void saveSchedule(ScheduleBean scheduleBean) {
        if(scheduleBean == null) {
            return;
        }
        db.execSQL("insert into schedule " +
                "(SyllabusId, TeacherCard, AdminCard, " +
                "AdminName, AdminPhone, TeacherPhone, " +
                "LessonDate, ClassRoomName, TeacherName, " +
                "CategoryName, StartTime, EndTime, " +
                "LessonNum, Students, Attendens, Late, ClassName) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        scheduleBean.getSyllabusId(), scheduleBean.getTeacherCard(),scheduleBean.getAdminCard(),
                        scheduleBean.getAdminName(), scheduleBean.getAdminPhone(), scheduleBean.getTeacherPhone(),
                        scheduleBean.getLessonDate(), scheduleBean.getClassName(), scheduleBean.getTeacherName(),
                        scheduleBean.getCategoryName(), scheduleBean.getStartTime(), scheduleBean.getEndTime(),
                        scheduleBean.getLessonNum(), scheduleBean.getStudents(),scheduleBean.getAttendens(),
                        scheduleBean.getLate(),scheduleBean.getClassName()});
    }

    /**
     * 删除所有课表信息
     */
    public void deleteAllSchedule() {
        db.execSQL("delete from schedule");
    }

    /**
     * 根据日期获取课表信息
     * @return
     */
    public List<ScheduleBean> getDateSchedule(String date) {
        List<ScheduleBean> scheduleBeens = new ArrayList<ScheduleBean>();
        Cursor cursor = db.rawQuery("select * from schedule where LessonDate=?", new String []{date});
        cursor.moveToLast();
        if(cursor.getCount() != 0)
            do {
                ScheduleBean scheduleBean = new ScheduleBean();
                scheduleBean.setSyllabusId(cursor.getInt(cursor.getColumnIndex("SyllabusId")));
                scheduleBean.setTeacherCard(cursor.getString(cursor.getColumnIndex("TeacherCard")));
                scheduleBean.setAdminCard(cursor.getString(cursor.getColumnIndex("AdminCard")));
                scheduleBean.setAdminName(cursor.getString(cursor.getColumnIndex("AdminName")));
                scheduleBean.setAdminPhone(cursor.getString(cursor.getColumnIndex("AdminPhone")));
                scheduleBean.setTeacherPhone(cursor.getString(cursor.getColumnIndex("TeacherPhone")));
                scheduleBean.setClassRoomName(cursor.getString(cursor.getColumnIndex("ClassRoomName")));
                scheduleBean.setTeacherName(cursor.getString(cursor.getColumnIndex("TeacherName")));
                scheduleBean.setCategoryName(cursor.getString(cursor.getColumnIndex("CategoryName")));
                scheduleBean.setStartTime(cursor.getString(cursor.getColumnIndex("StartTime")));
                scheduleBean.setEndTime(cursor.getString(cursor.getColumnIndex("EndTime")));
                scheduleBean.setLessonNum(cursor.getInt(cursor.getColumnIndex("LessonNum")));
                scheduleBean.setStudents(cursor.getString(cursor.getColumnIndex("Students")));
                scheduleBean.setAttendens(cursor.getString(cursor.getColumnIndex("Attendens")));
                scheduleBean.setLate(cursor.getString(cursor.getColumnIndex("Late")));
                scheduleBean.setClassName(cursor.getString(cursor.getColumnIndex("ClassName")));
                scheduleBeens.add(scheduleBean);
            }while (cursor.moveToPrevious());
        cursor.close();
        return scheduleBeens;
    }


    /**
     * 获取所有课表信息
     * @return
     */
    public List<ScheduleBean> getAllSchedule() {
        List<ScheduleBean> scheduleBeens = new ArrayList<ScheduleBean>();
        Cursor cursor = db.rawQuery("select * from schedule", null);
        cursor.moveToLast();
        if(cursor.getCount() != 0)
            do {
                ScheduleBean scheduleBean = new ScheduleBean();
                scheduleBean.setSyllabusId(cursor.getInt(cursor.getColumnIndex("SyllabusId")));
                scheduleBean.setTeacherCard(cursor.getString(cursor.getColumnIndex("TeacherCard")));
                scheduleBean.setAdminCard(cursor.getString(cursor.getColumnIndex("AdminCard")));
                scheduleBean.setLessonDate(cursor.getString(cursor.getColumnIndex("LessonDate")));
                scheduleBean.setAdminName(cursor.getString(cursor.getColumnIndex("AdminName")));
                scheduleBean.setAdminPhone(cursor.getString(cursor.getColumnIndex("AdminPhone")));
                scheduleBean.setTeacherPhone(cursor.getString(cursor.getColumnIndex("TeacherPhone")));
                scheduleBean.setClassRoomName(cursor.getString(cursor.getColumnIndex("ClassRoomName")));
                scheduleBean.setTeacherName(cursor.getString(cursor.getColumnIndex("TeacherName")));
                scheduleBean.setCategoryName(cursor.getString(cursor.getColumnIndex("CategoryName")));
                scheduleBean.setStartTime(cursor.getString(cursor.getColumnIndex("StartTime")));
                scheduleBean.setEndTime(cursor.getString(cursor.getColumnIndex("EndTime")));
                scheduleBean.setLessonNum(cursor.getInt(cursor.getColumnIndex("LessonNum")));
                scheduleBean.setStudents(cursor.getString(cursor.getColumnIndex("Students")));
                scheduleBean.setAttendens(cursor.getString(cursor.getColumnIndex("Attendens")));
                scheduleBean.setLate(cursor.getString(cursor.getColumnIndex("Late")));
                scheduleBean.setClassName(cursor.getString(cursor.getColumnIndex("ClassName")));
                scheduleBeens.add(scheduleBean);
            }while (cursor.moveToPrevious());
        cursor.close();
        return scheduleBeens;
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
