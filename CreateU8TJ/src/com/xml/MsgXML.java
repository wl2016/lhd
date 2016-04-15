package com.xml;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.b2m.eucp.sdkhttp.Mo;
import com.SingletonClient;
import com.xml.jdbc.Execute;

public class MsgXML
{
  private boolean finishFlag;
//  private boolean outputFinishFlag;
  
//  Element nodeElement;
//  Element bodyElement;
//  Element entryElement;
//  Element storeinoutElement;
//  Element headerElement;
//  Element ufinterfaceElement;
  
//  static String fname = "";
//  boolean sucessFlag = false;
//  boolean saveFlag = false;

  Execute queryClass = new Execute();

//  String tempSQL = "";

  private void finishReset(){
    this.finishFlag = true;
//    this.outputFinishFlag = true;
//    this.sucessFlag = false;
//    this.saveFlag = false;
  }

  public synchronized void inputMerchandise2XML(){
    this.finishFlag = false;
    
	try {
//		System.out.println("短信接收开始……");
		List<Mo> list = SingletonClient.getClient().getMO();
		if (list != null) {
//			System.out.println("testGetMO1size:" + list.size());
			String sql = "";
			for(Mo mo : list) {
//				System.out.print("短信内容:" + mo.getSmsContent());
//				System.out.println("短信内容:" + (mo.getSmsContent().length()==6));
//				System.out.println("通道号:" + mo.getChannelnumber());
//				System.out.println("   手机号:" + mo.getMobileNumber());
//				System.out.println("附加码:" + mo.getAddSerialRev());
//				System.out.println("附加码:" + mo.getAddSerial());
					
				if(mo.getSmsContent()!=null && !"".equals(mo.getSmsContent()) ){
					//卖场安装短信回复----------------------------------------
					if(mo.getSmsContent().toUpperCase().startsWith("AZ")){
						String inst_man = mo.getMobileNumber(),azddm = "",rydm = "";
						sql = "select rydm,rymc,azddm from gy_dm_azdry where zt = '1' and dh ='"+mo.getMobileNumber()+"' and rownum =1 ";
						if(queryClass.checkExists(sql)){
							//获得对应的工人师傅名称
							ResultSet rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								inst_man = rs.getString("rymc");
								rydm = rs.getString("rydm");
								azddm = rs.getString("azddm");
							}
							rs.close();
						}else{
//							SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】未登记的手机号码，回复失败，请联系菱汇达张敬倩（13811109830）核实手机号码","",5);
							break;
						}
						
						sql = "select cus_phone from installation where ins_status = '20' and out_com = '"+azddm+"' and bill_no = '"+mo.getSmsContent().toUpperCase()+"' ";
						if(queryClass.checkExists(sql)){
							String cus_phone = "";
							ResultSet rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								cus_phone = rs.getString("cus_phone");
								if(cus_phone.length() >= 11){
									cus_phone = cus_phone.substring(0,11);
								}	 								
							}
							rs.close();
							if(!"".equals(cus_phone)) SingletonClient.getClient().sendSMS(new String[] {cus_phone}, "【三菱电机-菱汇达公司】您所购三菱电机空调已安装完毕，请您对安装服务进行评价。请回复数字：1、满意；2、一般；3、不满意","",5);
							
							sql = "select b.cost from basic_cost b,ins_product_list d,gy_dm_gjsc s,installation f where f.prod_id = d.prod_id and b.cost_type = 4 and ";
							sql += " d.prod_style = b.machine_type and b.azddm = f.out_com and f.shop = s.gjscdm and b.sell_sys = s.mcdm and b.qydm = f.qydm ";
							sql += " and f.bill_no ='"+mo.getSmsContent().toUpperCase()+"'";
							//System.out.println("卖场安装信息费："+sql);
							String cost="0.00";
							rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								cost = rs.getString("cost");
							}
							rs.close();
							
							//设置安装单为已完成状态
							sql = "update installation set ins_status = '27',inst_date=sysdate,fin_time=sysdate,manage_total_azd="+cost+",inst_man = '"+inst_man+"' where bill_no = '"+mo.getSmsContent().toUpperCase()+"'";
							queryClass.execute(sql);
							
							try{
								sql = "insert into SH_JL_AZRY (bill_no,Emp_No,Emp_Name,pay_out,Sent_Date) values ('"+mo.getSmsContent().toUpperCase()+"','"+rydm+"','"+inst_man+"','',sysdate)";
								queryClass.execute(sql);
							}catch(Exception t){
								t.printStackTrace();
							}							
							
							sql="insert into ins_service_detail(bill_no,question_no,question_answer,question_other) "+" values('"+mo.getSmsContent().toUpperCase()+"'"+",'HF000000'"+",'80'"+",'')";
							queryClass.execute(sql);
							
							sql="insert into ins_track(bill_no,act_program,operator,act_time,act_matter) ";
							sql+= "values('"+mo.getSmsContent().toUpperCase()+"','卖场安装完成短信回复','"+inst_man+"',sysdate,'工人师傅手机回复内容:"+mo.getSmsContent().toUpperCase()+"')";
							queryClass.execute(sql);
						}else{
							SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】未找到卖场安装出库的匹配信息，请重新输入","",5);
							break;
						}
					//制冷店安装短信回复--------------------------------------
					}else if(mo.getSmsContent().toUpperCase().startsWith("SENO")){
						String inst_man = mo.getMobileNumber(),azddm = "";
						sql = "select rymc,azddm from gy_dm_azdry where zt = '1' and dh ='"+mo.getMobileNumber()+"' and rownum =1 ";
						if(!queryClass.checkExists(sql)){
//							SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】未登记的手机号码，回复失败，请联系菱汇达张敬倩（13811109830）核实手机号码","",5);
							break;
						}else{
							//获得对应的工人师傅名称
							ResultSet rs = queryClass.scrollQuery(sql);
							while(rs.next()){
								inst_man = rs.getString("rymc");
								azddm = rs.getString("azddm");
							}
							rs.close();							
						}
						sql = "select customer_phone from sell_out where nvl(ins_status,'0') not in ( '27','28') and out_com = '"+azddm+"' and sell_id = '"+mo.getSmsContent().toUpperCase()+"'";
						//System.out.println("制冷店---  ："+sql);
						if(queryClass.checkExists(sql)){
//							String cus_phone = "";
//							ResultSet rs = queryClass.scrollQuery(sql);
//							while(rs.next()){
//								cus_phone = rs.getString("customer_phone");
//							}
//							rs.close();
//							if(!"".equals(cus_phone)) SingletonClient.getClient().sendSMS(new String[] {cus_phone}, "【三菱电机-菱汇达公司】您所购三菱电机空调已安装完毕，请您对安装服务进行评价。回复：1、满意；2、一般；3、不满意。","",5);
							
							sql = "select b.cost from basic_cost b,ins_product_list d,sell_out so,prod_out_detail pod  ";
							sql += " where pod.item_id = so.item_id and pod.prod_id = d.prod_id and b.cost_type = 4 and d.prod_style = b.machine_type ";
							sql += " and b.azddm = so.out_com and b.sell_sys = so.mer_code and b.qydm = so.qydm ";
							sql += " and so.sell_id ='"+mo.getSmsContent().toUpperCase()+"'";
							//System.out.println("制冷店安装信息费："+sql);
							String cost="0.00";
							ResultSet rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								cost = rs.getString("cost");
							}
							rs.close();							
							
							//设置销售单为已完成状态 27表示已完成
							sql = "update sell_out set ins_status = '27',inst_date=sysdate,manage_total_azd = "+cost+" where sell_id = '"+mo.getSmsContent().toUpperCase()+"'";
							queryClass.execute(sql);
							
							sql="insert into act_track(bill_no,act_program,operator,act_time,act_matter) ";
							sql+= "values('"+mo.getSmsContent().toUpperCase()+"','销售安装完成短信回复','"+inst_man+"',sysdate,'工人师傅手机回复内容:"+mo.getSmsContent().toUpperCase()+"')";
							queryClass.execute(sql);
							
						}else{
							SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】未找到制冷店出库的匹配信息，请重新输入","",5);
							break;
						}
					//物流配送信息
					}else if(mo.getSmsContent().length()==6){
						sql = "select rymc from trans_company_wldry where zt = '1' and dh ='"+mo.getMobileNumber()+"' and rownum =1 ";
						String inst_man = mo.getMobileNumber();
						if(!queryClass.checkExists(sql)){
//							SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】未登记的手机号码，回复失败，请联系菱汇达张敬倩（13811109830）核实手机号码","",5);
							break;
						}else{
							//获得对应的工人师傅名称
							ResultSet rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								inst_man = rs.getString("rymc");
							}
							rs.close();								
						}
						sql = "select sell_id,bill_id,bill_no,logistics_status from logistics_schedule where logistics_status >= '2' and logistics_status <> '6' and confirm_code ='"+mo.getSmsContent()+"'";
						if(queryClass.checkExists(sql)){
							String sell_id = "",bill_id = "",bill_no = "",logistics_status = "";
							ResultSet rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								sell_id = rs.getString("sell_id");
								bill_id = rs.getString("bill_id");
								bill_no = rs.getString("bill_no");
								logistics_status = rs.getString("logistics_status");
							}
							rs.close();
							if(!"3".equals(logistics_status)){
								//设置安装单为已配送完成状态
								if(null != sell_id && !"".equals(sell_id)){
									sql = "update sell_out set logis_status = '3' where  item_id = '"+sell_id+"'";
									queryClass.execute(sql);
									
									sql="insert into act_track(bill_no,act_program,operator,act_time,act_matter) ";
									sql+= "values('"+sell_id+"','销售配送完成短信回复','"+inst_man+"',sysdate,'工人师傅手机回复内容:"+mo.getSmsContent().toUpperCase()+"')";
									queryClass.execute(sql);
								}else{
									sql = "update installation set logis_status = '3' where  bill_no = '"+bill_id+"'";
									queryClass.execute(sql);
									
									sql="insert into ins_track(bill_no,act_program,operator,act_time,act_matter) ";
									sql+= "values('"+bill_id+"','卖场配送完成短信回复','"+inst_man+"',sysdate,'工人师傅手机回复内容:"+mo.getSmsContent().toUpperCase()+"')";
									queryClass.execute(sql);								
								}
								
								sql = "select b.cost from logistics_schedule a,basic_cost b,ins_product_list d,sell_out e,installation f";
								sql += " where a.product_id = d.prod_id and a.sell_id = e.item_id(+) and a.bill_id = f.bill_no(+) and cost_type = 0 and d.prod_style = b.machine_type and b.trans_id = a.out_com and (b.isinnersix = e.isinnersix or b.isinnersix = f.isinnersix)";
								sql += " and a.confirm_code ='"+mo.getSmsContent()+"'";
								//System.out.println("物流运费："+sql);
								String cost="0.00";
								rs = queryClass.scrollQuery(sql);
								if(rs.next()){
									cost = rs.getString("cost");
								}
								rs.close();
								
								//设置物流排程单为已完成状态
								sql = "update logistics_schedule set cost="+cost+",sms_fillback_date=sysdate,confirm_code='',confirm_code_remark='"+mo.getSmsContent()+"',logistics_status = '3' where logistics_status = '2' and  confirm_code ='"+mo.getSmsContent()+"'";
								queryClass.execute(sql);
								
								sql = "insert into operation_track values(seq_operation_track.nextval,sysdate,'"+inst_man+"','配送短信回执','"+bill_no+"','"+inst_man+"回复短信:"+mo.getSmsContent()+"')";
								queryClass.execute(sql);
								
								SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】系统接收确认码 "+mo.getSmsContent()+"成功","",5);								
							}else{
								SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】该物流单已经配送完成，请勿重复发送"+mo.getSmsContent(),"",5);
								break;								
							}
						}else{
							SingletonClient.getClient().sendSMS(new String[] {mo.getMobileNumber()}, "【三菱电机-菱汇达公司】未找到匹配信息，或该货物还未做系统出库审核，请核实后重新输入","",5);
							break;
						}
					//此处满意度调查待填写
					}else{
						sql = "select bill_no,fin_time,cus_phone from installation where ins_status = '27' and cus_phone like '%"+mo.getMobileNumber()+"%' and rownum = 1 order by fin_time desc";
						if(queryClass.checkExists(sql)){
							String bill_no = "",cus_phone = "";Date fin_time = null;
							ResultSet rs = queryClass.scrollQuery(sql);
							if(rs.next()){
								bill_no = rs.getString("bill_no");
								cus_phone = rs.getString("cus_phone");
								fin_time = rs.getDate("fin_time");
							}
							rs.close();
							//设置安装单满意度
							if(null != bill_no && !"".equals(bill_no)){
								String bill = "";
								if(mo.getSmsContent().equals("3") || mo.getSmsContent().equals("不满意") || mo.getSmsContent().indexOf("不满意")>0){
									bill = "40";
								}else if(mo.getSmsContent().equals("1") || mo.getSmsContent().equals("满意") || mo.getSmsContent().indexOf("满意")>0){
									bill = "80";
								}else if(mo.getSmsContent().equals("2") || mo.getSmsContent().equals("一般") || mo.getSmsContent().indexOf("一般")>0){
									bill = "60";
								}else {
									bill = "80";
								}
//								sql="insert into ins_service_detail(bill_no,question_no,question_answer,question_other) "+" values('"+bill_no+"'"+",'HF000000'"+",'"+bill+"'"+",'')";
								sql="update ins_service_detail set question_answer = '"+bill+"'  where bill_no = '"+bill_no+"' and question_no = 'HF000000'";
								queryClass.execute(sql);
								
								SimpleDateFormat sdfDate= new SimpleDateFormat("yyyy-MM-dd");
								sql="update installation set huifu_status = '1'  where bill_no = '"+bill_no+"'";
								if(fin_time!=null)sql="update installation set huifu_status = '1'  where ins_status = '27' and cus_phone = '"+cus_phone+"' and to_char(fin_time,'yyyy-MM-dd') = '"+sdfDate.format(fin_time)+"'";
								queryClass.execute(sql);								
							}
						}						
					}
				}
				// 上行短信务必要保存,加入业务逻辑代码,如：保存数据库，写文件等等
				sql = "insert into sms_receive(id,phone,text,mdate,pdu,content,create_date)"+
						  " values(sms_receive_id.nextval,'"+mo.getMobileNumber()+"','"+mo.getSmsContent()+"',sysdate,'"+mo.getAddSerialRev()+"','"+mo.getAddSerial()+"',sysdate)";
				queryClass.execute(sql);				
			}
		} else {
//			System.out.println("NO HAVE MO");
		}
	} catch (Exception e) {
		e.printStackTrace();
		queryClass.close();
	}
//	System.out.println("短信接收结束……");
    this.queryClass.close();
    finishReset();
  }
  
  public boolean getFinishFlag(){
    return this.finishFlag;
  }

  public void setFinishFlag(boolean finishFlag) {
    this.finishFlag = finishFlag;
  }

//  public boolean getOutputFinishFlag() {
//    return this.outputFinishFlag;
//  }
}