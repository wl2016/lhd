package com.phone;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class PhoneAction extends Action
{
  String str = ""; String phone = ""; String act = ""; String msg = "";
  PrintWriter out;
  HttpSession session;
  public String CONTENT_TYPE_TEXT = "text/html;charset=UTF-8";

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    throws IOException
  {
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0L);
    this.session = request.getSession();

    this.act = request.getParameter("act");
    this.phone = request.getParameter("phone");
    this.msg = request.getParameter("msg");
    String channel;
    if (this.session.getAttribute("channel") == null)
      channel = "";
    else
      channel = this.session.getAttribute("channel").toString();
    response.setContentType(this.CONTENT_TYPE_TEXT);
    this.out = response.getWriter();
    if (this.act == null)
      this.act = "";
    if (this.phone == null)
      this.phone = "";
    if (this.msg == null) {
      this.msg = "";
    }

    if (this.act.equals("1"))
    {
      String[] splitChannel = this.msg.split(",");
      int channelNo = Integer.parseInt(splitChannel[1]);
      int strLen = splitChannel[0].length() + 4;
      System.out.print("\n 数据长度" + this.str + "\n");
      PhoneSocket.autoDial.put("autoDial" + channelNo, "1");
      PhoneSocket.phone.put("phone" + channelNo, splitChannel[0]);
      PhoneSocket.writeData(strLen, 32770, channelNo, splitChannel[0]);
    }
    else if (this.act.equals("0"))
    {
      int strLen = this.msg.length() + 4;

      String[] splitChannel = this.msg.split(",");
      int channelNo = Integer.parseInt(splitChannel[1]);
      PhoneSocket.autoDial.put("autoDial" + channelNo, "0");
      PhoneSocket.phone.put("phone" + channelNo, splitChannel[0]);
      PhoneSocket.writeData(strLen, 32770, channelNo, this.msg);
    }
    this.str = "";
    this.str = PhoneSocket.getReadString(channel);
    this.out.print(this.str);
    return null;
  }
}