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

public class DiaoboXML
{
  public static boolean finishFlag;
  public static boolean outputFinishFlag;
  private static boolean isExistsXML;

  private static synchronized boolean inputMerchandise2XML(ResultSet rs)
  {
    String sql = "";
    Execute in_queryClass = new Execute();

    String fname = getFile("周转库入库单");
    boolean innerIsExists = isExistsXML;

    boolean sucessFlag = false;
    boolean saveFlag = false;

    int codeIndex = 1;
    if (rs == null)
    {
      //System.out.print("\n resultSet is null");
      return false;
    }

    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Element ufinterfaceElement;
      Document doc;
      if (innerIsExists)
      {
        doc = builder.parse(new FileInputStream(fname));
        ufinterfaceElement = (Element)doc.getElementsByTagName("ufinterface").item(0);
      }
      else
      {
        doc = builder.newDocument();
        ufinterfaceElement = doc.createElement("ufinterface");
      }

      doc.normalize();

      ufinterfaceElement.setAttribute("sender", "");
      ufinterfaceElement.setAttribute("receiver", "u8");
      ufinterfaceElement.setAttribute("roottag", "storein");
      ufinterfaceElement.setAttribute("docid", "");
      ufinterfaceElement.setAttribute("proc", "add");
      ufinterfaceElement.setAttribute("codeexchanged", "N");
      ufinterfaceElement.setAttribute("exportneedexch", "N");
      ufinterfaceElement.setAttribute("display", "入库单");
      ufinterfaceElement.setAttribute("family", "库存管理");
      //System.out.print("\n start to read resultset records");

      //System.out.print("\n  read resultset records");
      while (rs.next())
      {
        Element storeinoutElement = doc.createElement("storein");

        Element headerElement = doc.createElement("header");

        Element nodeElement = doc.createElement("receiveflag");
        nodeElement.appendChild(doc.createTextNode("1"));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("vouchtype");
        nodeElement.appendChild(doc.createTextNode("08"));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("businesstype");
        nodeElement.appendChild(doc.createTextNode("其他入库"));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("source");
        nodeElement.appendChild(doc.createTextNode("库存"));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("businesscode");
        nodeElement.appendChild(doc.createTextNode(rs.getString("out_id")));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("warehousecode");

        nodeElement.appendChild(doc.createTextNode(rs.getString("prod_shelf")));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("date");
        nodeElement.appendChild(doc.createTextNode(DateFormat.string2Date(rs.getString("auth_date"))));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("code");
        nodeElement.appendChild(doc.createTextNode(rs.getString("enter_id")));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("receivecode");

        nodeElement.appendChild(doc.createTextNode("0103"));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("departmentcode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("com_id"))));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("personcode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("personcode"))));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("purchasetypecode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("saletypecode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("customercode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("vendorcode");

        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("ordercode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("quantity");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("arrivecode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("billcode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("consignmentcode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("arrivedate");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("checkcode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("checkdate");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("checkperson");
        nodeElement.appendChild(doc.createTextNode(rs.getString("authorize") == null ? "" : rs.getString("authorize")));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("templatenumber");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("serial");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("handler");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("memory");
        nodeElement.appendChild(doc.createTextNode(rs.getString("out_id") + "," + rs.getString("bill_no")));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("maker");
        nodeElement.appendChild(doc.createTextNode(rs.getString("maker") == null ? "" : rs.getString("maker")));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define1");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define2");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define3");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define4");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define5");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define6");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define7");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define8");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define9");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define10");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define11");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define12");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define13");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define14");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define15");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define16");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("auditdate");
        nodeElement.appendChild(doc.createTextNode(DateFormat.string2Date(rs.getString("auth_date"))));
        headerElement.appendChild(nodeElement);
        storeinoutElement.appendChild(headerElement);

        Element bodyElement = doc.createElement("body");
        Element entryElement = doc.createElement("entry");

        nodeElement = doc.createElement("id");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("barcode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("inventorycode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("prod_id"))));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free1");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free2");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free3");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free4");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free5");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free6");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free7");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free8");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free9");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free10");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("shouldquantity");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("shouldnumber");
        nodeElement.appendChild(doc.createTextNode(""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("quantity");
        int quantity = rs.getInt("quantity");
        nodeElement.appendChild(doc.createTextNode(quantity+""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("assitantunit");
        nodeElement.appendChild(doc.createTextNode(rs.getString("unitage") == null ? "" : rs.getString("unitage")));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("number");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("price");
        float price = rs.getFloat("enter_price");
        nodeElement.appendChild(doc.createTextNode((price / (1.0F + Constant.tax_rate))+""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cost");

        nodeElement.appendChild(doc.createTextNode(String.valueOf(price / (1.0F + Constant.tax_rate) * quantity)));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("planprice");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);
        nodeElement = doc.createElement("cost");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("serial");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("makedate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("validdate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("transitionid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subbillcode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subpurchaseid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("position");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemclasscode");
        nodeElement.appendChild(doc.createTextNode(""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemclassname");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemcode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemname");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define22");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define23");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define24");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define25");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define26");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define27");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define28");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define29");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define30");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define31");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define32");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define33");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define34");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define35");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define36");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define37");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subconsignmentid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("delegateconsignmentid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subproducingid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subcheckid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cRejectCode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("iRejectIds");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cCheckPersonCode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("dCheckDate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cCheckCode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("iMassDate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        bodyElement.appendChild(entryElement);
        storeinoutElement.appendChild(bodyElement);
        ufinterfaceElement.appendChild(storeinoutElement);
        sucessFlag = true;
      }

      //System.out.print("\n  read resultset records end ");

      if (!innerIsExists) {
        doc.appendChild(ufinterfaceElement);
      }
      if (sucessFlag)
      {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty("encoding", "utf-8");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(fname);
        transformer.transform(source, result);
        saveFlag = true;
        //System.out.print("\n 时间：" + new Date().toLocaleString() + "调拨入库 结果：写入成功");
      }
      rs.close();
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
      saveFlag = false;
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      saveFlag = false;
    }
    catch (SAXException ex)
    {
      ex.printStackTrace();
      saveFlag = false;
    }
    catch (ParserConfigurationException ex)
    {
      ex.printStackTrace();
      saveFlag = false;
    }
    catch (TransformerException e)
    {
      e.printStackTrace();
      saveFlag = false;
    }
    rs = null;

    return saveFlag;
  }

  public static synchronized void outputMerchandise2XML()
  {
    String sql = "";
    String sql_out = "";
    String fname = getFile("安装机出库单");
    boolean innerIsExists = isExistsXML;

    sql = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (select pro.item_id,pro.enter_id,pro.sell_id as out_id,pro.prod_shelf,pro.requistion as maker,pro.auth_date,ins.od_date create_date,pro.authorize,proded.prod_id,pro.com_id,proded.quantity ,proded.real_price as price,proded.unitage,pro.MER_CODE as manufactory,xml_sh.out_code,xml_sh.out_name,xml_sh.receivecode,xml_sh.prod_shelf_out_from,(select e.emp_id from employee e where e.emp_name=pro.authorize ) as personcode,sh_out.bill_no,ins_prod.enter_price from prod_enter pro,prod_enter_detail proded,xml_diaobo xml_sh,sh_ins_out sh_out,installation ins,ins_product_list ins_prod where pro.item_id=xml_sh.item_id and  pro.item_id=proded.item_id and xml_sh.out_id=sh_out.out_id and ins_prod.prod_id=proded.prod_id and sh_out.bill_no = ins.bill_no and xml_sh.createflag='0' ) A WHERE ROWNUM <= 100) WHERE RN >= 0";

    boolean sucessFlag = false;
    boolean saveFlag = false;
    List idList = new Vector();

    outputFinishFlag = false;
    Execute queryClass = new Execute();
    ResultSet rs = queryClass.scrollQuery(sql);
    if (rs == null) {
      return;
    }
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Element ufinterfaceElement;
      Document doc;
      if (innerIsExists)
      {
        doc = builder.parse(new FileInputStream(fname));
        ufinterfaceElement = (Element)doc.getElementsByTagName("ufinterface").item(0);
      }
      else
      {
        doc = builder.newDocument();
        ufinterfaceElement = doc.createElement("ufinterface");
      }
      doc.normalize();

      ufinterfaceElement.setAttribute("sender", "");
      ufinterfaceElement.setAttribute("receiver", "u8");
      ufinterfaceElement.setAttribute("roottag", "storeout");
      ufinterfaceElement.setAttribute("docid", "");
      ufinterfaceElement.setAttribute("proc", "add");
      ufinterfaceElement.setAttribute("codeexchanged", "N");
      ufinterfaceElement.setAttribute("exportneedexch", "N");
      ufinterfaceElement.setAttribute("display", "出库单");
      ufinterfaceElement.setAttribute("family", "库存管理");
      while (rs.next())
      {
        Element storeinoutElement = doc.createElement("storeout");

        Element headerElement = doc.createElement("header");

        Element nodeElement = doc.createElement("receiveflag");
        nodeElement.appendChild(doc.createTextNode("0"));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("vouchtype");
        nodeElement.appendChild(doc.createTextNode("09"));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("businesstype");
        nodeElement.appendChild(doc.createTextNode("其他出库"));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("source");
        nodeElement.appendChild(doc.createTextNode("库存"));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("businesscode");
        nodeElement.appendChild(doc.createTextNode(rs.getString("out_id")));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("warehousecode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("prod_shelf"))));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("date");
        nodeElement.appendChild(doc.createTextNode(DateFormat.string2Date(rs.getString("auth_date"))));
        headerElement.appendChild(nodeElement);         

        nodeElement = doc.createElement("code");
        nodeElement.appendChild(doc.createTextNode(rs.getString("out_id")));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("receivecode");

        nodeElement.appendChild(doc.createTextNode("0203"));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("departmentcode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("com_id"))));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("personcode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("personcode"))));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("purchasetypecode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("saletypecode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("customercode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("vendorcode");

        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("ordercode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("quantity");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("arrivecode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("billcode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("consignmentcode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("arrivedate");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("checkcode");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("checkdate");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("checkperson");
        nodeElement.appendChild(doc.createTextNode(rs.getString("authorize") == null ? "" : rs.getString("authorize")));

        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("templatenumber");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("serial");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("handler");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("memory");
        nodeElement.appendChild(doc.createTextNode(rs.getString("out_id") + "," + rs.getString("bill_no")));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("maker");
        nodeElement.appendChild(doc.createTextNode(rs.getString("maker") == null ? "" : rs.getString("maker")));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define1");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define2");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define3");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define4");
        nodeElement.appendChild(doc.createTextNode(DateFormat.string2Date(rs.getString("create_date"))));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define5");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define6");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define7");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define8");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define9");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define10");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define11");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define12");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define13");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define14");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define15");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define16");
        nodeElement.appendChild(doc.createTextNode(""));
        headerElement.appendChild(nodeElement);

        nodeElement = doc.createElement("auditdate");
        nodeElement.appendChild(doc.createTextNode(DateFormat.string2Date(rs.getString("auth_date"))));
        headerElement.appendChild(nodeElement);
        storeinoutElement.appendChild(headerElement);

        Map idMap = new HashMap();
        idMap.put("item_id", rs.getString("item_id"));
        idMap.put("prod_id", rs.getString("prod_id"));
        idList.add(idMap);

        Element bodyElement = doc.createElement("body");
        Element entryElement = doc.createElement("entry");

        nodeElement = doc.createElement("id");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("barcode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("inventorycode");
        nodeElement.appendChild(doc.createTextNode(mappingCode(rs.getString("prod_id"))));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free1");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free2");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free3");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free4");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free5");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free6");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free7");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free8");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free9");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("free10");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("shouldquantity");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("shouldnumber");
        nodeElement.appendChild(doc.createTextNode(""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("quantity");
        int quantity = rs.getInt("quantity");
        nodeElement.appendChild(doc.createTextNode(quantity+""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("assitantunit");
        nodeElement.appendChild(doc.createTextNode(rs.getString("unitage") == null ? "" : rs.getString("unitage")));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("number");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("price");
        float price = rs.getFloat("enter_price");
        nodeElement.appendChild(doc.createTextNode((price / (1.0F + Constant.tax_rate))+""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cost");

        nodeElement.appendChild(doc.createTextNode(String.valueOf(price / (1.0F + Constant.tax_rate) * quantity)));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("planprice");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);
        nodeElement = doc.createElement("cost");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("serial");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("makedate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("validdate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("transitionid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subbillcode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subpurchaseid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("position");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemclasscode");
        nodeElement.appendChild(doc.createTextNode(""));

        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemclassname");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemcode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("itemname");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define22");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define23");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define24");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define25");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define26");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define27");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define28");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define29");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define30");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define31");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define32");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define33");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define34");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define35");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define36");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("define37");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subconsignmentid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("delegateconsignmentid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subproducingid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("subcheckid");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cRejectCode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("iRejectIds");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cCheckPersonCode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("dCheckDate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("cCheckCode");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        nodeElement = doc.createElement("iMassDate");
        nodeElement.appendChild(doc.createTextNode(""));
        entryElement.appendChild(nodeElement);

        bodyElement.appendChild(entryElement);
        storeinoutElement.appendChild(bodyElement);
        ufinterfaceElement.appendChild(storeinoutElement);
        sucessFlag = true;
      }

      if (!innerIsExists) {
        doc.appendChild(ufinterfaceElement);
      }

      if (sucessFlag)
      {
//    	sql = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM (select ins.bill_no item_id,ins.bill_no enter_id,ins.bill_no as out_id,ins.prod_shelf,ins.operator as maker,ins.audi_time auth_date,ins.od_date create_date,ins.audi_er authorize,ins.prod_id,ins.com_id,ins.ins_number quantity,ins.price as price,ins.unit_no unitage,ins.shop as manufactory,xml_sh.out_code,xml_sh.out_name,xml_sh.receivecode,(select a.prod_shelf from gy_dm_mcxx a, gy_dm_gjsc b where ins.shop = b.gjscdm and b.mcdm = a.mcdm) prod_shelf_out_from,(select e.emp_id from employee e where e.emp_name = ins.audi_er) as personcode,ins.bill_no,ins.price enter_price from xml_sh_ins_out_detail xml_sh, installation ins, ins_product_list ins_prod where ins.prod_id = ins_prod.prod_id and xml_sh.item_id = ins.bill_no and xml_sh.createflag = '0') A WHERE ROWNUM <= 100) WHERE RN >= 0";
        ResultSet rs2 = queryClass.scrollQuery(sql);
        if (inputMerchandise2XML(rs2))
        {
          TransformerFactory tFactory = TransformerFactory.newInstance();
          Transformer transformer = tFactory.newTransformer();
          transformer.setOutputProperty("encoding", "utf-8");
          DOMSource source = new DOMSource(doc);
          StreamResult result = new StreamResult(fname);
          transformer.transform(source, result);
          saveFlag = true;
          //System.out.print("\n 时间：" + new Date().toLocaleString() + "安装机出库 结果：写入成功");
        }
//        rs2.close();
//        rs2 = null;
      }
      rs.close();
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
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
      e.printStackTrace();
    }

    if (saveFlag)
    {
      for (int i = 0; i < idList.size(); i++)
      {
        Map m = (Map)idList.get(i);
        sql = "update xml_diaobo set createflag='1' where item_id='" + 
          m.get("item_id") + 
          "'";

        queryClass.execute(sql);
      }
    }

    rs = null;
    queryClass.close();
    outputFinishFlag = true;
  }

  public static boolean getFinishFlag() {
    return finishFlag;
  }

  public static void setFinishFlag(boolean flg) {
    finishFlag = flg;
  }

  public static String getFile(String dir)
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
    isExistsXML = false;
    file = new File(fn);
    if (file.exists())
    {
      isExistsXML = true;
      return fn;
    }
    return fn;
  }

  protected static String mappingCode(String src)
  {
    List lst = Constant.BASE_MESSAGE_MAPPING;
    if (lst == null)
      return src;
    if (src == null)
      return "";
    src = src.trim();
    for (int i = 0; i < lst.size(); i++)
    {
      Map codeMap = (Map)lst.get(i);
      if (codeMap.get("title").toString().equals("入库类型"))
        continue;
      if (codeMap.get("e3_code").toString().equals(src))
        return codeMap.get("u8_code").toString();
    }
    return src;
  }

  public static boolean getOutputFinishFlag() {
    return outputFinishFlag;
  }

  public static void setOutputFinishFlag(boolean flg) {
    outputFinishFlag = flg;
  }

  public static boolean getExistsXML() {
    return isExistsXML;
  }
}