package com.mnn.society.server.bamul.parse;

import java.util.ArrayList;
import java.util.List;

public class BAMUL {
	List<BAMULOp> opList;

	public BAMUL() {
		opList = new ArrayList<BAMULOp>();
	}
	
	public void addOp(BAMULOp op) {
		opList.add(op);
	}
	
	public BAMULOp readOp(int position) {
		return opList.get(position);
	}
	
	public int getOpCount() {
		return opList.size();
	}
}
