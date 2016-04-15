package com.xml.util;

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

  public static String string2Date(String str) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try
    {
      date = sdf.parse(str);
    }
    catch (Exception e)
    {
      return getCurrentDate();
    }
    return sdf.format(date);
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

  public static String setRandomFileName()
  {
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