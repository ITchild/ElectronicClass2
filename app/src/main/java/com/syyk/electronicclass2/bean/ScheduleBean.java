package com.syyk.electronicclass2.bean;

/**
 * Created by fei on 2018/1/25.
 */

public class ScheduleBean {
    private String _number;//第几节课
    private String _starttime;//开始时间
    private String _endtime;//结束时间
    private String course;//课程名字
    private String teacherName;//教室名字
    private String _c_id;//上课班级
    private String quantity;//课程人数

    public String get_number() {
        return _number;
    }

    public void set_number(String _number) {
        this._number = _number;
    }

    public String get_starttime() {
        return _starttime;
    }

    public void set_starttime(String _starttime) {
        this._starttime = _starttime;
    }

    public String get_endtime() {
        return _endtime;
    }

    public void set_endtime(String _endtime) {
        this._endtime = _endtime;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String get_c_id() {
        return _c_id;
    }

    public void set_c_id(String _c_id) {
        this._c_id = _c_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
