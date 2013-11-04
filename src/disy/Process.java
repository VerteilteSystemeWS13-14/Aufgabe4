package disy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Process extends Thread {
	private final static int POOL_SIZE = 10;

	private final int id;
	private boolean active = true;
	protected final Map<Integer, Process> destinations = new HashMap<Integer, Process>();
	protected BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();

	protected Process(int id) {
		this.id = id;
	}

	public final int getID() {
		return id;
	}

	final void connect(Process destination) {
		destinations.put(destination.getID(), destination);
	}

	public final void receiveMessage(Message message) {
		if (active)
			msgQueue.add(message);
	}

	public final void multicast(Message message) {
		for (Process process : destinations.values()) {
			process.receiveMessage(message);
		}
	}

	public final void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "process " + id;
	}

	@Override
	public final void run() {
		ExecutorService exec = Executors.newFixedThreadPool(POOL_SIZE);
		try {
			while (true) {
				final Message msg = msgQueue.take();
				exec.execute(new Runnable() {
					@Override
					public void run() {
						msg.getProcessedBy(Process.this);
					}
				});

			}
		} catch (InterruptedException consumed) {
			exec.shutdown();
		}
	}

}
