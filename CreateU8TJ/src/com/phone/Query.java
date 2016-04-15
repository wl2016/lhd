package com.phone;

import com.xml.jdbc.DataBase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query
{
  ResultSet rs = null;
  Statement stmt;

  public boolean query(String sql)
  {
    boolean flg = false;
    try
    {
      this.stmt = DataBase.getConnnection().createStatement();
      this.rs = this.stmt.executeQuery(sql);
      if (this.rs.next())
      {
        flg = true;
      }
      this.rs.close();
      this.stmt.close();
    }
    catch (SQLException localSQLException)
    {
    }

    return flg;
  }
}