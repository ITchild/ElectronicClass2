package com.syyk.electronicclass2.bean;

/**
 * Created by fei on 2018/1/25.
 */

public class ScheduleBean {
    private String LessonDate;//课程日期
    private int SyllabusId;//课程ID
    private String ClassRoomName;
    private String TeacherName;//教师名字
    private String TeacherPhone;//教师电话
    private String TeacherCard;//教师卡
    private String AdminName;//管理员名称
    private String AdminPhone;//管理员电话
    private String AdminCard;//管理员卡
    private String CategoryName;//课程名称
    private String StartTime;//课程开始的时间
    private String EndTime;//课程结束时间
    private String Students;//学生总数
    private String Attendens;//签到人数
    private String Late;//迟到人数
    private String ClassName;//班级名称
    private String SysTime;//系统时间
    private int LessonNum;//课程编号


    public String getLessonDate() {
        return LessonDate;
    }

    public void setLessonDate(String lessonDate) {
        LessonDate = lessonDate;
    }

    public String getTeacherCard() {
        return TeacherCard;
    }

    public void setTeacherCard(String teacherCard) {
        TeacherCard = teacherCard;
    }

    public String getAdminCard() {
        return AdminCard;
    }

    public void setAdminCard(String adminCard) {
        AdminCard = adminCard;
    }

    public int getSyllabusId() {
        return SyllabusId;
    }

    public void setSyllabusId(int syllabusId) {
        SyllabusId = syllabusId;
    }

    public String getClassRoomName() {
        return ClassRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        ClassRoomName = classRoomName;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        TeacherName = teacherName;
    }

    public String getTeacherPhone() {
        return TeacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        TeacherPhone = teacherPhone;
    }

    public String getAdminName() {
        return AdminName;
    }

    public void setAdminName(String adminName) {
        AdminName = adminName;
    }

    public String getAdminPhone() {
        return AdminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        AdminPhone = adminPhone;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStudents() {
        return Students;
    }

    public void setStudents(String students) {
        Students = students;
    }

    public String getAttendens() {
        return Attendens;
    }

    public void setAttendens(String attendens) {
        Attendens = attendens;
    }

    public String getLate() {
        return Late;
    }

    public void setLate(String late) {
        Late = late;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getSysTime() {
        return SysTime;
    }

    public void setSysTime(String sysTime) {
        SysTime = sysTime;
    }

    public int getLessonNum() {
        return LessonNum;
    }

    public void setLessonNum(int lessonNum) {
        LessonNum = lessonNum;
    }
}
