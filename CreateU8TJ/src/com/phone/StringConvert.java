package com.phone;

import com.xml.Constant;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringConvert
{
  public static String toStringHex(String s)
  {
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++)
    {
      try
      {
        baKeyword[i] = 
          (byte)(0xFF & 
          Integer.parseInt(s
          .substring(i * 2, 
          i * 2 + 2), 16));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    try
    {
      s = new String(baKeyword, "utf-8");
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
    return s;
  }

  public static byte[] toStringByte(String s)
  {
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++)
    {
      try
      {
        baKeyword[i] = 
          (byte)(0xFF & 
          Integer.parseInt(s
          .substring(i * 2, 
          i * 2 + 2), 16));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    return baKeyword;
  }

  public static String toHexString(String s)
  {
    String str = "";
    for (int i = 0; i < s.length(); i++)
    {
      int ch = s.charAt(i);
      String s4 = Integer.toHexString(ch);
      str = str + s4 + " ";
    }
    return str;
  }

  public static String byte2hex(byte[] b)
  {
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++)
    {
      stmp = Integer.toHexString(b[n] & 0xFF);
      if (stmp.length() == 1)
        hs = hs + "0" + stmp;
      else
        hs = hs + stmp;
    }
    return hs;
  }

  public static byte[] hex2byte(String str)
  {
    if (str == null)
      return null;
    str = str.trim();
    int len = str.length();
    if ((len == 0) || (len % 2 == 1)) {
      return null;
    }
    byte[] b = new byte[len / 2];
    try
    {
      for (int i = 0; i < str.length(); i += 2)
      {
        b[(i / 2)] = 
          (byte)Integer.decode("0x" + str.substring(i, i + 2)).intValue();
      }
      return b;
    }
    catch (Exception e) {
    }
    return null;
  }

  public static void printHexString(byte[] b)
  {
    String rstr = "";
    for (int i = 0; i < b.length; i++) {
      String hex = Integer.toHexString(b[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      rstr = rstr + hex;
      System.out.print(hex + " ");
    }
    System.out.println("\n 十六进数" + rstr);
  }

  public static String Bytes2HexString(byte[] b)
  {
    String ret = "";
    for (int i = 0; i < b.length; i++) {
      String hex = Integer.toHexString(b[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      ret = ret + hex.toUpperCase();
    }
    System.out.println("\n 十六进数" + ret);
    return ret;
  }

  public static void main(String[] args)
  {
    String msg = ".AbcdD62978762Co!@#$%^&*()_";

    String hexStr = ""; String msgStr = ""; String binaryStr = "";
    byte[] b = msg.getBytes();
    for (int i = 0; i < b.length; i++)
    {
      System.out.print(b[i] + " ");
    }

    System.out.print("\n十进制转换为十六进制" + msgStr);
    for (int i = 0; i < b.length; i++)
    {
      char c = msg.charAt(i);
      hexStr = hexStr + c;
      System.out.print(Integer.toHexString(c) + " ");
    }

    b = hexStr.getBytes();
    for (int i = 0; i < b.length; i++)
    {
      char c = hexStr.charAt(i);
      msgStr = msgStr + c;
    }
    System.out.print("\n十六进制转换为十进制" + msgStr);
    System.out.print("\n二进制数");
    for (int i = 0; i < b.length; i++)
    {
      char c = msg.charAt(i);

      binaryStr = binaryStr + c;
      System.out.print(Integer.toBinaryString(c) + " ");
    }
    int bibt = Integer.parseInt(Integer.valueOf("101110", 2).toString());
    System.out.print("\n二进制转换为十进制.");

    String src = "00088000";
    b = src.getBytes();
    System.out.print("\n16进制串转换成字符\b");
    for (int i = 0; i < b.length - 2; i++);
    String str = "2*4+3";
    b = str.getBytes();
    float s = 0.0F;
    for (int i = 0; i < b.length; i++)
    {
      s = b[i];
    }

    System.out.print("\nddd" + s);
    Constant.AUTO_CALL_DATE_MAP.put("date_time_8", "22-23");
    Constant.AUTO_CALL_DATE_MAP.put("date_time_9", "23-24");

    Iterator it = Constant.AUTO_CALL_DATE_MAP.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry)it.next();
      System.out.print("\n" + entry.getKey() + "==" + entry.getValue());
    }
    String phone = "01062983783";
    phone = phone.replaceAll("-", "");
    String regEx = "((^\\d{3,4}\\d{3,8})|(^\\d{3,8}))$";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(phone);
    System.out.print("\n mactch==" + m.find());

    System.out.print("\n string to integer=" + Integer.parseInt(""));
  }
}