package com.util;

public class StringUtil
{
  public static String stringFilter(String src)
  {
    if (src == null)
      return "";
    src = src.replaceAll("'", "''");
    return src.trim();
  }

  public static String trimNull(String src) {
    return src == null ? "" : src.trim();
  }
}