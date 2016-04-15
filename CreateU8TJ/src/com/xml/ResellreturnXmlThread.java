package com.xml;

import java.io.PrintStream;

public class ResellreturnXmlThread extends Thread
{
  RecreatSellreturnXML create = new RecreatSellreturnXML();

  public void run() {
    reoutputMerchandise2XMLListener();
  }

  private void reoutputMerchandise2XMLListener()
  {
    this.create.setOutputFinishFlag(true);
    System.out.print("\n -----ssss监听需要重新生成xml操作----- ");
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (this.create == null)
          this.create = new RecreatSellreturnXML();
        if (!this.create.getOutputFinishFlag())
          continue;
        this.create.reoutputMerchandise2XML();
      }

    }
    catch (InterruptedException ie)
    {
      ie.printStackTrace();
    }
  }
}