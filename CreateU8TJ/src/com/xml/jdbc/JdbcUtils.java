package com.xml.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public abstract interface JdbcUtils
{
  public abstract Object getResultSetValue(ResultSet paramResultSet, int paramInt)
    throws SQLException;

  public abstract void getProcessRow(ResultSet paramResultSet)
    throws SQLException;

  public abstract Map getRowValue(ResultSet paramResultSet)
    throws SQLException;
}