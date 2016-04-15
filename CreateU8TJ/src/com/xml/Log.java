package com.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Log
{
  public static void log(String fname, String msg, boolean append)
  {
    try
    {
      PrintWriter out = new PrintWriter(new FileWriter(fname, append), true);
      out.print(msg);      
    }
    catch (IOException e)
    {
      System.err.println("无法打开日志文件: " + fname);
    }
  }
}