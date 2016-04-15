package com.xml.jdbc;

import com.xml.Constant;
import com.xml.MappingCode;
import com.xml.util.Encodeing;
import com.xml.util.StringUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

public class JdbcTemplet
{
  private String sql = "";
  private String returnString = "";
  private Execute exe = new Execute();

  public String execute(HttpServletRequest request)
  {
    String chartset = request.getCharacterEncoding();
    String act = request.getParameter("act");
    String autoid = request.getParameter("autoid");
    String e3_code = request.getParameter("e3_code");
    String u8_code = request.getParameter("u8_code");
    String title = Encodeing.Unicode2Chartset(request.getParameter("title"), "GBK");
    e3_code = StringUtil.stringFilter(e3_code);
    u8_code = StringUtil.stringFilter(u8_code);
    title = StringUtil.stringFilter(title);
    autoid = StringUtil.stringFilter(autoid);
    if (act == null)
      return "";
    if (act.equals("add"))
    {
      if ((e3_code.equals("")) || (u8_code.equals("")) || (title.equals("")))
        return "";
      this.sql = ("select autoid from baseinfomaping where e3_code='" + e3_code + "' and title='" + title + "'");
      if (this.exe.checkExists(this.sql))
        return "该代码已经存在";
      this.sql = 
        ("insert into baseinfomaping values(sys_guid(),'" + e3_code + "','" + u8_code + "','" + 
        title + "')");
    }
    else if (act.equals("update"))
    {
      if ((e3_code.equals("")) || (u8_code.equals("")) || (title.equals("")))
        return "";
      this.sql = ("select autoid from baseinfomaping where e3_code='" + e3_code + "' and title='" + title + "' and autoid <>'" + autoid + "'");
      if (this.exe.checkExists(this.sql))
        return "该代码已经存在";
      this.sql = 
        ("update baseinfomaping set e3_code='" + e3_code + "',u8_code='" + u8_code + "',title='" + 
        title + "' where autoid='" + autoid + "'");
    }
    else if (act.equals("del"))
    {
      this.sql = ("delete from baseinfomaping where autoid='" + autoid + "'");
    }

    if (this.exe.executeUpdate(this.sql))
    {
      this.returnString = "操作成功";
    }
    else {
      this.returnString = "操作失败，请检查数据";
    }
    Constant.BASE_MESSAGE_MAPPING = new Execute().getBaseMapping();
    return this.returnString;
  }

  public List list(HttpServletRequest request)
  {
    String title = request.getParameter("title");

    title = Encodeing.Unicode2Chartset(title, "GBK");

    title = StringUtil.stringFilter(title);

    if (title.equals(""))
    {
      this.sql = "select * from baseinfomaping  order by title ";
    }
    else {
      this.sql = ("select * from baseinfomaping where title='" + title + "' order by title ");
    }
    return this.exe.get(this.sql);
  }

  public List get(HttpServletRequest request)
  {
    String autoid = request.getParameter("autoid");
    autoid = StringUtil.stringFilter(autoid);
    this.sql = ("select * from baseinfomaping where autoid='" + autoid + "'");
    return this.exe.get(this.sql);
  }

  public List query(String sql)
  {
    return this.exe.queryUpdate(sql);
  }

