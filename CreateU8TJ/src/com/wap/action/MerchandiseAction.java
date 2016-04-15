package com.wap.action;

import com.entity.Merchandise;
import com.jdbc.JdbcTemplet;
import com.util.DateFormat;
import com.util.Encode;
import com.util.StringUtil;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class MerchandiseAction extends Action
{
  HttpSession session;
  Merchandise mcd = new Merchandise();
  JdbcTemplet templet = new JdbcTemplet();
  String returnString = "";
  String act = ""; String sql = "";
  final String chartset = "UTF-8";
  final int pageCount = 5;

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    this.session = request.getSession(false);
    if ((this.session == null) || (this.session.getAttribute("userId") == null))
    {
      request.setAttribute("msg", "登录超时");
      return mapping.findForward("logout");
    }
    this.act = StringUtil.stringFilter(request.getParameter("act"));
    if (this.act.equals("add"))
    {
      return add(mapping, form, request, response);
    }
    if (this.act.equals("update"))
      return update(mapping, form, request, response);
    if (this.act.equals("view"))
    {
      return get(mapping, form, request, response);
    }
    if (this.act.equals("lst"))
    {
      return list(mapping, form, request, response);
    }
    return mapping.getInputForward();
  }

  protected ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    setMerchandiseValue(request);
    this.returnString = checkMerchandiseValue(request);
    if (this.returnString.equals("0"))
    {
      return mapping.getInputForward();
    }

    if (!getProduct(request))
    {
      return mapping.getInputForward();
    }

    getSalerRelation();
    if (this.templet.executeUpdate(createInsertSQL()))
    {
      request.setAttribute("msg", "添加成功");
    }
    else
      request.setAttribute("msg", "添加失败，请稍候再试");
    return mapping.getInputForward();
  }

  protected ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    setMerchandiseValue(request);
    this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
    this.returnString = checkMerchandiseValue(request);
    if (this.returnString.equals("0"))
    {
      return mapping.getInputForward();
    }
    if (this.templet.executeUpdate(createUpdateSQL()))
    {
      request.setAttribute("msg", "更新成功");
    }
    else
      request.setAttribute("msg", "更新失败，请稍候再试");
    return mapping.getInputForward();
  }

  protected ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
    this.mcd.setClient(StringUtil.stringFilter(Encode.Unicode2Chartset(request.getParameter("client"), "UTF-8")));
    this.mcd.setTel(StringUtil.stringFilter(request.getParameter("tel")));
    this.mcd.setProduct(StringUtil.stringFilter(request.getParameter("product")));

    if (!this.mcd.getPid().equals(""))
    {
      this.sql = ("select i.*,(select gy.azfsmc  from gy_dm_azfs gy where gy.azfsdm=i.ins_status) as azfsmc from installation i where i.bill_no='" + this.mcd.getPid() + "'");
    }
    else if (!this.mcd.getProduct().equals(""))
    {
      this.sql = ("select i.*,(select gy.azfsmc  from gy_dm_azfs gy where gy.azfsdm=i.ins_status) as azfsmc from installation i where i.prod_id='" + this.mcd.getProduct() + "'");
    }
    else if ((!this.mcd.getClient().equals("")) || (!this.mcd.getTel().equals("")))
    {
      this.sql = "select i.*,(select gy.azfsmc  from gy_dm_azfs gy where gy.azfsdm=i.ins_status) as azfsmc from installation where 1=1 ";
      if (!this.mcd.getClient().equals(""))
        this.sql = (this.sql + " and cus_name='" + this.mcd.getClient() + "' ");
      if (!this.mcd.getTel().equals(""))
        this.sql = (this.sql + " and cus_phone='" + this.mcd.getTel() + "'");
    }
    else {
      return mapping.getInputForward();
    }request.setAttribute("result", this.templet.query(this.sql));
    return mapping.findForward("view");
  }
  protected ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    String page = StringUtil.trimNull(request.getParameter("page"));
    String tp = StringUtil.trimNull(request.getParameter("tp"));
    String cntSql = "";
    int p;
    try {
      p = Integer.parseInt(page);
    }
    catch (NumberFormatException nfx)
    {
      p = 1;
    }
    if (tp.equals(""))
    {
      this.sql = 
        ("select * from (select a.*, rownum rn from (select * from installation) a where rownum <= " + 
        p * 5 + ")where rn >" + (p - 1) * 5);
    }
    cntSql = "select count(*) from installation";
    int cnt = this.templet.getRecordCount(cntSql);
    int totalPage = (cnt - 1) / 5 + 1;
    request.setAttribute("index", String.valueOf((p - 1) * 5));
    if (p < totalPage)
      request.setAttribute("nextPage", String.valueOf(p + 1));
    if (p > 1)
      request.setAttribute("prePage", String.valueOf(p - 1));
    request.setAttribute("result", this.templet.query(this.sql));
    return mapping.findForward("query");
  }

  protected ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
    this.sql = ("delete from testwap where pid='" + this.mcd.getPid() + "'");
    if (this.templet.executeUpdate(this.sql))
    {
      request.setAttribute("msg", "删除成功");
    }
    else
      request.setAttribute("msg", "删除失败，请稍候再试");
    return mapping.getInputForward();
  }

  private void setMerchandiseValue(HttpServletRequest request)
  {
    this.mcd.setClient(StringUtil.stringFilter(Encode.Unicode2Chartset(
      request.getParameter("client"), "UTF-8")));
    this.mcd
      .setMovebile(StringUtil.stringFilter(request
      .getParameter("movebile")));
    this.mcd.setTel(StringUtil.stringFilter(request.getParameter("tel")));
    this.mcd.setPrice(StringUtil.stringFilter(request.getParameter("price")));
    this.mcd.setProduct(StringUtil.stringFilter(request.getParameter("product")));
    this.mcd.setHandTime(DateFormat.formatDate(request.getParameter("handTime")));
    this.mcd.setAddress(StringUtil.stringFilter(Encode.Unicode2Chartset(
      request.getParameter("address"), "UTF-8")));
    this.mcd.setBuyDate(DateFormat.getCurrentDate());
  }

  private String checkMerchandiseValue(HttpServletRequest request)
  {
    String msg = "";
    if (this.mcd.getProduct().equals(""))
      msg = msg + "<br/>产品不能为空";
    try
    {
      this.mcd.setCount(Integer.parseInt(request.getParameter("quantity")));
    }
    catch (NumberFormatException ne)
    {
      msg = msg + "<br/>数量不正确";
    }
    if (this.mcd.getPrice().equals(""))
      msg = msg + "<br/>价格不能为空";
    if (this.mcd.getClient().equals(""))
      msg = msg + "<br/>客户不能为空";
    if (this.mcd.getHandTime().equals(""))
      msg = msg + "<br/>时间格式(yyyymmdd)";
    if (!msg.equals(""))
    {
      request.setAttribute("msg", msg);
      return "0";
    }
    return "1";
  }

  private boolean getProduct(HttpServletRequest request)
  {
    this.sql = 
      ("select p.prod_id,p.prod_model from ins_product_list p where p.prod_id='" + 
      this.mcd.getProduct() + "'");
    List lst = this.templet.query(this.sql);
    if ((lst == null) || (lst.size() == 0))
    {
      request.setAttribute("msg", "编号：" + this.mcd.getProduct() + " <br/> 产品不存在");
      return false;
    }
    Map product = (Map)lst.get(0);
    this.mcd.setBrand(product.get("prod_id").toString().substring(0, 4));
    this.mcd.setCategory(product.get("prod_id").toString().substring(4, 6));
    this.mcd.setModel(product.get("prod_model").toString());
    this.mcd.setSaler(this.session.getAttribute("userName").toString());
    this.mcd.setCount(1);
    return true;
  }

  private void getSalerRelation()
  {
    this.sql = 
      ("select *from gy_dm_gjsc where ywy='" + 
      this.session.getAttribute("userId").toString() + "'");
    List lst = this.templet.query(this.sql);
    if ((lst == null) || (lst.size() == 0))
    {
      this.mcd.setShopRegion("");
      this.mcd.setSalePlace("");
      this.mcd.setBuyPlace("");
    }
    else
    {
      Map shop = (Map)lst.get(0);
      this.mcd.setShopRegion(shop.get("qydm").toString());
      this.mcd.setSalePlace(shop.get("mcdm").toString());
      this.mcd.setBuyPlace(shop.get("gjscdm").toString());
    }
  }

  private String createInsertSQL() {
    this.sql = 
      ("insert into installation(bill_no,cardtype,brand_id,product,shop  ,shop_date,ask_date,ask_content,cus_name,cus_phone  ,address,zipcode,operator,od_date,od_com  ,out_serv,out_com,memo_base,card_no,acc_status,price,payoff,passer,truck,ins_type  ,ins_number,hole_date,ins_method,ins_status,out_flag,com_id,prod_id,qydm,qydmparent,time_type,fanxian,cxydm,jzl_flag,fee_type,fee_num,fldm,sell_id) values (GET_NEXTBILLNO('STNO'),'1','" + 
      this.mcd.getBrand() + "','" + 
      this.mcd.getModel() + "'," + 
      "'0001' ," + 
      "to_date('" + this.mcd.getBuyDate() + "','YYYY-MM-DD')," + 
      "to_date('" + this.mcd.getHandTime() + "','YYYY-MM-DD')," + 
      "'','" + 
      this.mcd.getClient() + "','" + 
      this.mcd.getTel() + "' ,'" + 
      this.mcd.getAddress() + "'," + 
      "'','" + 
      this.mcd.getSaler() + "'," + 
      "sysdate," + 
      "'' ," + 
      "'0'," + 
      "''," + 
      "''," + 
      "''," + 
      "0," + 
      this.mcd.getPrice() + "," + 
      "0," + 
      "''," + 
      "0," + 
      "0 ," + 
      this.mcd.getCount() + "," + 
      "null," + 
      "'1'," + 
      "'1'," + 
      "'0'," + 
      "'" + this.session.getAttribute("comId") + "'," + 
      "'" + this.mcd.getProduct() + "'," + 
      "'QY00005'," + 
      "'QY00004' ," + 
      "'全天'," + 
      "0," + 
      "''," + 
      "'0'," + 
      "''," + 
      "0," + 
      "'" + this.mcd.getCategory() + "','" + 
      this.mcd.getProduct() + "')");
    //System.out.print("\n insrt sql==" + this.sql);
    return this.sql;
  }

  private String createUpdateSQL() {
    this.sql = "update testwap set ";
    return this.sql;
  }
}