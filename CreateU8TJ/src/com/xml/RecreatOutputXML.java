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

public class RecreatOutputXML
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
  String tempSQL = "";

  private void finishReset() {
    this.finishFlag = true;
    this.outputFinishFlag = true;
    this.isExistsXML = false;
    this.sucessFlag = false;
    this.saveFlag = false;
  }

  public void reoutputMerchandise2XML()
  {
    this.outputFinishFlag = false;
    this.finishFlag = false;

    String sql = "select pro.item_id,pro.out_id,pro.prod_shelf,pro.bill_type as out_type,pro.requistion as maker,pro.auth_date,pro.authorize, proded.prod_id,pro.com_id,proded.quantity ,proded.real_price as price,xml_sh.out_code, pro.com_id as departmentcode,(select s.operdm from sell_out s where s.item_id=pro.item_id) as personcode,xml_sh.out_name,xml_sh.receivecode ,proded.unitage,pro.MER_CODE as manufactory,(select e.db_no from prod_db e where e.out_id=pro.out_id ) as no_id from prod_out pro,prod_out_detail proded,xml_sh_ins_out_detail xml_sh where pro.item_id=xml_sh.item_id and  pro.item_id=proded.item_id and proded.prod_id=xml_sh.prod_id  and xml_sh.errorflag!='2' and xml_sh.out_code<>'05' and xml_sh.out_code<>'06'";

    String itemSQL = "select distinct ITEM_ID from xml_sh_ins_out_detail x where x.errorflag!='2' and x.out_code<>'05' and x.out_code<>'06'";

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
      tempSQL = sql + " and pro.item_id='" + itemList.get(i) + "'  order by proded.prod_id asc ";
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
          tempValue = rs.getString("prod_shelf");
          if (tempValue == null)
            tempValue = "";
          headMap.put("prod_shelf", mappingCode(tempValue));
          tempValue = DateFormat.string2Date(rs.getString("auth_date"));
          if (tempValue == null)
            tempValue = "";
          headMap.put("auth_date", tempValue);
          tempValue = rs.getString("item_id");
          if (tempValue == null)
            tempValue = "";
          headMap.put("item_id", tempValue);

          tempValue = rs.getString("out_id");
          if (tempValue == null)
            tempValue = "";
          headMap.put("out_id", tempValue);

          tempValue = rs.getString("no_id");
          if (tempValue == null)
            tempValue = "";
          headMap.put("no_id", tempValue);

          tempValue = rs.getString("manufactory");
          if (tempValue == null)
            tempValue = "";
          else if ((tempValue.equals("00000000")) || (tempValue.equals("000000")))
            tempValue = "";
          else
            tempValue = mappingCode(tempValue);
          headMap.put("personcode", tempValue);
          headMap.put("manufactory", tempValue);
          tempValue = rs.getString("maker");
          if (tempValue == null)
            tempValue = "";
          headMap.put("maker", tempValue);

          tempValue = rs.getString("receivecode");
          if (tempValue == null)
            tempValue = "";
          headMap.put("receivecode", tempValue);

          tempValue = rs.getString("out_code");
          if (tempValue == null)
            tempValue = "";
          headMap.put("out_code", tempValue);

          tempValue = rs.getString("personcode");
          if (tempValue == null)
            tempValue = "";
          headMap.put("personcode", mappingCode(tempValue));

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
      this.queryClass
        .execute("update xml_sh_ins_out_detail set errorflag='2' where item_id='" + 
        itemId + "'");

      return;
    }
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      fname = getFile("补加出库单");
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
      this.ufinterfaceElement.setAttribute("roottag", "storeout");
      this.ufinterfaceElement.setAttribute("docid", "");
      this.ufinterfaceElement.setAttribute("proc", "add");
      this.ufinterfaceElement.setAttribute("codeexchanged", "N");
      this.ufinterfaceElement.setAttribute("exportneedexch", "N");
      this.ufinterfaceElement.setAttribute("display", "出库单");
      this.ufinterfaceElement.setAttribute("family", "库存管理");

      this.storeinoutElement = doc.createElement("storeout");

      this.headerElement = doc.createElement("header");

      this.nodeElement = doc.createElement("receiveflag");
      this.nodeElement.appendChild(doc.createTextNode("0"));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("vouchtype");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_code").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("businesstype");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_name").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("source");
      this.nodeElement.appendChild(doc.createTextNode("库存"));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("businesscode");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("no_id").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("warehousecode");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("prod_shelf").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("date");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("auth_date").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("code");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_id").toString()));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("receivecode");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_type").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("departmentcode");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("departmentcode").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("personcode");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("personcode").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("purchasetypecode");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("out_code").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("saletypecode");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("customercode");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("manufactory").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("vendorcode");

      String manufactory = headMap.get("manufactory").toString();

      this.nodeElement.appendChild(doc.createTextNode(manufactory));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("ordercode");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("quantity");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("arrivecode");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("billcode");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("consignmentcode");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("arrivedate");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("checkcode");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("checkdate");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("checkperson");
      this.nodeElement.appendChild(doc.createTextNode(""));

      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("templatenumber");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("serial");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("handler");
      this.nodeElement.appendChild(doc.createTextNode(""));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("memory");
      this.nodeElement.appendChild(doc.createTextNode(headMap.get("no_id").toString()));
      this.headerElement.appendChild(this.nodeElement);

      this.nodeElement = doc.createElement("maker");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("maker").toString()));
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

      this.nodeElement = doc.createElement("auditdate");

      this.nodeElement.appendChild(doc.createTextNode(headMap.get("auth_date").toString()));
      this.headerElement.appendChild(this.nodeElement);
      this.storeinoutElement.appendChild(this.headerElement);

      this.bodyElement = doc.createElement("body");
      for (int i = 0; i < entryList.size(); i++)
      {
        Map map = (Map)entryList.get(i);
        this.entryElement = doc.createElement("entry");

        this.nodeElement = doc.createElement("id");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("barcode");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("inventorycode");

        this.nodeElement.appendChild(doc.createTextNode(map.get("prod_id").toString()));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free1");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("free2");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("billcode");
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

        this.nodeElement = doc.createElement("shouldquantity");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("shouldnumber");
        this.nodeElement.appendChild(doc.createTextNode(""));

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

        this.nodeElement = doc.createElement("assitantunit");

        this.nodeElement.appendChild(doc.createTextNode(map.get("unitage").toString()));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("number");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("price");
        float price;
        try { price = Float.parseFloat(map.get("price").toString());
        }
        catch (NumberFormatException e)
        {
          price = 0.0F;
        }
        this.nodeElement.appendChild(doc.createTextNode((price / (1.0F + Constant.tax_rate))+""));
        //System.out.print("\nrs.getString(\"price\")" + price / (1.0F + Constant.tax_rate));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("cost");

        this.nodeElement.appendChild(doc.createTextNode(String.valueOf(price / (1.0F + Constant.tax_rate) * quantity)));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("planprice");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);
        this.nodeElement = doc.createElement("cost");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("serial");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("makedate");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("validdate");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("transitionid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("subbillcode");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("subpurchaseid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("position");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("itemclasscode");
        this.nodeElement.appendChild(doc.createTextNode(""));

        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("itemclassname");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("itemcode");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("itemname");
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

        this.nodeElement = doc.createElement("subconsignmentid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("delegateconsignmentid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("subproducingid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("subcheckid");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("cRejectCode");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("iRejectIds");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("cCheckPersonCode");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("dCheckDate");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("cCheckCode");
        this.nodeElement.appendChild(doc.createTextNode(""));
        this.entryElement.appendChild(this.nodeElement);

        this.nodeElement = doc.createElement("iMassDate");
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
      e.printStackTrace();
    }

    if (this.saveFlag)
    {
      for (int i = 0; i < this.idList.size(); i++)
      {
        Map m = (Map)this.idList.get(i);
        String updateSQL = "update xml_sh_ins_out_detail set errorflag='2' where item_id='" + 
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