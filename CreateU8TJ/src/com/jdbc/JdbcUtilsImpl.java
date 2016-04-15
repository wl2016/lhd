package com.jdbc;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JdbcUtilsImpl
  implements JdbcUtils
{
  int columnCount;
  int rowCount;
  int[] columnTypes;
  String[] columnNames;

  public Object getResultSetValue(ResultSet rs, int index)
    throws SQLException
  {
    Object obj = rs.getObject(index);
    if ((obj instanceof Blob))
    {
      obj = rs.getBytes(index);
    }
    else if ((obj instanceof Clob))
    {
      obj = rs.getString(index);
    }
    else if ((obj != null) && 
      (obj.getClass().getName().startsWith("oracle.sql.TIMESTAMP")))
    {
      obj = rs.getTimestamp(index);
    }
    else if ((obj != null) && 
      (obj.getClass().getName().startsWith("oracle.sql.DATE")))
    {
      String metaDataClassName = rs.getMetaData().getColumnClassName(index);
      if (("java.sql.Timestamp".equals(metaDataClassName)) || 
        ("oracle.sql.TIMESTAMP".equals(metaDataClassName)))
      {
        obj = rs.getTimestamp(index);
      }
      else
      {
        obj = rs.getDate(index);
      }
    }
    else if ((obj != null) && ((obj instanceof Date)))
    {
      if ("java.sql.Timestamp".equals(rs.getMetaData()
        .getColumnClassName(index)))
      {
        obj = rs.getTimestamp(index);
      }
    }
    return obj;
  }

  public final void getProcessRow(ResultSet rs)
    throws SQLException
  {
    if (this.rowCount == 0)
    {
      ResultSetMetaData rsmd = rs.getMetaData();
      this.columnCount = rsmd.getColumnCount();
      this.columnTypes = new int[this.columnCount];
      this.columnNames = new String[this.columnCount];
      for (int i = 0; i < this.columnCount; i++)
      {
        this.columnTypes[i] = rsmd.getColumnType(i + 1);
        this.columnNames[i] = rsmd.getColumnName(i + 1);
      }
    }
  }

  public Map getRowValue(ResultSet rs)
    throws SQLException
  {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    Map mapOfColValues = new HashMap();
    for (int i = 1; i <= columnCount; i++)
    {
      String key = rsmd.getColumnName(i);
      Object obj = getResultSetValue(rs, i);

      mapOfColValues.put(key.toLowerCase(), obj);
    }
    return mapOfColValues;
  }
}