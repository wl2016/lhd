package com.xml;

public class RecreateInputXmlThread extends Thread
{
  RecreateInputXML recreate = new RecreateInputXML();

  public void run() {
    recreateInputXml();
  }

  private void recreateInputXml() {
    this.recreate.setFinishFlag(true);
    try
    {
      while (true)
      {
        Thread.sleep(Constant.TREAD_TIME_OUT);
        if (this.recreate == null)
        {
          this.recreate = new RecreateInputXML();
          this.recreate.setFinishFlag(true);
        }

        if (!this.recreate.getFinishFlag())
          continue;
        this.recreate.inputMerchandise2XML();
      }

    }
    catch (InterruptedException ie)
    {
      ie.printStackTrace();
    }
  }
}