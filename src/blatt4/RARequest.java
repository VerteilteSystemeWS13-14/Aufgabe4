package blatt4;
import disy.Message;
import disy.Process;


public class RARequest extends Message {
	private final int logicalTime;	
	
	RARequest(Process sender, int logicalTime) {
		super(sender);
		this.logicalTime = logicalTime;
	}
	
	int getLogicalTime() {
		return this.logicalTime;
	}
	
	@Override
	public void getProcessedBy(Process process) {
		((RAProcess)process).process(this);
	}

}
