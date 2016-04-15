package com.wap.action;

import com.entity.Merchandise;
import com.jdbc.JdbcTemplet;
import com.util.Encode;
import com.util.StringUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class InstallionAction extends Action
{
  Merchandise mcd = new Merchandise();
  JdbcTemplet templet = new JdbcTemplet();
  HttpSession session;
  String sql;
  String act;
  String chartset = "UTF-8";

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    this.session = request.getSession(false);
    if ((this.session == null) || (this.session.getAttribute("userId") == null))
    {
      request.setAttribute("msg", "登录超时");
      return mapping.findForward("logout");
    }
    this.act = StringUtil.trimNull(request.getParameter("act"));
    this.mcd.setClient(Encode.Unicode2Chartset(StringUtil.stringFilter(request.getParameter("client")), this.chartset));
    this.mcd.setTel(StringUtil.stringFilter(request.getParameter("tel")));
    this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
    if (this.act.equals("qry"))
    {
      this.sql = "select i.*,(select gy.azfsmc  from gy_dm_azfs gy where gy.azfsdm=i.ins_status) as azfsmc from installation i where 1=1 ";
      if (!this.mcd.getClient().equals(""))
      {
        this.sql = (this.sql + " and i.cus_name='" + this.mcd.getClient() + "'");
      }
      if (!this.mcd.getTel().equals(""))
      {
        this.sql = (this.sql + " and i.cus_phone='" + this.mcd.getTel() + "'");
      }
      if (!this.mcd.getPid().equals(""))
      {
        this.sql = (this.sql + " and i.bill_no='" + this.mcd.getPid() + "'");
      }
      if ((this.mcd.getClient().equals("")) && (this.mcd.getTel().equals("")) && (this.mcd.getPid().equals("")))
      {
        request.setAttribute("msg", "请输入查询条件");
        return mapping.getInputForward();
      }
      request.setAttribute("result", this.templet.query(this.sql));
      return mapping.findForward("show");
    }
    return mapping.getInputForward();
  }
}