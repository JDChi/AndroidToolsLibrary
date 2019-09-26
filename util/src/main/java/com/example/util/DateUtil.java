package com.example.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式化工具类
 *
 * @author longmh
 */
public class DateUtil {
    // 返回格式
    public final static String DATE_SHORT = "yyyy-MM-dd";
    public final static String DATE_SHORT1 = "yyyy-M-d";
    public final static String DATE_SHORT2 = "yyyy.MM.dd";
    public final static String DATE_SHORT3 = "yyyy MM dd";
    public final static String DATE_SHORT4 = "yyyy年MM月dd日";
    public final static String DATE_SHORT5 = "M月dd日 m:ss";

    public final static String DATE_LONG = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_LONG1 = "yyyy-M-d HH:m:s";
    public final static String DATE_LONG2 = "yyyy.MM.dd HH:mm:ss";
    public final static String DATE_LONG3 = "yyyy.MM.dd HH:mm:ss";
    public final static String DATE_LONG4 = "yyyy-MM-dd-HH-mm-ss";
    public final static String DATE_LONG5 = "MM-dd HH:mm";
    public final static String DATE_LONG6 = "yyyyMMddHHmmss";
    public final static String DATE_LONG7 = "yyyy年MM月dd日 HH:mm";

    /**
     * @param fromType 你传入时间的格式
     * @param toType   你想转的格式
     * @param fromDate 你想处理的时间
     * @return 返回处理结果，String
     */
    public static String Date2String(String fromType, String toType,
                                     String fromDate) {
        String toDateStr = "";
        Date tempDate = null;
        SimpleDateFormat sdfFrom = null;
        SimpleDateFormat sdfTo = null;
        try {
            sdfFrom = new SimpleDateFormat(fromType);
            tempDate = sdfFrom.parse(fromDate);
        } catch (Exception e) {
            e.printStackTrace();
            tempDate = new Date();
        }

        sdfTo = new SimpleDateFormat(toType);
        toDateStr = sdfTo.format(tempDate);
        return toDateStr;
    }

    public static String Date2String(Date fromDate, String toType) {
        String toDateStr = "";
        SimpleDateFormat sdfFrom = null;
        sdfFrom = new SimpleDateFormat(toType);
        toDateStr = sdfFrom.format(fromDate);
        return toDateStr;
    }

    public static String StringToDateStr(String dateType, String toType) {
        Date date = new Date(toType);
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        return dateFormat.format(date);
    }


    /**
     * 获取系统当前时间
     *
     * @param toDate 你传入时间的格式
     * @return 返回处理结果，String
     */
    public static String Time2String(String toDate, String time) {
        String date = new SimpleDateFormat(toDate)
                .format(time);
        return date;
    }

    /**
     * 获取系统当前时间
     *
     * @param datetype 你传入时间的格式
     * @return 返回处理结果，String
     */
    public static String nowDate(String datetype) {
        String date = new SimpleDateFormat(datetype)
                .format(Calendar.getInstance().getTime());
        return date;
    }

    public static Date String2Date(String fromType, String dateStr) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(fromType);
        try {
            date = format.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            // 转行错误则返回当前系统日期
            date = Calendar.getInstance().getTime();
        }
        return date;

    }

    /**
     * 生日日期转时间戳
     *
     * @param shengri
     * @return
     */
    public static String shengriToTime(String shengri) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        String time = "";
        try {
            d = sdf.parse(shengri);
            long l = d.getTime();
            time = String.valueOf(l).substring(0, String.valueOf(l).length() - 3);
        } catch (Exception ex) {

        }
        return time;
    }

    /**
     * 获取时间戳
     */
    public static String getTime() {
        String time = System.currentTimeMillis() + "";
        return time.substring(0, time.length() - 3);
    }

    /**
     * 获取时间戳
     */
    public static long getUpdateTime() {
        long time = System.currentTimeMillis() + 600;
        return time;
    }

}
