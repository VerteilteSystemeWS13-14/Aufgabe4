package blatt4;
import disy.Message;
import disy.Process;


public class RATrigger extends Message {
	RATrigger() {
		super(null);
	}
	
	@Override public void getProcessedBy(Process process) {
		((RAProcess) process).process(this);
	}

}
