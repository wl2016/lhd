package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormat
{
  public static String getCurrentDate()
  {
    Date date = new Date();

    Calendar cd = Calendar.getInstance();
    cd.setTime(new Date());
    date = cd.getTime();
    SimpleDateFormat lFormat = new SimpleDateFormat("yyyy-MM-dd");
    String gRtnStr = lFormat.format(date);
    return gRtnStr;
  }

  public static String string2Date(String str)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try
    {
      date = sdf.parse(str);
      return sdf.format(date);
    }
    catch (Exception localException)
    {
    }

    return "";
  }

  public static String randomEncodeing() {
    Date date = new Date();

    Calendar cd = Calendar.getInstance();
    cd.setTime(new Date());
    date = cd.getTime();
    SimpleDateFormat lFormat = new SimpleDateFormat("yyMMddhhmm");
    String gRtnStr = lFormat.format(date);
    return gRtnStr + Math.round(Math.random() * 10.0D);
  }

  public static String getTime()
  {
    Date date = new Date();

    Calendar cd = Calendar.getInstance();
    cd.setTime(new Date());
    date = cd.getTime();
    SimpleDateFormat lFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String gRtnStr = lFormat.format(date);
    return gRtnStr;
  }

  public static String formatDate(String src)
  {
    if ((src.length() != 8) || (src == null)) {
      return "";
    }
    int iy = 0; int im = 0; int id = 0;
    String year = src.substring(0, 4);
    String month = src.substring(4, 6);
    String day = src.substring(6, 8);
    src = year + "-" + month + "-" + day;
    return string2Date(src);
  }

  public static String setRandomFileName() {
    Date date = new Date();

    Calendar cd = Calendar.getInstance();
    cd.setTime(new Date());
    date = cd.getTime();
    SimpleDateFormat lFormat = new SimpleDateFormat("yyMMddhhmmss");
    String gRtnStr = lFormat.format(date);
    return gRtnStr + Math.round(Math.random() * 1000.0D);
  }

  public static String creatTempCodeId() {
    Date date = new Date();

    Calendar cd = Calendar.getInstance();
    cd.setTime(new Date());
    date = cd.getTime();
    SimpleDateFormat lFormat = new SimpleDateFormat("yyyyMMddhhmmssmmm");
    String gRtnStr = lFormat.format(date);
    return gRtnStr + Math.round(Math.random() * 999999999.0D);
  }
}