package com.xml;

import java.io.PrintStream;

public class XmlreturnThread extends Thread
{
  CreatreturnXML createInput = new CreatreturnXML();

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
          this.createInput = new CreatreturnXML();
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