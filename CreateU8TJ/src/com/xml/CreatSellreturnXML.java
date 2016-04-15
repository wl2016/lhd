package com.xml;

import com.xml.jdbc.Execute;
import com.xml.util.DateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CreatSellreturnXML
{
  private boolean finishFlag;
  private boolean outputFinishFlag;
  private boolean isExistsXML;
  Element nodeElement;
  Element bodyElement;
  Element entryElement;
  Element storeinoutElement;
  Element headerElement;
  Element ufinterfaceElement;
  static String fname = "";
  boolean sucessFlag = false;
  boolean saveFlag = false;

  List idList = new Vector();
  Execute queryClass = new Execute();

  private void finishReset()
  {
    this.finishFlag = true;
    this.outputFinishFlag = true;
    this.isExistsXML = false;
    this.sucessFlag = false;
    this.saveFlag = false;
    this.idList = null;
  }

  public void outputMerchandise2XML()
  {
    this.outputFinishFlag = false;
    this.finishFlag = false;

    String sql = "select pro.item_id,pro.sell_id,pro.back_sell_id,pro.mer_address,pro.prod_shelf,pro1.bill_type||'' as out_type,pro.requistion as maker,pro.req_date as auth_date,pro.authorize, proded.prod_id,pro.com_id,-proded.quantity as quantity,proded.real_price as price,proded.return_total,xml_sh.out_code, pro.com_id as departmentcode,substr(pro.remark,0,30) remark,pro.mer_name,(select s.operdm from sell_out s where s.item_id=pro.item_id) as personcode,(select s.item_id from sell_out s where s.sell_id=pro.back_sell_id) as back_item_id,xml_sh.out_name,xml_sh.receivecode ,proded.unitage,pro.MER_CODE as manufactory  from sell_out pro,sell_out pro1,prod_out_detail proded,xml_sh_ins_out_detail xml_sh where pro.item_id=xml_sh.item_id and pro.back_sell_id=pro1.sell_id and  pro.item_id=proded.item_id and proded.prod_id=xml_sh.prod_id and xml_sh.createflag='0' and xml_sh.out_code='06'";

    String itemSQL = "select distinct ITEM_ID from xml_sh_ins_out_detail x where x.createflag='0' and x.out_code='06'";

    ResultSet itemRs = this.queryClass.query(itemSQL);
    List itemList = new Vector();
    String tempSQL = "";
    try
    {
      while (itemRs.next())
      {
        itemList.add(itemRs.getString("item_id").trim());
      }
      itemRs.close();
    }
    catch (SQLException localSQLException)
    {
    }

    for (int i = 0; i < itemList.size(); i++)
    {
      tempSQL = sql + " and pro.item_id='" + itemList.get(i) + "'";
      System.out.println(" 生成销售发货单 ： "+tempSQL);
      ResultSet rs = this.queryClass.query(tempSQL);
      if (rs == null)
      {
        continue;
      }

      writeFile(rs, itemList.get(i).toString());
    }

    this.queryClass.close();
    finishReset();
  }

  private void writeFile(ResultSet rs, String itemId)
  {
    List entryList = new Vector();
    Map headMap = new HashMap();
    boolean headFlag = true;
    String tempValue = "";
    this.idList = new Vector();
    try
    {
      while (rs.next())
      {
        if (headFlag)
        {
          tempValue = rs.getString("out_type");
          if (tempValue == null)
            tempValue = "";
          headMap.put("out_type", mappingCode(tempValue));
          tempValue = rs.getString("out_name");
          if (tempValue == null)
            tempValue = "";
          headMap.put("out_name", tempValue);

          tempValue = DateFormat.string2Date(rs.getString("auth_date"));
          if (tempValue == null)
            tempValue = "";
          headMap.put("auth_date", tempValue);
          tempValue = rs.getString("item_id");
          if (tempValue == null)
            tempValue = "";
          headMap.put("item_id", tempValue);
          tempValue = rs.getString("sell_id");
          if (tempValue == null)
            tempValue = "";
          headMap.put("sell_id", tempValue);
          tempValue = rs.getString("manufactory");
          if (tempValue == null)
            tempValue = "";
          else if ((tempValue.equals("00000000")) || (tempValue.equals("000000")))
            tempValue = "";
          else
            tempValue = mappingCode(tempValue);
          headMap.put("manufactory", tempValue);

          tempValue = rs.getString("remark");
          if (tempValue == null)
            tempValue = "";
          headMap.put("remark", tempValue);

          tempValue = rs.getString("maker");
          if (tempValue == null)
            tempValue = "";
          headMap.put("maker", tempValue);

          tempValue = rs.getString("receivecode");
          if (tempValue == null)
            tempValue = "";
          headMap.put("receivecode", tempValue);

          tempValue = "05";
          if (tempValue == null)
            tempValue = "";
          headMap.put("out_code", tempValue);

          tempValue = rs.getString("personcode");
          if (tempValue == null)
            tempValue = "";
          else
            tempValue = mappingCode(tempValue);
          headMap.put("personcode", tempValue);

          tempValue = rs.getString("mer_name");
          if (tempValue == null)
            tempValue = "";
          headMap.put("mer_name", tempValue);

          tempValue = rs.getString("mer_address");
          if (tempValue == null)
            tempValue = "";
          headMap.put("mer_address", tempValue);

          tempValue = rs.getString("departmentcode");
          if (tempValue == null)
            tempValue = "";
          headMap.put("departmentcode", mappingCode(tempValue));

          headFlag = false;
        }
        Map entryMap = new HashMap();
        Map idMap = new HashMap();
        idMap.put("item_id", rs.getString("item_id"));
        idMap.put("prod_id", rs.getString("prod_id"));
        this.idList.add(idMap);

        tempValue = rs.getString("item_id");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("item_id", tempValue);

        tempValue = rs.getString("prod_id");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("prod_id", mappingCode(tempValue));

        tempValue = rs.getString("quantity");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("quantity", tempValue);

        tempValue = rs.getString("unitage");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("unitage", tempValue);

        tempValue = rs.getString("price");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("price", tempValue);

        tempValue = rs.getString("prod_shelf");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("prod_shelf", mappingCode(tempValue));

        tempValue = rs.getString("back_sell_id");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("back_sell_id", tempValue);

        tempValue = rs.getString("back_item_id");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("back_item_id", tempValue);

        tempValue = rs.getString("return_total");
        if (tempValue == null)
          tempValue = "";
        entryMap.put("return_total", tempValue);
        entryList.add(entryMap);
      }

      rs.close();
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    if (entryList.size() == 0)
    {
      String errSQL = "update xml_sh_ins_out_detail  set createflag='1' where item_id='" + 
        itemId + "'";
      this.queryClass
        .execute(errSQL);
      //System.out.print("\n errSQL=" + errSQL);

      return;
    }
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      fname = getFile("销售退货单");
      Document doc;
      if (this.isExistsXML)
      {
        doc = builder.parse(new FileInputStream(fname));
        this.ufinterfaceElement = ((Element)doc.getElementsByTagName("ufinterface").item(0));
      }
      else
      {
        doc = builder.newDocument();
        this.ufinterfaceElement = doc.createElement("ufinterface");
      }
      doc.normalize();
      this.ufinterfaceElement.setAttribute("sender", "");
      this.ufinterfaceElement.setAttribute("receiver", "u8");
      this.ufinterfaceElement.setAttribute("roottag", "consignment");
      this.ufinterfaceElement.setAttribute("docid", "");
      this.ufinterfaceElement.setAttribute("proc", "add");
      this.ufinterfaceElement.setAttribute("codeexchanged", "N");
      this.ufinterfaceElement.setAttribute("exportneedexch", "N");
      this.ufinterfaceElement.setAttribute("display", "销售退货单");
      this.ufinterfaceElement.setAttribute("family", "销售管理");

      this.storeinoutElement = doc.createElement("consignment");

      this.headerElement = doc.createElement("header");

      this.nodeElement = doc.createElement("id");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("item_id").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("code");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("sell_id").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("vouchertype");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_code").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("saletype");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_type").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("date");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("auth_date").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("deptcode");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("departmentcode").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("personcode");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("personcode").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("custcode");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("manufactory").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("paycondition_code");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("shippingchoice");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("address");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("mer_address").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("currency_name");
      this.nodeElement.appendChild(doc.createTextNode("人民币"));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("currency_rate");
      this.nodeElement.appendChild(doc.createTextNode("1"));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("taxrate");
      this.nodeElement.appendChild(doc.createTextNode(String.valueOf(Constant.tax_rate * 100.0F)));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("beginflag");
      this.nodeElement.appendChild(doc.createTextNode("0"));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("returnflag");
      this.nodeElement.appendChild(doc.createTextNode("1"));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("remark");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("remark").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define1");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define2");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define3");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define4");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define5");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define6");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define7");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define8");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define9");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define10");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define11");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define12");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define13");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define14");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define15");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("define16");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("maker");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("maker").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("sales_cons_flag");
      this.nodeElement.appendChild(doc.createTextNode("0"));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("retail_custname");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("mer_name").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("operation_type");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_name").toString()));
      this.headerElement.appendChild(this.nodeElement);
      this.storeinoutElement.appendChild(this.headerElement);

      this.bodyElement = doc.createElement("body");
      for (int i = 0; i < entryList.size(); i++)
      {
        Map map = (Map)entryList.get(i);
        this.entryElement = doc.createElement("entry");

        this.nodeElement = doc.createElement("headid");
        this.nodeElement.appendChild(doc.createTextNode(map.get("item_id").toString()));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("warehouse_code");
        this.nodeElement.appendChild(doc.createTextNode(map.get("prod_shelf").toString()));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("inventory_code");
        this.nodeElement.appendChild(doc.createTextNode(map.get("prod_id").toString()));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("quantity");
        int quantity = 0;
        try
        {
          quantity = Integer.parseInt(map.get("quantity").toString());
        }
        catch (NumberFormatException e)
        {
          quantity = 0; } this.nodeElement.appendChild(doc.createTextNode(quantity+""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("num");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("assistantunit");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("quotedprice");

        this.nodeElement.appendChild(doc.createTextNode("0"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("price");
        float price;
        float return_total;
        try {
        	price = Float.parseFloat(map.get("price").toString());
        	return_total = Float.parseFloat(map.get("return_total").toString());
        }
        catch (NumberFormatException e)
        {
          price = 0.0F;
          return_total = 0.0F;
        }
        this.nodeElement.appendChild(doc.createTextNode(((price - return_total) / (1.0F + Constant.tax_rate))+""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("taxprice");
        this.nodeElement.appendChild(doc.createTextNode((price - return_total)+""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("money");

        this.nodeElement.appendChild(doc.createTextNode(String.valueOf((price - return_total) / (1.0F + Constant.tax_rate) * quantity)));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("tax");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf((price - return_total) / (1.0F + Constant.tax_rate) * Constant.tax_rate * quantity)));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("sum");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf(price * quantity - return_total * quantity)));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("discount");

        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("natprice");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf((price - return_total) / (1.0F + Constant.tax_rate))));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("natmoney");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf((price - return_total) / (1.0F + Constant.tax_rate) * quantity)));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("nattax");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf((price - return_total) / (1.0F + Constant.tax_rate) * Constant.tax_rate * quantity)));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("natsum");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf(price * quantity - return_total * quantity)));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("natdiscount");

        this.nodeElement.appendChild(doc.createTextNode("0"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("batch");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("remark");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("backflag");
        this.nodeElement.appendChild(doc.createTextNode("0"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("overdate");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("backquantity");
        this.nodeElement.appendChild(doc.createTextNode(quantity+""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("backnum");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("discount1");
        this.nodeElement.appendChild(doc.createTextNode("100"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("discount2");
        this.nodeElement.appendChild(doc.createTextNode("100"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("inventory_printname");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("taxrate");
        this.nodeElement.appendChild(doc.createTextNode(String.valueOf(Constant.tax_rate * 100.0F)));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("item_class");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("item_classname");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("item_code");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("item_name");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("retail_price");
        this.nodeElement.appendChild(doc.createTextNode("0"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("retail_money");
        this.nodeElement.appendChild(doc.createTextNode("0"));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("vendor_name");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("unitrate");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("unit_code");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free1");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free2");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free3");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free4");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free5");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free6");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free7");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free8");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free9");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free10");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define22");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define23");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define24");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define25");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define26");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define27");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define28");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define29");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define30");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define31");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define32");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define33");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define34");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define35");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define36");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("define37");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("ccorcode");
        this.nodeElement.appendChild(doc.createTextNode(map.get("back_sell_id").toString()));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("icorid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);
        this.bodyElement.appendChild(this.entryElement);
      }
      this.storeinoutElement.appendChild(this.bodyElement);
      this.ufinterfaceElement.appendChild(this.storeinoutElement);
      this.sucessFlag = true;

      if (!this.isExistsXML) {
        doc.appendChild(this.ufinterfaceElement);
      }
      if (this.sucessFlag)
      {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty("encoding", "utf-8");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(fname);
        transformer.transform(source, result);
        this.saveFlag = true;
        //System.out.print("\n 时间：" + new Date().toLocaleString() + " 结果：写入成功");
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    catch (SAXException ex)
    {
      ex.printStackTrace();
    }
    catch (ParserConfigurationException ex)
    {
      ex.printStackTrace();
    }
    catch (TransformerException e)
    {
      this.saveFlag = false;
      System.out.print("&&&&&&&  e = "+e.getMessage());
      e.printStackTrace();
    }

    if (this.saveFlag)
    {
      for (int i = 0; i < this.idList.size(); i++)
      {
        Map m = (Map)this.idList.get(i);
        String updateSQL = "update xml_sh_ins_out_detail set createflag='1' where item_id='" + 
          m.get("item_id") + "' and prod_id='" + m.get("prod_id") + "'";
        this.queryClass.execute(updateSQL);
      }
    }
  }

  public boolean getFinishFlag()
  {
    return this.finishFlag;
  }

  public void setFinishFlag(boolean finishFlag) {
    this.finishFlag = finishFlag;
  }

  public String getFile(String dir)
  {
    String fn = ""; String folder = "";
    fn = DateFormat.getCurrentDate();
    folder = Constant.STORE_IN_OUT_XML + "/" + dir;
    File file = new File(folder);

    if (!file.exists())
      file.mkdirs();
    fn = folder + "/" + dir + fn + ".xml";
    fn = fn.replaceAll("\\\\", "/");
    fn = fn.replaceAll("//", "/");
    this.isExistsXML = false;
    file = new File(fn);
    if (file.exists())
    {
      this.isExistsXML = true;
      return fn;
    }
    return fn;
  }

  protected static String mappingCode(String src)
  {
    List lst = Constant.BASE_MESSAGE_MAPPING;
    if (lst == null)
      return src;
    if ((src == null) || (src.equals("")))
      return "";
    src = src.trim();
    for (int i = 0; i < lst.size(); i++)
    {
      Map codeMap = (Map)lst.get(i);

      if (codeMap.get("title").toString().equals("入库类型"))
        continue;
      if (codeMap.get("e3_code").toString().equals(src)) {
        return codeMap.get("u8_code").toString();
      }
    }
    return src;
  }

  public boolean getOutputFinishFlag() {
    return this.outputFinishFlag;
  }

  public void setOutputFinishFlag(boolean flg) {
    this.outputFinishFlag = flg;
  }

  public boolean getExistsXML() {
    return this.isExistsXML;
  }

  public void setExistsXML(boolean isExistsXML) {
    this.isExistsXML = isExistsXML;
  }
}