package com.mnn.society.server.bamul.utils;

public class OpSpec {
	private int opCode;
	private int paramCount;
	
	public OpSpec(int opCode, int paramCount) {
		this.opCode = opCode;
		this.paramCount = paramCount;
	}

	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	public int getParamCount() {
		return paramCount;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}
}