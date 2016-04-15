package com.wap.action;

import com.entity.UserInfo;
import com.jdbc.JdbcTemplet;
import com.util.StringUtil;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class LoginAction extends Action
{
  String act = "";
  UserInfo user = new UserInfo();
  JdbcTemplet templet = new JdbcTemplet();
  HttpSession session;

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
  {
    this.act = StringUtil.trimNull(request.getParameter("act"));
    if (this.act.equals("login"))
    {
      this.user.setUserName(StringUtil.stringFilter(request.getParameter("userName")));
      this.user.setPasswd(StringUtil.stringFilter(request.getParameter("passwd")));
      String sql = "select *from employee e where e.emp_id='" + this.user.getUserName() + "' and e.emp_pass='" + this.user.getPasswd() + "'";
      List lst = this.templet.query(sql);
      if ((lst == null) || (lst.size() == 0))
      {
        request.setAttribute("msg", "用户名或密码错误");
        return mapping.getInputForward();
      }

      this.session = request.getSession();
      Map userMap = (Map)lst.get(0);
      this.session.setAttribute("userId", userMap.get("emp_id"));
      this.session.setAttribute("userName", userMap.get("emp_name"));
      this.session.setAttribute("comId", userMap.get("emp_com"));

      return mapping.findForward("menu");
    }
    return mapping.getInputForward();
  }
}