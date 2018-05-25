package com.syyk.electronicclass2.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具
 * Created by xiaowei on 2017/5/22.
 */

public class DateTimeUtil {
    /**
     * 准备第一个模板，从字符串中提取出日期数字
     */
    private static String pat1 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 准备第二个模板，将提取后的日期数字变为指定的格式
     */
    private static String pat2 = "yyyy年MM月dd日 HH:mm:ss";
    /**
     * 实例化模板对象
     */
    private static SimpleDateFormat sdf1 = new SimpleDateFormat(pat1);
    private static SimpleDateFormat sdf2 = new SimpleDateFormat(pat2);
    private static long mExitTime = 0;

    /**
     * 获取格式化当前时间
     *
     * @return
     */
    public static String getCurFormatStrTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取格式化当前时间
     * @return
     */
    public static String getCurFormatStrTime(String format,long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timeMillis));
    }
    /**
     * 获取系统格式化当前时间
     * @return
     */
    public static String getCurFormatStrTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 改变时间，mill为负值则前移时间，反之相反
     * @param time
     * @param mill
     * @return
     */
    public static String changeTime(String time,int mill){
        long startTimeL = getCurFormat2Millis("HH:mm:ss",time);
        return getCurFormatStrTime("HH:mm:ss",startTimeL + mill);
    }
    /**
     * 日期加或减
     * @param date
     * @param day
     * @return
     */
    public static String changeDate(String date,int day){
        long startTimeL = getCurFormat2Millis("yyyy-MM-dd",date);
        return getCurFormatStrTime("yyyy-MM-dd",startTimeL + (86400000*day));
    }

    /**
     * 根据日期获取毫秒数
     */
    public static long getCurFormat2Millis(String format,String stringDate){
        //先把字符串转成Date类型 "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //此处会抛异常
        Date date = null;
        try {
            date = sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //获取毫秒数
        return date.getTime();

    }
    /**
     * 根据日期获取周几
     */
    public static String getCurFormatWeek(String format,String stringDate){
        long startTimeL = getCurFormat2Millis(format,stringDate);
        return getCurFormatStrTime("E",startTimeL);
    }

    public static boolean getTimeBetewnTimes(String format,String startTime,String endTime,String time){
        long startTimeL = getCurFormat2Millis(format,startTime);
        long endTimeL = getCurFormat2Millis(format,endTime);
        long timeL = getCurFormat2Millis(format,time);
        if(timeL>startTimeL && timeL<endTimeL){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 此方法仅适用于获取课表时
     * @param format
     * @return
     */
    public static String delete0(String format){
        String date = getCurFormatStrTime(format);
        String[] datearr = date.split("/");
        if(datearr[1].startsWith("0")){
            datearr[1] = datearr[1].replace("0","");
        }
        if(datearr[2].startsWith("0")){
            datearr[2] = datearr[2].replace("0","");
        }
        return datearr[0]+"/"+datearr[1]+"/"+datearr[2];
    }

    /**
     * 获取格式化当前时间
     *
     * @return
     */
    public static Date getCurFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            return sdf.parse(getCurFormatStrTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取格式化当前时间
     *
     * @return
     */
    public static Date getCurFormatDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(getCurFormatStrTime(format));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取格式化当前时间
     *
     * @return
     */
    public static String getCurFormatTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public static Long farmatTime(String string) {
        Date date = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = Date(sf.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 将先有时间转变成秒
     * @param format
     * @param sdate
     * @return
     */
    public static Long getFarmatTime(String format,String sdate) {
        Date date = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat(format);
            date = Date(sf.parse(sdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime()/1000;
    }

    public static Date Date(Date date) {
        Date datetimeDate;
        datetimeDate = new Date(date.getTime());
        return datetimeDate;
    }

    public static String getTime(String commitDate) {
        // 在主页面中设置当天时间
        Date nowTime = new Date();
        String currDate = sdf1.format(nowTime);
        Date date = null;
        try {
            if (commitDate.length() > 19) {
                commitDate = commitDate.substring(0, 18);
            }
            if (commitDate.length() == 16) {
                StringBuffer buffer = new StringBuffer(commitDate);
                buffer.append(":00");
                commitDate = buffer.toString();
            }
            // 将给定的字符串中的日期提取出来
            date = sdf1.parse(commitDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int nowDate = Integer.valueOf(currDate.substring(8, 10));
        int commit = Integer.valueOf(commitDate.substring(8, 10));

        String monthDay = sdf2.format(date).substring(5, 12);
        String yearMonthDay = sdf2.format(date).substring(0, 12);
        int month = Integer.valueOf(monthDay.substring(0, 2));
        int day = Integer.valueOf(monthDay.substring(3, 5));
        if (month < 10 && day < 10) {
            monthDay = monthDay.substring(1, 3) + monthDay.substring(4);
        } else if (month < 10) {
            monthDay = monthDay.substring(1);
        } else if (day < 10) {
            monthDay = monthDay.substring(0, 3) + monthDay.substring(4);
        }
        int yearMonth = Integer.valueOf(yearMonthDay.substring(5, 7));
        int yearDay = Integer.valueOf(yearMonthDay.substring(8, 10));
        if (yearMonth < 10 && yearDay < 10) {
            yearMonthDay = yearMonthDay.substring(0, 5)
                    + yearMonthDay.substring(6, 8) + yearMonthDay.substring(9);
        } else if (yearMonth < 10) {
            yearMonthDay = yearMonthDay.substring(0, 5)
                    + yearMonthDay.substring(6);
        } else if (yearDay < 10) {
            yearMonthDay = yearMonthDay.substring(0, 8)
                    + yearMonthDay.substring(9);
        }
        String str = " 00:00:00";
        float currDay = farmatTime(currDate.substring(0, 10) + str);
        float commitDay = farmatTime(commitDate.substring(0, 10) + str);
        int currYear = Integer.valueOf(currDate.substring(0, 4));
        int commitYear = Integer.valueOf(commitDate.substring(0, 4));
        int flag = (int) (farmatTime(currDate) / 1000 - farmatTime(commitDate) / 1000);
        String des = null;
        String hourMin = commitDate.substring(11, 16);
        int temp = flag;
        if (temp < 60) {
            System.out.println("A");
            if (commitDay < currDay) {
                des = "昨天  " + hourMin;
            } else {
                des = "刚刚";
            }
        } else if (temp < 60 * 60) {
            System.out.println("B");
            if (commitDay < currDay) {
                des = "昨天  " + hourMin;
            } else {
                des = temp / 60 + "分钟前";
            }
        } else if (temp < 60 * 60 * 24) {
            System.out.println("C");
            int hour = temp / (60 * 60);
            if (commitDay < currDay) {
                des = "昨天  " + hourMin;
            } else {
                if (hour < 6) {
                    des = hour + "小时前";
                } else {
                    des = hourMin;
                }
            }
        } else if (temp < (60 * 60 * 24 * 2)) {
            System.out.println("D");
            if (nowDate - commit == 1) {
                des = "昨天  " + hourMin;
            } else {
                des = "前天  " + hourMin;
            }
        } else if (temp < 60 * 60 * 60 * 3) {
            System.out.println("E");
            if (nowDate - commit == 2) {
                des = "前天  " + hourMin;
            } else {
                if (commitYear < currYear) {
                    des = yearMonthDay + hourMin;
                } else {
                    des = monthDay + hourMin;
                }
            }
        } else {
            System.out.println("F");
            if (commitYear < currYear) {
                des = yearMonthDay + hourMin;
            } else {
                des = monthDay + hourMin;
            }
        }
        if (des == null) {
            des = commitDate;
        }
        return des;
    }

    /**
     * 根据日期获取当期是周几
     */
    public static String getWeek(Date date) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 根据日期获取当期是周几
     */
    public static String getWeek(String date) {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(stringToDate("yyyy-MM-dd", date));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 获取当前日期
     *
     * @param format
     * @return
     */
    public static String getCurDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static Date getCurDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前星期
     *
     * @return
     */
    public static String getCurWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(new Date());
    }

    public static String dateToString(String format, Date date) {
        if (date == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date formatDate(String format, Date date) {
        if (date == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateToString(format, date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurStrTimeMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public static Date getCurDateTimeMillis() {
        Date date = new Date(System.currentTimeMillis());
        return date;
    }

    public static Date longToDate(String format, long aLong) {
        Date oldDate = new Date(aLong);
        String strDate = dateToString(format, oldDate);
        return stringToDate(format, strDate);
    }

    public static String longtoStr(String format, long time) {
        Date date = longToDate(format, time);
        String strDate = dateToString(format, date);
        return strDate;
    }

    public static Date stringToDate(String format, String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return date;
    }

    /**
     * 前一天
     *
     * @param time
     * @param day
     * @return
     */
    public static long beforeDay(long time, int day) {
        return new Date(time - day * 24 * 3600 * 1000).getTime();
    }

    /**
     * 前一天
     *
     * @param time
     * @param day
     * @return
     */
    public static Date beforeDayD(long time, int day) {
        return new Date(time - day * 24 * 3600 * 1000);
    }

    /**
     * 前一天
     *
     * @param date
     * @param day
     * @return
     */
    public static long beforeDay(Date date, int day) {
        return new Date(date.getTime() - day * 24 * 3600 * 1000).getTime();
    }

    /**
     * 前一天
     *
     * @param date
     * @param day
     * @return
     */
    public static Date beforeDayD(Date date, int day) {
        return new Date(date.getTime() - day * 24 * 3600 * 1000);
    }

    /**
     * 前一天
     *
     * @param pattern
     * @param strDate
     * @param day
     * @return
     */
    public static String beforeDayStr(String pattern, String strDate, int day) {
        Date date = stringToDate(pattern, strDate);
        date = new Date(date.getTime() - day * 24 * 3600 * 1000);
        return dateToString(pattern, date);
    }

    /**
     * 后一天
     *
     * @param time
     * @param day
     * @return
     */
    public static long afterDay(long time, int day) {
        return new Date(time + day * 24 * 3600 * 1000).getTime();
    }

    /**
     * 后一天
     *
     * @param time
     * @param day
     * @return
     */
    public static Date afterDayD(long time, int day) {
        return new Date(time + day * 24 * 3600 * 1000);
    }

    /**
     * 后一天
     *
     * @param date
     * @param day
     * @return
     */
    public static long afterDay(Date date, int day) {
        return new Date(date.getTime() + day * 24 * 3600 * 1000).getTime();
    }

    /**
     * 后一天
     *
     * @param date
     * @param day
     * @return
     */
    public static Date afterDayD(Date date, int day) {
        return new Date(date.getTime() + day * 24 * 3600 * 1000);
    }

    /**
     * 后一天
     *
     * @param pattern
     * @param strDate
     * @param day
     * @return
     */
    public static String afterDayStr(String pattern, String strDate, int day) {
        Date date = stringToDate(pattern, strDate);
        date = new Date(date.getTime() + day * 24 * 3600 * 1000);
        return dateToString(pattern, date);
    }

    /**
     * 返回二个时间相差的分分钟数,如果一个为空，返回为0；
     *
     * @param startDate，比如08：09
     * @param endDate，如18：09
     * @return
     */
    public static int getMinutesDiff(String startDate, String endDate) {
        int ret = 0;
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            return ret;
        } else {
            String startDateStr[] = startDate.split(":");
            String endDateStr[] = endDate.split(":");
            if (startDateStr[0].startsWith("0")) {
                startDateStr[0] = startDateStr[0].substring(1);
            }
            if (startDateStr[1].startsWith("0")) {
                startDateStr[1] = startDateStr[1].substring(1);
            }
            if (endDateStr[0].startsWith("0")) {
                endDateStr[0] = endDateStr[0].substring(1);
            }
            if (endDateStr[1].startsWith("0")) {
                endDateStr[1] = endDateStr[1].substring(1);
            }
            int s = Integer.parseInt(startDateStr[0]) * 60 + Integer.parseInt(startDateStr[1]);
            int e = Integer.parseInt(endDateStr[0]) * 60 + Integer.parseInt(endDateStr[1]);
            ret = e - s;
        }
        return ret;
    }

    /**
     * 返回二个时间相差的分分钟数,如果一个为空，返回为0；
     *
     * @param startDate，比如08：09
     * @param endDate，如18：09
     * @return
     */
    public static int getMinutesDiff(String format, Date startDate, Date endDate) {
        int ret = -1;
        if (null == startDate || null == endDate) {
            return ret;
        } else {
            String startDateStr[];
            String endDateStr[];
            if (null == format) {
                startDateStr = dateToString("HH:mm:ss", startDate).split(":");
                endDateStr = dateToString("HH:mm:ss", endDate).split(":");
            } else {
                startDateStr = dateToString(format, startDate).split(":");
                endDateStr = dateToString(format, endDate).split(":");
            }
            if (startDateStr[0].startsWith("0")) {
                startDateStr[0] = startDateStr[0].substring(1);
            }
            if (startDateStr[1].startsWith("0")) {
                startDateStr[1] = startDateStr[1].substring(1);
            }
            if (startDateStr[2].startsWith("0")) {
                startDateStr[2] = startDateStr[2].substring(1);
            }
            if (endDateStr[0].startsWith("0")) {
                endDateStr[0] = endDateStr[0].substring(1);
            }
            if (endDateStr[1].startsWith("0")) {
                endDateStr[1] = endDateStr[1].substring(1);
            }
            if (endDateStr[2].startsWith("0")) {
                endDateStr[2] = endDateStr[2].substring(1);
            }
            int s = Integer.parseInt(startDateStr[0]) * 60 * 60 + Integer.parseInt(startDateStr[1]) * 60 + Integer.parseInt(startDateStr[2]);
            int e = Integer.parseInt(endDateStr[0]) * 60 * 60 + Integer.parseInt(endDateStr[1]) * 60 + Integer.parseInt(endDateStr[2]);
            ret = e - s;
        }
        return ret;
    }

    /**
     * 过了ms毫秒吗？
     *
     * @param ms 毫秒
     */
    public static boolean secondsLater(int ms) {
        if (System.currentTimeMillis() - mExitTime > ms) {
            mExitTime = System.currentTimeMillis();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 格式转换
     *
     * @param source  原来格式 yyyy-MM-dd
     * @param target  目标格式 yyyy年MM月dd日
     * @param strDate
     * @return
     */
    public static String formatConvert(String source, String target, String strDate) {
        Date date = stringToDate(source, strDate);
        SimpleDateFormat sdf = new SimpleDateFormat(target);
        return sdf.format(date);
    }
}
