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

public class MerchandiseReserveAction extends Action
{
  Merchandise mcd = new Merchandise();
  JdbcTemplet templet = new JdbcTemplet();
  HttpSession session;
  String sql;
  String act;

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
    if (this.act.equals("qry"))
    {
      this.mcd.setPid(StringUtil.stringFilter(request.getParameter("pid")));
      this.sql = 
        ("select prod.prod_name,st.storage,(select shelf.shelf_name from shelf_lib shelf where shelf.prod_shelf=st.prod_shelf ) as shelf_name from sh_jl_storage st,ins_product_list prod where prod.prod_id=st.prod_id and st.prod_id='" + 
        this.mcd.getPid() + "'");
      request.setAttribute("result", this.templet.query(this.sql));
      return mapping.findForward("show");
    }
    return mapping.getInputForward();
  }
}