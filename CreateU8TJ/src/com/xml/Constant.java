package com.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constant
{
  public static int TREAD_TIME_OUT;
  public static String SERVLET_PATH;
  public static String DATA_PATH;
  public static String JDBC_DRIVER;
  public static String JDBC_URL;
  public static String JDBC_USER;
  public static String JDBC_PASSWD;
  public static String STORE_IN_OUT_XML;
  public static String ISAUTO_CREATE_XML;
  public static List BASE_MESSAGE_MAPPING;
  public static String NEW_XML_NAME;
  public static String REMOTE_IP;
  public static int CONNECT_PORT;
  public static Map properties;
  public static int PHONE_FREQUENCY_TIME;
  public static int AUTO_CALL_FREQUENCY;
  public static Map AUTO_CALL_DATE_MAP = new HashMap();
  public static String PHONE_REGEX;
  public static long CONFIG_FILE_DATE;
  public static float tax_rate = 0.0F;

  public static void initConfig()
  {
    properties = new HashMap();
    String path = SERVLET_PATH + "config/config.properties";
    path = path.replaceAll("\\\\", "/");
    path = path.replaceAll("//", "/");

    File file = new File(path);
    CONFIG_FILE_DATE = file.lastModified();
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null)
      {
        line = line.trim();
        if (line.startsWith("#"))
          continue;
        int index = line.indexOf("=");
        if ((index == -1) || (index == line.length() - 1))
          continue;
        if (line.substring(0, index).toLowerCase().equals("ip"))
        {
          REMOTE_IP = line.substring(index + 1);
        }

        if (line.substring(0, index).toLowerCase().equals("tax_rate"))
        {
          String r = line.substring(index + 1);
          try
          {
            tax_rate = Float.parseFloat(r);
          }
          catch (NumberFormatException nfe)
          {
            tax_rate = 0.0F;
          }
          if (tax_rate >= 1.0F)
            tax_rate = 0.0F;
        }
        else if (line.substring(0, index).toLowerCase().equals("port"))
        {
          int port = Integer.parseInt(line.substring(index + 1));
          CONNECT_PORT = port;
        }
        else if (line.substring(0, index).toLowerCase().equals("phone_frequency_time"))
        {
          PHONE_FREQUENCY_TIME = Integer.parseInt(line.substring(index + 1));
        }
        else if (line.substring(0, index).toLowerCase().equals("auto_call_frequency"))
        {
          AUTO_CALL_FREQUENCY = Integer.parseInt(line.substring(index + 1));
        }
        else if (line.substring(0, index).toLowerCase().startsWith("data_time"))
        {
          AUTO_CALL_DATE_MAP.put(line.substring(0, index).toLowerCase(), line.substring(index + 1));
        }
        else if (line.substring(0, index).toLowerCase().equals("phone_regex"))
        {
          PHONE_REGEX = line.substring(index + 1);
        }
        else {
          properties.put(line.substring(0, index).toLowerCase(), line.substring(index + 1));
        }
      }

    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
}