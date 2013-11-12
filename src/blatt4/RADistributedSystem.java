package blatt4;

import java.util.concurrent.TimeUnit;

import disy.DistributedSystem;
import disy.Process;

public class RADistributedSystem extends DistributedSystem {
	RADistributedSystem(int numProcesses) {
		super(numProcesses);
	}

	@Override
	protected Process newProcess(int id) {
		return new RAProcess(id);
	}

	@Override
	protected void run() {
		try {
			super.run();
			getProcess(0).receiveMessage(new RATrigger());
			TimeUnit.SECONDS.sleep(1);
			getProcess(1).receiveMessage(new RATrigger());
			TimeUnit.SECONDS.sleep(1);
			getProcess(2).receiveMessage(new RATrigger());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		RADistributedSystem disy = new RADistributedSystem(4);
		disy.run();
		TimeUnit.SECONDS.sleep(10);
		disy.shutdown();
	}

}
