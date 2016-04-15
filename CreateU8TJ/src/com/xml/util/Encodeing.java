package com.xml.util;

public class Encodeing
{
  public static String Unicode2Chartset(String strIn, String chartset)
  {
    String strOut = null;
    if ((strIn == null) || (strIn.trim().equals("")))
    {
      return strIn;
    }
    try
    {
      byte[] b = strIn.getBytes("ISO-8859-1");
      strOut = new String(b, chartset);
    }
    catch (Exception localException)
    {
    }

    return strOut;
  }
}