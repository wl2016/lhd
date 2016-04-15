package com.wap.action;

import com.entity.Merchandise;
import com.jdbc.JdbcTemplet;
import com.util.StringUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ConfirmAction extends Action
{
  Merchandise mcd = new Merchandise();
  JdbcTemplet templet = new JdbcTemplet();
  String act = "";
  String sql = "";
  HttpSession session;

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0L);
    this.session = request.getSession(false);
    if ((this.session == null) || (this.session.getAttribute("userId") == null))
    {
      request.setAttribute("msg", "登录超时");
      return mapping.findForward("logout");
    }
    this.act = StringUtil.trimNull(request.getParameter("act"));
    if (this.act.equals("qry"))
      return get(mapping, form, request, response);
    if (this.act.equals("cfm"))
    {
      return confirm(mapping, form, request, response);
    }
    return mapping.getInputForward();
  }

  public ActionForward confirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
    String type = StringUtil.trimNull(request.getParameter("tp"));
    String confirmCode = "";
    confirmCode = StringUtil.trimNull(request.getParameter("confirm_code"));
    if ((confirmCode.equals("")) || (!this.templet.checkExists("select *from CONFIRM_CODE where CODE_ID='" + confirmCode + "' and ISAVAILABLE='1' and install_id='" + this.mcd.getPid() + "'")))
    {
      request.setAttribute("msg", "认证码错误");
      return mapping.findForward("alert");
    }
    if (this.templet.checkExists("select BILL_NO from installation where INS_STATUS='1' and BILL_NO='" + this.mcd.getPid() + "'"))
    {
      request.setAttribute("msg", "该安装单已经安装");
      return mapping.findForward("alert");
    }

    if (type.equals("out"))
    {
      this.sql = ("select BILL_NO from installation where RUHUDATE1 is null and BILL_NO='" + this.mcd.getPid() + "'");
      if (this.templet.checkExists(this.sql))
      {
        request.setAttribute("msg", "请先确认入户");
        return mapping.findForward("alert");
      }
      this.sql = ("update installation set CHUHUDATE1=sysdate where BILL_NO='" + this.mcd.getPid() + "'");
    }
    else if (type.equals("con"))
    {
      this.sql = ("update installation set CONNECT_CUSTOM_DATE=sysdate where BILL_NO='" + this.mcd.getPid() + "'");
    }
    else
    {
      this.sql = ("select BILL_NO from installation where CONNECT_CUSTOM_DATE is null and BILL_NO='" + this.mcd.getPid() + "'");
      if (this.templet.checkExists(this.sql))
      {
        request.setAttribute("msg", "请先联系客户");
        return mapping.findForward("alert");
      }
      this.sql = ("update installation set RUHUDATE1=sysdate where BILL_NO='" + this.mcd.getPid() + "'");
    }
    if (this.templet.executeUpdate(this.sql))
    {
      if (type.equals("out"))
      {
        this.templet.execute("update CONFIRM_CODE set ISAVAILABLE='0' where ISAVAILABLE='1' and CODE_ID='" + confirmCode + "' and  install_id='" + this.mcd.getPid() + "'");
      }
      request.setAttribute("msg", "操作成功");
    }
    else
    {
      request.setAttribute("msg", "操作失败,请稍后再试");
    }
    return mapping.findForward("alert");
  }

  public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
    this.mcd.setTel(StringUtil.stringFilter(request.getParameter("tel")));
    String type = StringUtil.stringFilter(request.getParameter("tp"));
    this.sql = "select i.*,(select gy.azfsmc  from gy_dm_azfs gy where gy.azfsdm=i.ins_status) as azfsmc  from installation i where 1=1 ";
    if ((this.mcd.getPid().equals("")) && (this.mcd.getTel().equals("")))
    {
      request.setAttribute("msg", "请输入查询关键字");
      return mapping.getInputForward();
    }
    if (!this.mcd.getPid().equals(""))
    {
      this.sql = (this.sql + " and i.bill_no='" + this.mcd.getPid() + "'");
    }
    if (!this.mcd.getTel().equals(""))
    {
      this.sql = (this.sql + " and i.cus_phone='" + this.mcd.getTel() + "'");
    }
    request.setAttribute("result", this.templet.query(this.sql));
    if (type.equals("out"))
    {
      return mapping.findForward("out_confirm");
    }
    return mapping.findForward("success");
  }
}