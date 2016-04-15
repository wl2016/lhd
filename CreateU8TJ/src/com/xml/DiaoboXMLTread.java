package com.xml;

import java.io.PrintStream;

public class DiaoboXMLTread extends Thread
{
  public void run()
  {
    System.out.print("\n 开启调拨线程监听程序 ");
    creatEnventListener();
  }

  private void creatEnventListener()
  {
    DiaoboXML.setFinishFlag(true);
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (!DiaoboXML.getFinishFlag())
          continue;
        DiaoboXML.outputMerchandise2XML();
      }

    }
    catch (InterruptedException ie)
    {
      ie.printStackTrace();
    }
  }
}