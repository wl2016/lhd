package com.xml;

import java.io.PrintStream;

public class SellputXmlThread extends Thread
{
  public void run()
  {
    System.out.print("\n -----开启出库监听程序----- ");
    creatEnventListener();
  }

  private void creatEnventListener()
  {
    CreatSellputXML output = new CreatSellputXML();
    output.setOutputFinishFlag(true);
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (output == null)
        {
          output = new CreatSellputXML();
          output.setOutputFinishFlag(true);
        }
        if (!output.getOutputFinishFlag())
          continue;
        output.outputMerchandise2XML();
      }

    }
    catch (InterruptedException ie)
    {
      ie.printStackTrace();
    }
  }
}