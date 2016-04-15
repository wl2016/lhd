package com.xml;

public class RecreateInputreturnXmlThread extends Thread
{
  RecreateInputreturnXML recreate = new RecreateInputreturnXML();

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
          this.recreate = new RecreateInputreturnXML();
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