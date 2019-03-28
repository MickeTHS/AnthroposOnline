package com.mnn.society.server.bamul.cpu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.mnn.society.server.bamul.cpu.io.PipeController;
import com.mnn.society.server.bamul.parse.BAMUL;
import com.mnn.society.server.bamul.parse.BAMULOp;
import com.mnn.society.server.bamul.utils.BAMULOps;
import com.mnn.society.server.bamul.utils.HardwareInterface;

public class Core {
	/**
	 * Defines the maximum end of the valid range for register values.
	 * The core can only operate within the range of positive integers limited by this value.
	 */
	private static final int REGISTER_MAX_VALUE = 1024;
	
	private static final int OPERATOR_FAULT_INVALID_JUMP = -1;
	
	private static final int HARDWARE_CONNECT_FAILURE_ALREADY_CONNECTED = -1;
	private static final int HARDWARE_DISCONNECT_FAILURE_NOT_CONNECTED = -2;
	
	private BAMUL script;
	private Map<String, Integer> labelMap;
	
	private int x, y, i, j, k, l, pc, t;
	private int opCount;
	private int coreId;
	private boolean eof;
	
	private static int coreCount = 0;
	
	private HardwareInterface hwi;
	private PipeController pipeController;
	
	private Core() {
		
	}
	
	private Core(int coreId) {
		this.coreId = coreId;
		this.labelMap = new HashMap<String, Integer>();
		this.pipeController = new PipeController();
	}
	
	public static Core newCoreInstance() {
		return new Core(coreCount++);
	}
	
	public void reset() {
		this.script = null;
		this.x = 0;
		this.y = 0;
		this.i = 0;
		this.j = 0;
		this.k = 0;
		this.l = 0;
		this.pc = 0;
		this.t = 0;
		this.hwi = null;
	}
	
	public void loadScript(BAMUL script) {
		this.script = script;
		mapLabels();
	}
	
	public int connectHardwareInterface(HardwareInterface hwi) {
		if(this.hwi != null)
			return HARDWARE_CONNECT_FAILURE_ALREADY_CONNECTED;
		this.hwi = hwi;
		return 0;
	}
	
	public int disconnectHardwareInterface() {
		if(this.hwi == null)
			return HARDWARE_DISCONNECT_FAILURE_NOT_CONNECTED;
		this.hwi = null;
		return 0;
	}
	
	public int executeOp() throws IndexOutOfBoundsException {
		if(eof)
			throw new IndexOutOfBoundsException("Core has reached end of script");
		
		BAMULOp op = script.readOp(pc);
		int ret = 0;
		
		// Op codes 0-32 are reserved for Core operations
		if(op.getOpCode() < 32) {
			ret = executeOperator(op);
		}
		//Other op codes are hardware actions and require a hardware interface to respond to the call
		else {
			if(this.hwi != null) {
				LinkedList<Integer> hwid = hwi.hardwareActionCall(op.getOpCode(), t);
				if(hwid != null) {
					pipeController.overwrite(op.getOpCode(), hwid);
					x = op.getOpCode();
				}
				else {
					x = 0;
				}
			}
		}
		
		// The AMU Core only operates in a limited range of positive integers.
		// The maximum is set hard in the core.
		// To fix any registers that might have had their value end up outside the valid range, we fix them
		fixRegisterValues();
		// Step up the total op counter
		++opCount;
		// Step up the program counter
		++pc;
		//Check if end of script was reached
		if(pc >= script.getOpCount())
			eof = true;
			
		return ret;
	}
	
	private int executeOperator(BAMULOp op) {
		if(op.getOpCode() == BAMULOps.OPERATOR_NOP) {
			return 0;
		}
		// Unary operators
		else if(op.getOpCode() == BAMULOps.OPERATOR_LBL) {
			//Don't do anything really
			return 0;
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_JMP) {
			//TODO:
			// Add support for direct jumps
			this.pc = labelMap.get(op.getParam1()).intValue();
			if(pc >= script.getOpCount())
				return OPERATOR_FAULT_INVALID_JUMP;
			return 0;
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_PIP) {
			int param1 = translateParam(op.getParam1());
			y = pipeController.readFirstValue(param1);
			return 0;
		}
		// Binary operators
		int param1 = translateParam(op.getParam1());
		int param2 = translateParam(op.getParam2());
		
		// The boolean operators function in the following way:
		// If the expression is FALSE, the next op in the script will be skipped, effectively creating an if clause
		// therefore, we increase the program counter (pc) if the expression is false
		// in other words, if the opposite boolean logical is TRUE, we increase the pc
		if(op.getOpCode() == BAMULOps.OPERATOR_ADD) {
			setRegisterValue(op.getParam1(), param1 + param2);
			if(param1 + param2 == 0)
				++pc;
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_SUB) {
			setRegisterValue(op.getParam1(), param1 - param2);
			if(param1 - param2 == 0)
				++pc;
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_ASS) {
			setRegisterValue(op.getParam1(), param2);
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_EQ) {
			if(param1 != param2)
				++pc;
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_NOT) {
			if(param1 == param2)
				++pc;
			
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_LT) {
			if(param1 >= param2)
				++pc;
		}
		else if(op.getOpCode() == BAMULOps.OPERATOR_GT) {
			if(param1 <= param2)
				++pc;
		}
		
		return 0;
	}
	
