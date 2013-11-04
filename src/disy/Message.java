package disy;

public abstract class Message {
	private final Process sender;

	protected Message(Process sender) {
		this.sender = sender;
	}

	public Process getSender() {
		return sender;
	}

	abstract public void getProcessedBy(Process process);
}
