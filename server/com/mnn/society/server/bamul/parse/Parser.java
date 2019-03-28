package com.mnn.society.server.bamul.parse;

import com.mnn.society.server.bamul.utils.BAMULInvalidOpException;
import com.mnn.society.server.bamul.utils.BAMULOps;
import com.mnn.society.server.bamul.utils.OpSpec;

public class Parser {
	/**
	 * Parse a bamulString into a BAMUL execution object. 
	 * @param bamulString
	 */
	
	public BAMUL parse(String bamulString) throws BAMULInvalidOpException {
		BAMUL bamul = new BAMUL();
		
		int currentOpPC;
		
		// First pass
		// Read string into BAMUL ops with params and verifying op validity
		for(int i = 0; i < bamulString.length(); ++i) {
			OpSpec currentOpSpec = BAMULOps.opLookup(bamulString.charAt(i));
			if(currentOpSpec == null)
				throw new BAMULInvalidOpException("Invalid command at index " + i + ": " + bamulString.charAt(i));
			//throw new Exception()
			currentOpPC = currentOpSpec.getParamCount();
			if(currentOpPC == 0) {
				bamul.addOp(new BAMULOp(currentOpSpec.getOpCode(), null, null));
			}
			else if(currentOpPC == 1) {
				String p1 = parseParam(bamulString, ++i);
				i += p1.length() - 1;
				bamul.addOp(new BAMULOp(currentOpSpec.getOpCode(), p1, null));
			}
			else if(currentOpPC == 2) {
				String p1 = parseParam(bamulString, ++i);
				i += p1.length() - 1;
				String p2 = parseParam(bamulString, ++i);
				i += p2.length() - 1;
				bamul.addOp(new BAMULOp(currentOpSpec.getOpCode(), p1, p2));
			}
		}
		
		// TODO:
		// Validate jump destinations
		// Validate registers
		
		return bamul;
	}
		
	private String parseParam(String bamulString, int i) {
		// Numeric
		if(bamulString.charAt(i) >= 48 && bamulString.charAt(i) <= 57) {
			int j = i;
			while(j < bamulString.length() && bamulString.charAt(j) >= 48 && bamulString.charAt(j) <= 57) {
				j++;
			}
			return bamulString.substring(i, j);
		}
		// Label/Register
		else {
			return bamulString.substring(i, i + 1);
		}
	}
}
