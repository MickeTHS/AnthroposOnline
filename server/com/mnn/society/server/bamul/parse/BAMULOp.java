package com.mnn.society.server.bamul.parse;

public class BAMULOp {
	private int opCode;
	private String param1;
	private String param2;

	public BAMULOp(int opCode, String param1, String param2) {
		this.opCode = opCode;
		this.param1 = param1;
		this.param2 = param2;
	}
	
	public int getOpCode() {
		return opCode;
	}
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
}
