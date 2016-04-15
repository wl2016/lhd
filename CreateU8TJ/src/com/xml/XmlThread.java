package com.xml;

import java.io.PrintStream;

public class XmlThread extends Thread
{
  CreatXML createInput = new CreatXML();

  public void run() {
    System.out.print("\n 开启入库线程监听程序 ");
    creatEnventListener();
  }

  private void creatEnventListener() {
    this.createInput.setFinishFlag(true);
    int i = 0;
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (this.createInput == null)
        {
          this.createInput = new CreatXML();
          this.createInput.setFinishFlag(true);
        }

        if (!this.createInput.getFinishFlag())
          continue;
        this.createInput.inputMerchandise2XML();
      }
    }
    catch (InterruptedException localInterruptedException)
    {
    }
  }
}