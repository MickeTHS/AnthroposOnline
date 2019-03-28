package com.mnn.society.server.bamul.utils;

import com.mnn.society.server.bamul.cpu.Core;
import com.mnn.society.server.bamul.parse.BAMUL;
import com.mnn.society.server.bamul.parse.Parser;

/**
 * will run a "fake" bamul initialization to test the scripts validity
 * @author mickeman
 *
 */
public class Validator {
	public static boolean validBAMULScript (String script) {
		Parser parser = new Parser();
		try {
			BAMUL bamulProgram = parser.parse(script);
			
			Core core = Core.newCoreInstance();
			core.loadScript(bamulProgram);
			
			if (core == null || bamulProgram == null) return false;
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
}