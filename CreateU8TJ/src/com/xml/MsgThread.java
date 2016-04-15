package com.xml;

public class MsgThread extends Thread {
	MsgXML msgInput = new MsgXML();

	public void run() {
		System.out.print("\n 开启短信接收线程监听程序 ");
		creatEnventListener();
	}

	private void creatEnventListener() {
		this.msgInput.setFinishFlag(true);
		try {
			while (true) {
				Thread.sleep(Constant.TREAD_TIME_OUT);
				if (this.msgInput == null) {
					this.msgInput = new MsgXML();
					this.msgInput.setFinishFlag(true);
				}

				if (!this.msgInput.getFinishFlag())
					continue;
				this.msgInput.inputMerchandise2XML();
			}
		} catch (InterruptedException localInterruptedException) {
		}
	}
}