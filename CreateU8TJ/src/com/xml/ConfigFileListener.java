package com.xml;

import java.io.File;
import java.io.PrintStream;

public class ConfigFileListener extends Thread
{
  public void run()
  {
    fileListener();
  }

  public void fileListener()
  {
    try
    {
      while (true)
      {
        Thread.sleep(1500L);
        String path = Constant.SERVLET_PATH + "config/config.properties";
        path = path.replaceAll("\\\\", "/");
        path = path.replaceAll("//", "/");
        File file = new File(path);
        long currentModified = file.lastModified();
        if (currentModified <= Constant.CONFIG_FILE_DATE)
          continue;
        System.out.print("\n 配置文件更新，重新加载配置文件");
        Constant.initConfig();
      }
    }
    catch (InterruptedException localInterruptedException)
    {
    }
  }
}