package com.mnn.society.server.bamul.cpu.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * The Pipe controller controls the access to all pipes associated with it and by extension to a Core. 
 * @author Patrik
 *
 */

//TODO:
//Implement max pipe limitation
public class PipeController {
	//private static final int MAX_PIPES = 16;
	//private static final int MAX_PIPE_SIZE = 256;
	
	//private static final int REQUEST_PIPE_ERROR_NO_FREE_PIPES = -1;
	
	private Map<Integer, LinkedList<Integer>> pipes;
	int pipeCounter;

	public PipeController() {
		pipes = new HashMap<Integer, LinkedList<Integer>>();
	}
	
	public void createAndAdd(int pipeId, LinkedList<Integer> data) {
		pipes.get(new Integer(pipeId)).addAll(data);
	}
	
	public void overwrite(int pipeId, LinkedList<Integer> data) {
		pipes.put(new Integer(pipeId), data);
	}
	
	public int readFirstValue(int pipeId) {
		Queue<Integer> pipe = pipes.get(new Integer(pipeId));
		if(pipe != null)
			if(pipe.isEmpty())
				return 0;
			else
				return pipe.poll();
		else
			return 0;
	}
}
