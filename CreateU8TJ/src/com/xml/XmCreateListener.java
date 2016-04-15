package com.xml;

import com.xml.jdbc.Execute;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class XmCreateListener extends HttpServlet {
	
	private static final long serialVersionUID = 339076055618142680L;
	ServletContext application;

	public void init() throws ServletException {
		String xmlPath = "";
		super.init();
		this.application = getServletContext();
		Constant.SERVLET_PATH = this.application.getRealPath("/");
		Constant.DATA_PATH = getInitParameter("dataconfig");
		Constant.TREAD_TIME_OUT = Integer
				.parseInt(getInitParameter("tread_time_out"));
		xmlPath = getInitParameter("store_in_out_xml");
		Constant.ISAUTO_CREATE_XML = getInitParameter("isAuto");
		xmlPath = Constant.SERVLET_PATH + xmlPath;
		xmlPath = xmlPath.replaceAll("\\\\", "/");
		xmlPath = xmlPath.replaceAll("//", "/");
		Constant.STORE_IN_OUT_XML = xmlPath;

		Constant.BASE_MESSAGE_MAPPING = new Execute().getBaseMapping();
		Constant.initConfig();
		if (Constant.ISAUTO_CREATE_XML.trim().equals("1")) {
			new XmlThread().start();
			new XmlreturnThread().start();
			new OutputXmlThread().start();
			new SellputXmlThread().start();
			new SellreturnXmlThread().start();
			new ReoutputXmlThread().start();
			new ResellputXmlThread().start();
			new ResellreturnXmlThread().start();
			new RecreateInputXmlThread().start();
			new RecreateInputreturnXmlThread().start();
			new DiaoboXMLTread().start();
			new RecreateDiaoboXMLThread().start();
			//new MsgThread().start();
		}
		new ConfigFileListener().start();
	}
}