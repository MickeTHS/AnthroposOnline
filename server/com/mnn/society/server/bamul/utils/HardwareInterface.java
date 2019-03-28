package com.mnn.society.server.bamul.utils;

import java.util.LinkedList;

public interface HardwareInterface {
	public LinkedList<Integer> hardwareActionCall(int opCode, int target);
}
