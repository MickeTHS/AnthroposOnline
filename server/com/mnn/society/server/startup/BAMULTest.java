package com.mnn.society.server.startup;

import java.util.LinkedList;

import com.mnn.society.server.bamul.cpu.Core;
import com.mnn.society.server.bamul.parse.BAMUL;
import com.mnn.society.server.bamul.parse.Parser;
import com.mnn.society.server.bamul.utils.BAMULInvalidOpException;
import com.mnn.society.server.bamul.utils.HardwareInterface;

/**
 * A test made by patrik to test BAMUL. This should probably be removed.
 * @author mickeman
 *
 */
public class BAMULTest implements HardwareInterface {
	public static void main(String[] args) {
		BAMULTest t = new BAMULTest();
		t.run();
	}
	
	public void run() {	
		String test = "=X5=Y4<XY=I6";
		String test2 = "W";
		String test3 = "=I3:ANA=TXD-I1GA";
		String testPipes = "A$X=KY$X=LY";
		
		Parser p = new Parser();
		BAMUL b;
		try {
			b = p.parse(testPipes);
			System.out.println("Parsed BAMUL string");

			Core c = Core.newCoreInstance();
			c.loadScript(b);
			
			System.out.println("Loaded script into core");
			
			c.connectHardwareInterface(this);
			
			System.out.println("Starting execution");
			while(!c.isEof()) {
				c.executeOp();
				System.out.println(c.getCoreDump());
			}
			
			System.out.println("Execution finished");
			
		} catch (BAMULInvalidOpException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public LinkedList<Integer> hardwareActionCall(int action, int target) {		
		System.out.println("Hardware call on action " + action + " with target " + target);
		LinkedList<Integer> ll = new LinkedList<Integer>();
		ll.add(new Integer(17));
		ll.add(new Integer(73));
		return ll;
	}
}
