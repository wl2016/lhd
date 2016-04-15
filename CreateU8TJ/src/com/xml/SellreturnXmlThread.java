package com.xml;

import java.io.PrintStream;

public class SellreturnXmlThread extends Thread
{
  public void run()
  {
    System.out.print("\n -----开启销售退货监听程序----- ");
    creatEnventListener();
  }

  private void creatEnventListener()
  {
    CreatSellreturnXML output = new CreatSellreturnXML();
    output.setOutputFinishFlag(true);
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (output == null)
        {
          output = new CreatSellreturnXML();
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