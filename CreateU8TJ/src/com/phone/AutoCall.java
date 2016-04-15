package com.phone;

import com.xml.Constant;
import com.xml.jdbc.Execute;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCall extends Thread
{
  public static String[] status;

  public void run()
  {
    aotuCall();
  }

  private static void aotuCall()
  {
    int arrayNum = 16;

    int[] channelNo = new int[arrayNum];
    int useFlag = 0;
    String phone = "";
    status = new String[arrayNum];

    String sql = "select distinct i.cus_phone,i.bill_no from installation i where i.review=0";
    List lst = null;
    boolean flg = false;
    for (int i = 0; i < channelNo.length; i++)
    {
      channelNo[i] = (100 + i);
      status[i] = "0";
    }
    int index = status.length;
    try
    {
      while (true)
      {
        Thread.sleep(Constant.AUTO_CALL_FREQUENCY);
        if (!checkDateTimeAvailable())
          continue;
        flg = false;

        if ((lst == null) || (lst.size() == 0))
          lst = new Execute().queryUpdate(sql);
        for (int i = 0; i < index; i++)
        {
          System.out.print("\n status " + i + " ==  " + status[i]);
          if (!status[i].equals("0"))
            continue;
          useFlag = i;
          flg = true;
          break;
        }

        if (!flg)
          continue;
        for (int i = 0; i < lst.size(); i++)
        {
          Map phoneMap = (Map)lst.get(i);
          if (phoneMap.get("cus_phone") == null)
          {
            update(phoneMap.get("bill_no").toString());
          }
          else {
            phone = phoneMap.get("cus_phone").toString();
            checkPhone(phone);
            System.out.print("\n 拨打电话号码-->" + phone);

            int strLen = phone.length() + 4;
            PhoneSocket.autoDial.put("autoDial" + channelNo[useFlag], "1");
            PhoneSocket.phone.put("phone" + channelNo[useFlag], phone);
            PhoneSocket.writeData(strLen, 32770, channelNo[useFlag], phone);
            status[useFlag] = "1";
            System.out.print("\n 拨打电话号码-->" + phone);
            lst.remove(i);
            break;
          }
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static void update(String phone)
  {
    String sql = "update installation i set i.review=1 where i.cus_phone ='" + phone + "' or i.bill_no='" + phone + "'";
    new Execute().execute(sql);
  }

  private static boolean checkPhone(String phone)
  {
    System.out.print("\n Constant.PHONE_REGEX==" + Constant.PHONE_REGEX);
    String regEx = Constant.PHONE_REGEX;
    phone = phone.replaceAll("-", "");
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(phone);
    System.out.print(m.find());
    return m.find();
  }

  private static int getDate()
  {
    Date date = new Date();
    return date.getDay();
  }

  private static int getHourse()
  {
    Date date = new Date();
    return date.getHours();
  }

  private static boolean checkDateTimeAvailable()
  {
    int hourse = getHourse();
    Iterator it = Constant.AUTO_CALL_DATE_MAP.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry)it.next();
      String key = entry.getKey().toString();
      String value = entry.getValue().toString();
      String[] valueSplit = value.split("-");
      if (Integer.parseInt(key.substring(key.length() - 1)) != getDate()) {
        continue;
      }
      if ((hourse >= Integer.parseInt(valueSplit[0])) && (hourse <= Integer.parseInt(valueSplit[1]))) {
        return true;
      }
    }
    return false;
  }
}