  public List queryInputXML(String sql)
  {
    ResultSet rs = this.exe.query(sql);
    List lst = new Vector();
    try
    {
      while (rs.next())
      {
        Map map = new HashMap();
        map.put("bill_id", rs.getString("bill_id"));
        map.put("item_id", rs.getString("item_id"));
        map.put("createflag", rs.getString("createflag"));
        map.put("prod_shelf", MappingCode.inputMappingCode(rs.getString("prod_shelf")));
        map.put("prod_id", rs.getString("prod_id"));
        map.put("com_id", MappingCode.inputMappingCode(rs.getString("com_id")));
        map.put("manufactory", MappingCode.inputMappingCode(rs.getString("manufactory")));
        map.put("personcode", MappingCode.inputMappingCode(rs.getString("personcode")));
        map.put("maker", rs.getString("maker"));
        map.put("auth_date", rs.getDate("auth_date"));
        float price = rs.getFloat("price");
        int quantity = rs.getInt("quantity");
        map.put("price", price);
        map.put("quantity", quantity);
        map.put("enter_name", rs.getString("enter_name"));

        map.put("unitage", rs.getString("unitage"));
        float total = price * quantity / (Constant.tax_rate + 1.0F);

        map.put("total", total);
        lst.add(map);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return lst;
  }

  public List queryOutputXML(String sql)
  {
    ResultSet rs = this.exe.query(sql);
    List lst = new Vector();
    try
    {
      while (rs.next())
      {
        Map map = new HashMap();
        map.put("bill_id", rs.getString("bill_id"));
        map.put("item_id", rs.getString("item_id"));
        map.put("createflag", rs.getString("createflag"));
        map.put("prod_id", rs.getString("prod_id"));
        map.put("prod_shelf", MappingCode.outputMappingCode(rs.getString("prod_shelf")));
        map.put("com_id", MappingCode.outputMappingCode(rs.getString("com_id")));
        map.put("manufactory", MappingCode.outputMappingCode(rs.getString("manufactory")));
        map.put("personcode", MappingCode.outputMappingCode(rs.getString("personcode")));
        map.put("maker", rs.getString("maker"));
        map.put("auth_date", rs.getDate("auth_date"));
        map.put("quantity", rs.getString("quantity"));
        map.put("price", rs.getString("price"));
        map.put("out_name", rs.getString("out_name"));
        float price = rs.getFloat("price");
        int quantity = rs.getInt("quantity");
        map.put("quantity", quantity);
        map.put("quantity", quantity);
        map.put("price", price);
        float total = price * quantity / (1.0F + Constant.tax_rate);
        map.put("total", total);
        lst.add(map);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return lst;
  }

  public List querySellputXML(String sql)
  {
    ResultSet rs = this.exe.query(sql);
    List lst = new Vector();
    try
    {
      while (rs.next())
      {
        Map map = new HashMap();
        map.put("bill_id", rs.getString("bill_id"));
        map.put("item_id", rs.getString("item_id"));
        map.put("createflag", rs.getString("createflag"));
        map.put("prod_id", rs.getString("prod_id"));
        map.put("prod_shelf", MappingCode.outputMappingCode(rs.getString("prod_shelf")));
        map.put("com_id", MappingCode.outputMappingCode(rs.getString("com_id")));
        map.put("manufactory", MappingCode.outputMappingCode(rs.getString("manufactory")));
        map.put("personcode", MappingCode.outputMappingCode(rs.getString("personcode")));
        map.put("maker", rs.getString("maker"));
        map.put("auth_date", rs.getDate("auth_date"));
        map.put("quantity", rs.getString("quantity"));
        map.put("price", rs.getString("price"));
        map.put("out_name", rs.getString("out_name"));
        float price = rs.getFloat("price");
        int quantity = rs.getInt("quantity");
        map.put("quantity", quantity);
        map.put("quantity", quantity);
        map.put("price", price);
        float total = price * quantity / (1.0F + Constant.tax_rate);
        map.put("total", total);
        lst.add(map);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return lst;
  }

  public List queryDiaoBoXML(String sql)
  {
    ResultSet rs = this.exe.query(sql);
    List lst = new Vector();
    try
    {
      while (rs.next())
      {
        Map map = new HashMap();
        map.put("bill_id", rs.getString("bill_id"));
        map.put("item_id", rs.getString("item_id"));
        map.put("createflag", rs.getString("createflag"));
        map.put("prod_id", rs.getString("prod_id"));
        map.put("prod_shelf", MappingCode.outputMappingCode(rs.getString("prod_shelf")));
        map.put("com_id", MappingCode.outputMappingCode(rs.getString("com_id")));
        map.put("manufactory", MappingCode.outputMappingCode(rs.getString("manufactory")));
        map.put("personcode", MappingCode.outputMappingCode(rs.getString("personcode")));
        map.put("maker", rs.getString("maker"));
        map.put("auth_date", rs.getDate("auth_date"));
        map.put("quantity", rs.getString("quantity"));
        map.put("price", rs.getString("price"));
        map.put("out_name", rs.getString("out_name"));
        map.put("prod_shelf_out_from", MappingCode.inputMappingCode(rs.getString("prod_shelf_out_from")));
        float price = rs.getFloat("price");
        int quantity = rs.getInt("quantity");
        map.put("quantity", quantity);
        map.put("quantity", quantity);
        map.put("price", price);
        float total = price * quantity / (1.0F + Constant.tax_rate);
        map.put("total", total);
        lst.add(map);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return lst;
  }
}