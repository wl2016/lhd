package com.wap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;

public class ActionServlet extends Action
{
  HttpSession session;

  public String add(HttpServletRequest request)
  {
    this.session = request.getSession();
    if ((this.session == null) || (this.session.getAttribute("userid") == null))
    {
      request.setAttribute("msg", "登录超时或您未登录，请重新登录");
    }
    return "";
  }
}