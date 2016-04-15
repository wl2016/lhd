package com.xml.util;

public class StringUtil
{
  public static String stringFilter(String src)
  {
    if (src == null)
      return "";
    src = src.replaceAll("'", "");
    return src.trim();
  }
}