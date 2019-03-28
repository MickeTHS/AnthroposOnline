package com.mnn.society.server.execution;
import java.util.LinkedList;

import com.mnn.society.server.utils.Logger;
import com.mnn.society.server.utils.ThreadLocker;

public class WorkQueue {
	private final int nThreads;
	private final PoolWorker[] threads;
	private final LinkedList<Runnable> queue;
	
	public WorkQueue(int nThreads) {
		this.nThreads = nThreads;
		queue = new LinkedList<Runnable>();
		threads = new PoolWorker[this.nThreads];
		
		for (int i=0; i<nThreads; i++) {
			threads[i] = new PoolWorker();
			threads[i].start();
		}
	}
	
	public void execute(Runnable r) {
		synchronized(queue) {
			queue.addLast(r);
			queue.notify();
		}
	}
	
	public void kill () {
		Logger.log(Logger.LOG_SERVER, "SERVER : WorkQueue : Killing threads");
		for (int i = 0; i < this.nThreads; i++) {
			synchronized (this.threads[i].lock) {
				this.threads[i].lock.stop();
				this.threads[i].interrupt();
			}
		}
	}
	
	private class PoolWorker extends Thread {
		public ThreadLocker lock = new ThreadLocker ();
		
		public void run() {
			Runnable r;
	
			while (true) {
				synchronized (lock) {
					if (lock.isStopping()) return;
				}
				
				synchronized(queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						}
						catch (InterruptedException ignored) { 
							synchronized (this.lock) {
								if (this.lock.isStopping()) return;
							}
						}
					}
					r = (Runnable) queue.removeFirst();
				}
				// If we don't catch RuntimeException, 
				// the pool could leak threads
				try {
					r.run();
				}
				catch (RuntimeException e) {
					Logger.log(Logger.LOG_SERVER, "SERVER : PoolWorker : Exception Run: " + e.getStackTrace());
				}
			}
		}
	}
}