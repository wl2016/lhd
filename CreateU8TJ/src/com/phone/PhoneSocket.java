package com.phone;

import com.xml.Constant;
import com.xml.jdbc.Execute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneSocket extends Thread
{
  protected static Socket socket;
  protected static BufferedReader bufReader;
  protected static PrintWriter out;
  protected static Map readString = new HashMap();
  public static Map autoDial = new HashMap();
  protected static Map phone = new HashMap();
  protected static Map save = new HashMap();

  public void run() {
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
      new AutoCall().start();
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

      socket.setTcpNoDelay(true);
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
    for (int i = 0; i < 2; i++)
    {
      b[i] = (byte)(num >>> 8 - i * 8);
    }
    return b;
  }

  public static void writeData(int strLen, int cmd, int channelNo, String msg)
  {
    byte[] resultByte = new byte[strLen + 4];
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

      resultByte[0] = -1;
      resultByte[1] = -1;

      byte[] b = int2bytes(strLen);
      for (int i = 0; i < b.length; i++)
      {
        resultByte[(2 + i)] = b[i];
      }

      b = int2bytes(cmd);
      for (int i = 0; i < b.length; i++)
      {
        resultByte[(4 + i)] = b[i];
      }

      resultByte[6] = (byte)channelNo;

      b = msg.getBytes();
      for (int i = 0; i < b.length; i++)
      {
        resultByte[(7 + i)] = b[i];
      }
      resultByte[(strLen + 3)] = 0;
      System.out.print("\n 转换后数据 ");
      for (int i = 0; i < resultByte.length; i++)
      {
        System.out.print(resultByte[i] + " ");
      }
      ops.write(resultByte);

      ops.flush();
    }
    catch (IOException ie)
    {
      ie.printStackTrace();
    }
  }

  public static void readData()
  {
    Map voiceId = new HashMap();
    Map voiceName = new HashMap();
    Map DTMF = new HashMap();
    Map contentString = new HashMap();
    String sql = "";
    String data = "";

    int ii = 0;
    while (true)
    {
      try
      {
        Thread.sleep(100L);
        System.out.print("\n运行次数===" + ii++);
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

        data = "";
        for (int j = 0; j < len; j++)
        {
          data = data + (char)buf[(j + 7)];
        }

        contentString.put("contentString" + channelNo, data);
        int flg = (buf[4] << 8) + buf[5];

        if (flg == 0)
        {
          System.out.print("\n 外部电话呼入" + data);
          if (!data.equals(""))
            data = data.substring(1, data.length() - 1);
          System.out.print("\n来电号码===" + data);
          phone.put("phone" + channelNo, data);

          sql = "select i.cus_phone from installation i  where i.cus_phone='" + phone.get(new StringBuilder("phone").append(channelNo).toString()) + "'";
          if (new Query().query(sql))
          {
            System.out.print("\n 电话存在，执行下一步提示操作");
            voiceId.put("voiceId" + channelNo, Constant.properties.get("selectproce").toString() + ",1");
            voiceName.put("voiceName" + channelNo, "selectproce");
          }
          else
          {
            System.out.print("\n 该电话未注册，提示用户是否输入输入注册电话号码");
            voiceId.put("voiceId" + channelNo, Constant.properties.get("callinwelcome").toString() + ",1");
            voiceName.put("voiceName" + channelNo, "callinwelcome");
          }
          int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
          writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
        }

        if (flg == 1)
        {
          System.out.print("\n 用户按键信息==" + data);

          if (voiceName.get("voiceName" + channelNo).toString().equals("callinwelcome"))
          {
            if (contentString.get("contentString" + channelNo).toString().equals("0"))
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("handing").toString());
              voiceName.put("voiceName" + channelNo, "handing");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32769, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }
            else if (contentString.get("contentString" + channelNo).toString().equals("1"))
            {
              System.out.print("\n 该电话未注册，提示用户是否输入输入注册电话号码");
              voiceId.put("voiceId" + channelNo, Constant.properties.get("confirmno").toString() + ",1");
              voiceName.put("voiceName" + channelNo, "confirmno");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }
            else
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("recallinwelcome").toString() + ",1");
              voiceName.put("voiceName" + channelNo, "callinwelcome");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }

          }
          else if (voiceName.get("voiceName" + channelNo).toString().equals("confirmno"))
          {
            if (!contentString.get("contentString" + channelNo).toString().equals("#"))
            {
              if (DTMF.get("DTMF" + channelNo) == null)
              {
                DTMF.put("DTMF" + channelNo, "");
              }

              DTMF.put("DTMF" + channelNo, DTMF.get(new StringBuilder("DTMF").append(channelNo).toString()).toString() + contentString.get(new StringBuilder("contentString").append(channelNo).toString()).toString());
            }
            else
            {
              System.out.print("\n 认证电话号码" + DTMF.get(new StringBuilder("DTMF").append(channelNo).toString()).toString());
              sql = "select i.cus_phone from installation i  where i.cus_phone='" + DTMF.get(new StringBuilder("DTMF").append(channelNo).toString()).toString() + "'";
              if (new Query().query(sql))
              {
                phone.put("phone" + channelNo, DTMF.get("DTMF" + channelNo).toString());
                voiceId.put("voiceId" + channelNo, Constant.properties.get("selectproce") + ",1");
                voiceName.put("voiceName" + channelNo, "selectproce");
              }
              else
              {
                voiceId.put("voiceId" + channelNo, Constant.properties.get("nothisnoerror") + ",1");
                voiceName.put("voiceName" + channelNo, "confirmno");
              }
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              DTMF.put("DTMF" + channelNo, "");
            }

          }
          else if (voiceName.get("voiceName" + channelNo).toString().equals("selectproce"))
          {
            System.out.print("\n selectproce 操作");

            if (contentString.get("contentString" + channelNo).toString().equals("1"))
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("advice").toString() + ",1");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              voiceName.put("voiceName" + channelNo, "advice");
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }
            else if ((contentString.get("contentString" + channelNo).toString().equals("2")) || 
              (contentString.get("contentString" + channelNo).toString().equals("0")))
            {
              readString.put("readString" + channelNo, "q" + channelNo + phone.get(new StringBuilder("phone").append(channelNo).toString()));
              voiceId.put("voiceId" + channelNo, Constant.properties.get("handing").toString());

              voiceName.put("voiceName" + channelNo, "handing");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32769, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }
            else
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("reselectproce") + ",1");
              voiceName.put("voiceName" + channelNo, "selectproce");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }

          }
          else if (voiceName.get("voiceName" + channelNo).toString().equals("calloutwelcome"))
          {
            save.put("save" + channelNo, "0");

            if (contentString.get("contentString" + channelNo).toString().equals(Constant.properties.get("satisfaction").toString()))
            {
              update(channelNo, "1");
              voiceId.put("voiceId" + channelNo, Constant.properties.get("welcomeback") + ",0");

              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;

              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else if (contentString.get("contentString" + channelNo).toString().equals(Constant.properties.get("dissatisfaction").toString()))
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("welcomeback") + ",0");

              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("inputerror") + ",1");

              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              voiceName.put("voiceName" + channelNo, "calloutwelcome");
            }
          }
          else if (voiceName.get("voiceName" + channelNo).toString().equals("advice"))
          {
            save.put("save" + channelNo, "0");

            DTMF.put("DTMF" + channelNo, data);
            System.out.print("\n 回访按键内容" + DTMF.get(new StringBuilder("DTMF").append(channelNo).toString()));
            if (DTMF.get("DTMF" + channelNo).toString().equals(Constant.properties.get("satisfaction").toString()))
            {
              System.out.print("\n 回访结果满意");
              update(channelNo, "1");

              voiceId.put("voiceId" + channelNo, Constant.properties.get("goodble").toString() + ",0");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else if (DTMF.get("DTMF" + channelNo).toString().equals(Constant.properties.get("dissatisfaction").toString()))
            {
              System.out.print("\n 回访结果不满意");
              update(channelNo, "0");
              voiceId.put("voiceId" + channelNo, Constant.properties.get("goodble").toString() + ",0");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else if (DTMF.get("DTMF" + channelNo).toString().equals(Constant.properties.get("coplainsuguest").toString()))
            {
              readString.put("readString" + channelNo, "q" + channelNo + phone.get(new StringBuilder("phone").append(channelNo).toString()));
              voiceId.put("voiceId" + channelNo, Constant.properties.get("handing").toString());
              voiceId.put("voiceId" + channelNo, Constant.properties.get("handing").toString());
              voiceName.put("voiceName" + channelNo, "handing");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32769, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }
            else
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("readvice").toString() + ",1");
              voiceName.put("voiceName" + channelNo, "advice");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;

              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }

          }
          else if (voiceName.get("voiceName" + channelNo).toString().equals("calloutprocess"))
          {
            DTMF.put("DTMF" + channelNo, data);
            System.out.print("\n 回访按键内容" + DTMF.get(new StringBuilder("DTMF").append(channelNo).toString()));
            save.put("save" + channelNo, "0");
            if (DTMF.get("DTMF" + channelNo).toString().equals(Constant.properties.get("satisfaction").toString()))
            {
              System.out.print("\n 回访结果满意");
              update(channelNo, "1");

              voiceId.put("voiceId" + channelNo, Constant.properties.get("goodble").toString() + ",0");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else if (DTMF.get("DTMF" + channelNo).toString().equals(Constant.properties.get("dissatisfaction").toString()))
            {
              System.out.print("\n 回访结果不满意");
              update(channelNo, "0");

              voiceId.put("voiceId" + channelNo, Constant.properties.get("goodble").toString() + ",0");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "0");
            }
            else
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("inputerror").toString() + ",1");
              voiceName.put("voiceName" + channelNo, "calloutprocess");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;

              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }

          }
          else if (voiceName.get("voiceName" + channelNo).toString().equals("exameresult"))
          {
            save.put("save" + channelNo, "0");

            if (contentString.get("contentString" + channelNo).toString().equals("1"))
            {
              System.out.print("\n 满意");
              update(channelNo, "1");
              voiceId.put("voiceId" + channelNo, Constant.properties.get("goodble").toString() + ",0");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else if (contentString.get("contentString" + channelNo).toString().equals("0"))
            {
              System.out.print("\n 不满意");
              update(channelNo, "0");
              voiceId.put("voiceId" + channelNo, Constant.properties.get("goodble").toString() + ",0");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
              save.put("save" + channelNo, "1");
            }
            else
            {
              voiceId.put("voiceId" + channelNo, Constant.properties.get("inputerror").toString() + ",1");
              voiceName.put("voiceName" + channelNo, "exameresult");
              int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
              writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
            }

            voiceName.put("voiceName" + channelNo, "exameresult");
          }
        }
        if (flg == 2)
        {
          String[] st = { "01", "02", "03", "04", "05", "06", "07", "10", "99" };
          System.out.print("\n呼叫中心返回值==" + contentString.get(new StringBuilder("contentString").append(channelNo).toString()).toString());
          if (contentString.get("contentString" + channelNo).toString().equals("04"))
          {
            readString.put("readString" + channelNo, "e" + channelNo + "拨打电话无法接通");
            System.out.print("\n 拨打电话无法接通");
          }

          if ((contentString.get("contentString" + channelNo).toString().equals("00")) || 
            (contentString.get("contentString" + channelNo).toString().equals("02")))
          {
            if ((save.get("save" + channelNo) != null) && (save.get("save" + channelNo).toString().equals("0")))
            {
              update(channelNo, "1");
            }
          }

          autoDial.put("autoDial" + channelNo, "0");
          phone.put("phone" + channelNo, "");
          voiceId.put("voiceId" + channelNo, "");
          voiceName.put("voiceName" + channelNo, "");
          DTMF.put("DTMF" + channelNo, "");
          contentString.put("contentString" + channelNo, "");
          save.put("save" + channelNo, "");

          System.out.print("\n呼叫结束，清除数据");

          if (channelNo >= 100)
          {
            System.out.print("\n AutoCall.status[channelNo-100]=" + channelNo);
            AutoCall.status[(channelNo - 100)] = "0";
          }
          else if (channelNo < 0)
          {
            for (int i = 0; i < AutoCall.status.length; i++)
            {
              AutoCall.status[i] = "0";
            }
          }
        }

        if (flg != 3)
          continue;
        System.out.print("\n 电话状态码" + contentString.get(new StringBuilder("contentString").append(channelNo).toString()).toString());
        if (contentString.get("contentString" + channelNo).toString().equals("20"))
        {
          System.out.print("\n 向呼叫中心发送命令");

          if (autoDial.get("autoDial" + channelNo).toString().equals("1"))
          {
            System.out.print("\n 自动拨打");
            voiceId.put("voiceId" + channelNo, Constant.properties.get("calloutwelcome") + ",1");
            voiceName.put("voiceName" + channelNo, "calloutwelcome");
            int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
            writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString()); continue;
          }

          System.out.print("\n 不是自动拨打");
          voiceName.put("voiceName" + channelNo, "calloutwelcome"); continue;
        }

        if (!contentString.get("contentString" + channelNo).toString().equals("10"))
          continue;
        voiceId.put("voiceId" + channelNo, Constant.properties.get("exameresult") + ",0");
        voiceName.put("voiceName" + channelNo, "exameresult");
        int l = voiceId.get("voiceId" + channelNo).toString().length() + 4;
        writeData(l, 32768, channelNo, voiceId.get("voiceId" + channelNo).toString());
      }
      catch (IOException ie)
      {
        ie.printStackTrace();
      }
    }
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

  public static String getReadString(String channelNo)
  {
    String msg = "";
    if (channelNo == null)
      msg = "e坐席号不正确或登录超时";
    else {
      msg = readString.get("readString" + channelNo).toString();
    }
    readString.put("readString" + channelNo, "");
    return msg;
  }

  public static void setReadString(String value, String channelNo)
  {
    readString.put("readString" + channelNo, value);
  }

  private static void update(int channelNo, String status)
  {
    Execute exe = new Execute();
    String sql = null;

    sql = "select bill_no from installation where cus_phone='" + phone.get(new StringBuilder("phone").append(channelNo).toString()) + "'";
    List lst = exe.queryUpdate(sql);
    int lstSize = lst.size();
    if (status.equals("1"))
    {
      status = "80";
    }
    else
      status = "40";
    for (int i = 0; i < lstSize; i++)
    {
      Map map = (Map)lst.get(i);
      sql = "insert into ins_service_detail(bill_no,question_no,question_answer,question_other)  values('" + 
        map.get("bill_no") + "'" + 
        ",'HF000000'" + 
        ",'" + status + "'" + 
        ",'')";
      exe.execute(sql);
    }
    sql = "update installation i set i.review=1 where i.cus_phone ='" + phone.get(new StringBuilder("phone").append(channelNo).toString()) + "'";
    exe.execute(sql);
  }
}