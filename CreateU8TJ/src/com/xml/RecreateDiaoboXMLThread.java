package com.xml;

import java.io.PrintStream;

public class RecreateDiaoboXMLThread extends Thread
{
  public void run()
  {
    System.out.print("\n 开启重新调拨线程监听程序 ");
    creatEnventListener();
  }

  private void creatEnventListener()
  {
    RecreateDiaoboXML.setFinishFlag(true);
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (!RecreateDiaoboXML.getFinishFlag())
          continue;
        RecreateDiaoboXML.outputMerchandise2XML();
      }

    }
    catch (InterruptedException ie)
    {
      ie.printStackTrace();
    }
  }
}