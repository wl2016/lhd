package com.phone;

import com.xml.Constant;
import com.xml.jdbc.DataBase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class PhoneSocketBack extends Thread
{
  protected static Socket socket;
  protected static BufferedReader bufReader;
  protected static PrintWriter out;
  protected static String readString = "";
  protected static String DTMF = "";
  public static boolean autoDial = false;
  protected static String phone;

  public void run()
  {
    System.out.print("\n开启接收数据监听");
    connect();

    readData();
  }

  private static void reconnect()
  {
    try
    {
      System.out.print("\n网络已断开, 2秒钟后重试连接...");
      Thread.sleep(2000L);
      connect();
    }
    catch (InterruptedException localInterruptedException)
    {
    }

    readData();
  }

  private static void connect()
  {
    if (Constant.REMOTE_IP == null)
      Constant.initConfig();
    try
    {
      socket = new Socket(Constant.REMOTE_IP, Constant.CONNECT_PORT);

      System.out.print("\n建立连接成功");
    }
    catch (UnknownHostException uhe)
    {
      System.out.print("无法连接远程服务");
      uhe.printStackTrace();
    }
    catch (IOException ie)
    {
      ie.printStackTrace();
    }
  }

  static byte[] int2bytes(int num)
  {
    byte[] b = new byte[2];
    int mask = 255;
    for (int i = 0; i < 2; i++)
    {
      b[i] = (byte)(num >>> 8 - i * 8);
    }
    return b;
  }

  public static void writeData(int strLen, int cmd, int channelNo, String msg)
  {
    try
    {
      socket.sendUrgentData(255);
    }
    catch (Exception ex)
    {
      reconnect();
    }
    System.out.print("\n 提交数据==" + msg);
    try
    {
      OutputStream ops = socket.getOutputStream();
      ops.write(int2bytes(65535));
      ops.write(int2bytes(strLen));
      ops.write(int2bytes(cmd));
      ops.write((byte)channelNo);

      byte[] b = msg.getBytes();
      ops.write(b);
      ops.write(0);
      ops.flush();
    }
    catch (IOException ie)
    {
      ie.printStackTrace();
    }
  }

  public static void readData()
  {
    String voiceId = "";
    String voiceName = "";
    String DTMF = "";
    String contentString = "";
    String sql = "";
    while (!socket.isClosed())
    {
      try
      {
        Thread.sleep(Constant.PHONE_FREQUENCY_TIME);
        try
        {
          socket.sendUrgentData(255);
        }
        catch (Exception ex)
        {
          reconnect();
        }

      }
      catch (InterruptedException localInterruptedException)
      {
      }

      try
      {
        InputStream ist = socket.getInputStream();
        byte[] buf = new byte[256];
        ist.read(buf);

        int channelNo = buf[6];
        int len = (buf[2] << 8) + buf[3];
        len -= 4;
        System.out.print("\n获取数据内容长度len===" + len);
        System.out.print("\n获取通道号===" + channelNo);

        contentString = "";
        readString = "";
        for (int j = 0; j < len; j++)
        {
          contentString = contentString + (char)buf[(j + 7)];
        }

        int flg = (buf[4] << 8) + buf[5];

        if (flg == 0)
        {
          System.out.print("\n 外部电话呼入");
          phone = contentString;
          voiceId = Constant.properties.get("voice00").toString() + ",1";
          voiceName = "voice00";

          int l = voiceId.length() + 4;
          writeData(l, 32768, channelNo, voiceId);
        }

        if (flg == 1)
        {
          System.out.print("\n 用户按键信息==" + contentString);

          if (voiceName.startsWith("voice00"))
          {
            if (contentString.equals("1"))
            {
              sql = "select i.cus_phone from installation i  where i.cus_phone='" + phone + "'";
              if (new Query().query(sql))
              {
                voiceId = Constant.properties.get("voice11") + ",1";
                voiceName = "voice11";
              }
              else
              {
                voiceId = Constant.properties.get("confirmno") + ",1";
                voiceName = "confirmno";
              }
              int l = voiceId.length() + 4;
              writeData(l, 32768, channelNo, voiceId);
            }
            else if (contentString.equals("2"))
            {
              voiceId = Constant.properties.get("voice21").toString();
              voiceName = "voice21";
              int l = voiceId.length() + 4;
              writeData(l, 32769, channelNo, voiceId);
            }
            else
            {
              voiceId = Constant.properties.get("errorvoice").toString() + ",1";
              voiceName = "voice00";
              int l = voiceId.length() + 4;

              writeData(l, 32769, channelNo, voiceId);
            }

          }
          else if (voiceName.equals("confirmno"))
          {
            if (!contentString.equals("#"))
            {
              DTMF = DTMF + contentString;
            }
            else
            {
              sql = "select i.cus_phone from installation i  where i.cus_phone='" + DTMF + "'";
              if (new Query().query(sql))
              {
                voiceId = Constant.properties.get("voice11") + ",1";
                voiceName = "voice11";
              }

              int l = voiceId.length() + 4;
              writeData(l, 32768, channelNo, voiceId);
              DTMF = "";
            }
          }
          else if (voiceName.equals("voice31"))
          {
            if (contentString.equals("1"))
            {
              voiceId = Constant.properties.get("voice11") + ",1";
              voiceName = "voice11";
              int l = voiceId.length() + 4;

              writeData(l, 32768, channelNo, voiceId);
            }
          }
          else if (voiceName.startsWith("voice11"))
          {
            DTMF = contentString;
            System.out.print("\n 回访按键内容" + DTMF);
            if (DTMF.equals(Constant.properties.get("satisfaction").toString()))
            {
              System.out.print("\n 回访结果满意");
              String bill_no = "";
              sql = "insert into ins_service_detail(bill_no,question_no,question_answer,question_other)  values('" + 
                bill_no + "'" + 
                ",'HF000000'" + 
                ",'80'" + 
                ",'')";
              new DataBase().execute(sql);
              sql = "update installation set review=1 where bill_no='" + bill_no + "'";
              new DataBase().execute(sql);
            }
            else if (DTMF.equals(Constant.properties.get("dissatisfaction").toString()))
            {
              System.out.print("\n 回访结果不满意");
            }
            else
            {
              voiceId = Constant.properties.get("voice12").toString();
              voiceName = "voice11";
              int l = voiceId.length() + 4;

              writeData(l, 32769, channelNo, voiceId);
            }

          }
          else if (voiceName.startsWith("voice21"))
          {
            voiceId = (String) Constant.properties.get("voice22");
            int l = voiceId.length() + 4;
            System.out.print("\n 用户回访按键" + contentString);

            readString = "q" + channelNo + contentString;
            writeData(l, 32768, channelNo, voiceId);
          }

        }

        if (flg == 2)
        {
          System.out.print("\n呼叫中心返回值==" + contentString);
          if (contentString.equals("04"))
          {
            readString = "e" + channelNo + "拨打电话无法接通";
            System.out.print("\n 拨打电话无法接通");
          }
        }

        if (flg == 3)
        {
          if (contentString.equals("20"))
          {
            if (autoDial)
            {
              System.out.print("\n 自动拨打");
              voiceId = Constant.properties.get("voice31") + ",1";
              voiceName = "voice31";
              int l = voiceId.length() + 4;
              writeData(l, 32768, channelNo, voiceId);
            }
            else
            {
              System.out.print("\n 不是自动拨打");
              voiceId = Constant.properties.get("voice31") + ",1";
              voiceName = "voice31";
              int l = voiceId.length() + 4;
              writeData(l, 32768, channelNo, voiceId);
            }

          }

        }

        System.out.print("\n数据内容" + contentString);
      }
      catch (IOException ie)
      {
        ie.printStackTrace();
      }
    }
  }

  public static String string2Hex(String msg)
  {
    String msg2hex = "";
    byte[] b = msg.getBytes();
    for (int i = 0; i < b.length; i++)
    {
      msg2hex = msg2hex + Integer.toHexString(msg.charAt(i));
    }
    return msg2hex;
  }

  public static void close()
  {
    try
    {
      socket.close();
    }
    catch (IOException localIOException)
    {
    }
  }

  public static String getReadString()
  {
    return readString;
  }

  public static void setReadString(String str) {
    readString = str;
  }
}