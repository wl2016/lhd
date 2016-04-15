package com.xml.jdbc;

import com.xml.Constant;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DataBase
{
  static Connection con = null;

  public static Connection getConnnection()
  {
    if ((Constant.JDBC_DRIVER == null) || (Constant.JDBC_URL == null) || (Constant.JDBC_DRIVER.equals("")) || (Constant.JDBC_URL.equals("")))
    {
      initDataConfig();
    }
    try
    {
      if ((con != null) && (!con.isClosed()))
      {
        return con;
      }
      Class.forName(Constant.JDBC_DRIVER);
      con = DriverManager.getConnection(Constant.JDBC_URL, Constant.JDBC_USER, Constant.JDBC_PASSWD);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return con;
  }

  public static void initDataConfig()
  {
	  System.out.println(" Constant.DATA_PATH : "+Constant.DATA_PATH);
    ResourceBundle resource = ResourceBundle.getBundle(Constant.DATA_PATH);
    Constant.JDBC_DRIVER = resource.getString("jdbc_drivers");
    Constant.JDBC_URL = resource.getString("jdbc_url");
    Constant.JDBC_USER = resource.getString("jdbc_user");
    Constant.JDBC_PASSWD = resource.getString("jdbc_password");
    if ((Constant.JDBC_DRIVER == null) || (Constant.JDBC_URL == null) || (Constant.JDBC_DRIVER.equals("")) || (Constant.JDBC_URL.equals("")))
    {
      throw new RuntimeException("数据库未配置，请先配置数据库");
    }
  }

  public void execute(String sql)
  {
    try {
      getConnnection().createStatement().execute(sql);
    }
    catch (SQLException localSQLException)
    {
    }
  }
}