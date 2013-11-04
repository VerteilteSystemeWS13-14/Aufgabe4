package disy;

public abstract class DistributedSystem {
	private final Process[] processes;

	protected abstract Process newProcess(int id);

	protected DistributedSystem(int numProcesses) {
		processes = new Process[numProcesses];

		for (int i = 0; i < processes.length; i++)
			processes[i] = newProcess(i);

		for (Process source : processes)
			for (Process destination : processes)
				if (!source.equals(destination))
					source.connect(destination);
	}

	protected final Process getProcess(int index) {
		return processes[index];
	}

	protected void run() {
		for (Process process : processes) {
			process.start();
		}
		System.out.println("Distributed system up and running.");
	}

	protected final void shutdown() {
		for (Process process : processes)
			process.interrupt();
		System.out.println("Distributed system shut down.");
	}

}
