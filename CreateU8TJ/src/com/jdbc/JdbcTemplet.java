package com.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class JdbcTemplet
{
  private Statement stmt;
  private Connection cnn;
  private ResultSet rs;

  public int getRecordCount(String sql)
  {
    int totalRecord = 0;
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    try
    {
      this.stmt = this.cnn.createStatement();
      this.rs = this.stmt.executeQuery(sql);
      if (this.rs.next())
      {
        totalRecord = this.rs.getInt(1);
      }
      this.rs.close();
      this.stmt.close();
    }
    catch (SQLException localSQLException)
    {
    }

    return totalRecord;
  }

  public void execute(String sql)
  {
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    try
    {
      this.stmt = this.cnn.createStatement();
      this.stmt.execute(sql);
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
  }

  public boolean executeUpdate(String sql) {
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    try
    {
      this.stmt = this.cnn.createStatement();
      this.stmt.execute(sql);
      this.stmt.close();
      return true;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return false;
  }

  public boolean checkExists(String sql)
  {
    boolean flg = false;
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    try
    {
      this.stmt = this.cnn.createStatement();
      this.rs = this.stmt.executeQuery(sql);
      if (this.rs.next())
        flg = true;
      this.rs.close();
      this.stmt.close();
    }
    catch (SQLException localSQLException)
    {
    }

    return flg;
  }

  public Connection getConnection()
  {
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    return this.cnn;
  }

  public void close()
  {
    try
    {
      if (this.rs != null)
        this.rs.close();
      if (this.stmt != null)
        this.stmt.close();
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public List query(String sql)
  {
    List lst = new Vector();
    JdbcUtils JU = new JdbcUtilsImpl();
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    try
    {
      this.stmt = this.cnn.createStatement();
      this.rs = this.stmt.executeQuery(sql);
      while (this.rs.next())
      {
        lst.add(JU.getRowValue(this.rs));
      }
      this.rs.close();
      this.stmt.close();
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return lst;
  }
}