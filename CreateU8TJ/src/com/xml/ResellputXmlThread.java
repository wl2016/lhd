package com.xml;

import java.io.PrintStream;

public class ResellputXmlThread extends Thread
{
  RecreatSellputXML create = new RecreatSellputXML();

  public void run() {
    reoutputMerchandise2XMLListener();
  }

  private void reoutputMerchandise2XMLListener()
  {
    this.create.setOutputFinishFlag(true);
    System.out.print("\n -----监听需要重新生成xml操作----- ");
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (this.create == null)
          this.create = new RecreatSellputXML();
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