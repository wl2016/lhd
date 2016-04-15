package com.xml.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Execute
{
  private Statement stmt;
  private Connection cnn;
  private ResultSet rs;

  public ResultSet query(String sql)
  {
    try
    {
      if ((this.cnn == null) || (this.cnn.isClosed()))
        this.cnn = DataBase.getConnnection();
      this.stmt = this.cnn.createStatement();
      this.rs = this.stmt.executeQuery(sql);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return this.rs;
  }

  public ResultSet scrollQuery(String sql)
  {
    try
    {
      if ((this.cnn == null) || (this.cnn.isClosed()))
        this.cnn = DataBase.getConnnection();
      this.stmt = this.cnn.createStatement();
      this.rs = this.stmt.executeQuery(sql);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return this.rs;
  }

  public void execute(String sql)
  {
    try
    {
      if ((this.cnn == null) || (this.cnn.isClosed()))
        this.cnn = DataBase.getConnnection();
      this.cnn.setAutoCommit(true);
      this.stmt = this.cnn.createStatement();
      this.stmt.execute(sql);
      this.stmt.close();
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public boolean executeUpdate(String sql)
  {
    try
    {
      if ((this.cnn == null) || (this.cnn.isClosed()))
        this.cnn = DataBase.getConnnection();
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
    try
    {
      if ((this.cnn == null) || (this.cnn.isClosed()))
        this.cnn = DataBase.getConnnection();
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
    try
    {
      if ((this.cnn == null) || (this.cnn.isClosed())) {
        this.cnn = DataBase.getConnnection();
      }
    }
    catch (SQLException localSQLException)
    {
    }
    return this.cnn;
  }

  public void close()
  {
    try
    {
      if (this.rs != null)
      {
        this.rs.close();
        this.rs = null;
      }

      if (this.stmt != null)
      {
        this.stmt.close();
        this.stmt = null;
      }
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public List get(String sql)
  {
    List lst = new ArrayList();
    if (this.cnn == null)
      this.cnn = DataBase.getConnnection();
    try
    {
      this.stmt = this.cnn.createStatement();
      this.rs = this.stmt.executeQuery(sql);
      while (this.rs.next())
      {
        Map mapValue = new HashMap();
        mapValue.put("autoid", this.rs.getString("autoid"));
        mapValue.put("e3_code", this.rs.getString("E3_CODE"));
        mapValue.put("u8_code", this.rs.getString("U8_CODE"));
        mapValue.put("title", this.rs.getString("TITLE"));
        lst.add(mapValue);
      }
      this.rs.close();
      this.stmt.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return lst;
  }

  public List queryUpdate(String sql)
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

  public List getBaseMapping() {
    return get("select *from baseinfomaping order by title");
  }
}