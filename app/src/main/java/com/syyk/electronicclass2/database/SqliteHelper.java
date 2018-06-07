package com.syyk.electronicclass2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wugang on 2016/9/2.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE atten (id integer primary key autoincrement, cardid varchar(50), syllabusid varchar(50))");

        db.execSQL("CREATE TABLE schedule (id integer primary key autoincrement, " +
                "SyllabusId integer, " +
                "TeacherCard varchar(20), " +
                "AdminName varchar(15), " +
                "AdminPhone varchar(15), " +
                "AdminCard varchar(20), " +
                "LessonDate varchar(20), " +
                "ClassRoomName varchar(20), " +
                "TeacherName varchar(10), " +
                "TeacherPhone carchar(20), " +
                "CategoryName varchar(10), " +
                "StartTime varchar(15), " +
                "EndTime varchar(15), " +
                "LessonNum integer, " +
                "Students varchar(10), " +
                "Attendens varchar(10), " +
                "Late varchar(10), " +
                "ClassName varchar(20))");
        db.execSQL("CREATE TABLE super (id integer primary key autoincrement, " +
                "CardId varchar(20), " +
                "Name varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
