package com.mnn.society.gui.windows;

import com.mnn.society.gui.ScrollableText;

/**
 * More or less a static class that just allows strings to be instantly output on the console instantly.
 * @author mickeman
 *
 */
public class Console {
	public static int CONSOLE = 0, WORKER_OUTPUT = 1;
	
	public static ScrollableText general, workerConsole; //TODO: maybe not use workerConsole, maybe implement a new Panel for that
	
	public Console() {
		
	}

	//TODO: Synchronize
	public static void log(int target, String text) {
		if (target == Console.CONSOLE) {
			if (Console.general != null)
				Console.general.addString(text);
			return;
		}
		if (target == Console.WORKER_OUTPUT) {
			if (Console.workerConsole != null) {
				Console.workerConsole.addString(text);
			}
			return;
		}
	}
}