	public String getCoreDump() {
		StringBuilder cd = new StringBuilder();
		cd.append("Core dump, core ID: ").append(this.coreId).append("\r\n");
		cd.append("Registers:\r\n");
		cd.append("X  : ").append(x).append("\r\n");
		cd.append("Y  : ").append(y).append("\r\n");
		cd.append("I  : ").append(i).append("\r\n");
		cd.append("J  : ").append(j).append("\r\n");
		cd.append("K  : ").append(k).append("\r\n");
		cd.append("L  : ").append(l).append("\r\n");
		cd.append("T  : ").append(t).append("\r\n");
		cd.append("PC : ").append(pc).append("\r\n");
		cd.append("Current OP count: ").append(opCount);
		
		return cd.toString();
	}
	
	private void mapLabels() {
		for(int i = 0; i < script.getOpCount(); ++i) {
			if(script.readOp(i).getOpCode() == BAMULOps.OPERATOR_LBL) {
				labelMap.put(script.readOp(i).getParam1(), new Integer(i));
			}
		}
	}
	
	private int getRegisterValue(String reg) {
		if(reg.toUpperCase().equals("X"))
			return x;
		if(reg.toUpperCase().equals("Y"))
			return y;
		if(reg.toUpperCase().equals("I"))
			return i;
		if(reg.toUpperCase().equals("J"))
			return j;
		if(reg.toUpperCase().equals("K"))
			return k;
		if(reg.toUpperCase().equals("L"))
			return l;
		if(reg.toUpperCase().equals("T"))
			return t;
		return -1;
	}
	
	private void setRegisterValue(String reg, int value) {
		if(reg.toUpperCase().equals("X"))
			x = value;
		if(reg.toUpperCase().equals("Y"))
			y = value;
		if(reg.toUpperCase().equals("I"))
			i = value;
		if(reg.toUpperCase().equals("J"))
			j = value;
		if(reg.toUpperCase().equals("K"))
			k = value;
		if(reg.toUpperCase().equals("L"))
			l = value;
		if(reg.toUpperCase().equals("T"))
			t = value;
	}
	
	/**
	 * "Fix" register values by "rolling them over" if they end up outside the valid ranges
	 */
	private void fixRegisterValues() {
		if(x < 0)
			x += REGISTER_MAX_VALUE;
		else if(x > REGISTER_MAX_VALUE)
			x -= REGISTER_MAX_VALUE;
		if(y < 0)
			y += REGISTER_MAX_VALUE;
		else if(y > REGISTER_MAX_VALUE)
			y -= REGISTER_MAX_VALUE;
		if(i < 0)
			i += REGISTER_MAX_VALUE;
		else if(i > REGISTER_MAX_VALUE)
			i -= REGISTER_MAX_VALUE;
		if(j < 0)
			j += REGISTER_MAX_VALUE;
		else if(j > REGISTER_MAX_VALUE)
			j -= REGISTER_MAX_VALUE;
		if(k < 0)
			k += REGISTER_MAX_VALUE;
		else if(k > REGISTER_MAX_VALUE)
			k -= REGISTER_MAX_VALUE;
		if(l < 0)
			l += REGISTER_MAX_VALUE;
		else if(l > REGISTER_MAX_VALUE)
			l -= REGISTER_MAX_VALUE;
		if(t < 0)
			t += REGISTER_MAX_VALUE;
		else if(t > REGISTER_MAX_VALUE)
			t -= REGISTER_MAX_VALUE;
	}
	
	private int translateParam(String param) {
		// Try to look up a register
		int value = getRegisterValue(param);
		if(value >= 0)
			return value;
		// If it fails it has to be a numeral
		return Integer.parseInt(param);
	}

	public int getT() {
		return t;
	}

	public int getOpCount() {
		return opCount;
	}

	public int getCoreId() {
		return coreId;
	}

	public boolean isEof() {
		return eof;
	}
}
