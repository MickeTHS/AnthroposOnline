package com.mnn.society.server.bamul.utils;

import java.util.HashMap;


public abstract class BAMULOps {
	public static final int OPERATOR_NOP = 0;	//Null operation
	public static final int OPERATOR_NOT = 1;	//Not equal
	public static final int OPERATOR_EQ = 2;	//Equal
	public static final int OPERATOR_LT = 3;	//Less than
	public static final int OPERATOR_GT = 4;	//Greater than
	public static final int OPERATOR_ASS = 5;	//Assign
	public static final int OPERATOR_ADD = 6;	//Add
	public static final int OPERATOR_SUB = 7;	//Subtract
	public static final int OPERATOR_LBL = 8;	//Label
	public static final int OPERATOR_JMP = 9;	//Jump
	public static final int OPERATOR_PIP = 10;	//Pipe reader
	
	public static final int HARDWARE_CALL_W = 32;
	public static final int HARDWARE_CALL_E = 33;
	public static final int HARDWARE_CALL_N = 34;
	public static final int HARDWARE_CALL_S = 35;
	public static final int HARDWARE_CALL_D = 36;
	public static final int HARDWARE_CALL_A = 37;
	public static final int HARDWARE_CALL_B = 38;
	public static final int HARDWARE_CALL_R = 39;
	public static final int HARDWARE_CALL_P = 40;
	public static final int HARDWARE_CALL_M = 41;
	public static final int HARDWARE_CALL_X = 42;
	public static final int HARDWARE_CALL_Y = 43;
	public static final int HARDWARE_CALL_C = 44;
	
	private static HashMap<Character, OpSpec> opSpecs = new HashMap<Character, OpSpec>();
	static {
		// 0, null instruction ("NOP") *currently not un use
		opSpecs.put(new Character('\0'), new OpSpec(0, 0));

		// 1-31 Operators
		opSpecs.put(new Character('!'), new OpSpec(OPERATOR_NOT, 2));
		opSpecs.put(new Character('&'), new OpSpec(OPERATOR_EQ, 2));
		opSpecs.put(new Character('<'), new OpSpec(OPERATOR_LT, 2));
		opSpecs.put(new Character('>'), new OpSpec(OPERATOR_GT, 2));
		opSpecs.put(new Character('='), new OpSpec(OPERATOR_ASS, 2));
		opSpecs.put(new Character('+'), new OpSpec(OPERATOR_ADD, 2));
		opSpecs.put(new Character('-'), new OpSpec(OPERATOR_SUB, 2));
		opSpecs.put(new Character(':'), new OpSpec(OPERATOR_LBL, 1));
		opSpecs.put(new Character('G'), new OpSpec(OPERATOR_JMP, 1));
		opSpecs.put(new Character('$'), new OpSpec(OPERATOR_PIP, 1));
		// 11-31 reserved for future use
		
		// 32-*, Hardware actions
		opSpecs.put(new Character('W'), new OpSpec(HARDWARE_CALL_W, 0));
		opSpecs.put(new Character('E'), new OpSpec(HARDWARE_CALL_E, 0));
		opSpecs.put(new Character('N'), new OpSpec(HARDWARE_CALL_N, 0));
		opSpecs.put(new Character('S'), new OpSpec(HARDWARE_CALL_S, 0));
		opSpecs.put(new Character('D'), new OpSpec(HARDWARE_CALL_D, 0));
		opSpecs.put(new Character('A'), new OpSpec(HARDWARE_CALL_A, 0));
		opSpecs.put(new Character('B'), new OpSpec(HARDWARE_CALL_B, 0));
		opSpecs.put(new Character('R'), new OpSpec(HARDWARE_CALL_R, 0));
		opSpecs.put(new Character('P'), new OpSpec(HARDWARE_CALL_P, 0));
		opSpecs.put(new Character('M'), new OpSpec(HARDWARE_CALL_M, 0));
		opSpecs.put(new Character('X'), new OpSpec(HARDWARE_CALL_X, 0));
		opSpecs.put(new Character('Y'), new OpSpec(HARDWARE_CALL_Y, 0));
		opSpecs.put(new Character('C'), new OpSpec(HARDWARE_CALL_C, 0));
	}
	
	public static OpSpec opLookup(char c) {
		return opSpecs.get(new Character(c));
	}
}
