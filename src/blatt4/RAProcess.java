package blatt4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import disy.Process;

public class RAProcess extends Process {

	private volatile Integer logicalTime;
	private volatile int wantedTime;
	private volatile RessourceStatus status;
	
	private CountDownLatch latch;

	protected RAProcess(int id) {
		super(id);
		logicalTime = 0;
		wantedTime = 0;
		status = RessourceStatus.RELEASED;
	}

	// TRIGGER (There can only be one request per process)
	public void process(RATrigger raTrigger) {
		incLogicalTime(logicalTime);
		
		// Prevent that there can be more triggers/requests at the same time
		synchronized (status) {
			if (status != RessourceStatus.RELEASED)
				return;
			
			System.out.printf("%s trying to aquire lock...\n", this);
			status = RessourceStatus.WANTED;
		}
		
		wantedTime = logicalTime; //!! Hier falsche Zeit bei 2 Threads die Trigger Msgs bearbeiten?
		
		// Blocks
		waitForResponse();
		
		// All processes sent response
		synchronized (status) {
			status = RessourceStatus.HELD; //!!
		}
		System.out.printf("%s holds lock.\n", this);
		
		process(); // Do some work (sleep)
		
		synchronized (status) {
			status = RessourceStatus.RELEASED; //!!
		}
		System.out.printf("%s released lock.\n", this);
	}
	
	private void waitForResponse() {
		RARequest req = new RARequest(this, wantedTime);
		latch = new CountDownLatch(destinations.size());
		multicast(req);
		waitLatch();
	}
	
	private void waitLatch() {
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			System.out.println(this + ": " + e.getMessage());
		}
	}
	
	private void process() {
		System.out.printf("%s processing ...\n", this);
		try
		{
			TimeUnit.SECONDS.sleep(3);
		}
		catch (Exception e)
		{
			System.out.printf("%s %s\n",this, e.getMessage());
		}
	}
	
	// REQUEST
	public void process(RARequest raRequest) {
		//System.out.printf("%s processing request from %s...\n", this, raRequest.getSender());
		
		incLogicalTime(raRequest.getLogicalTime());
		
		synchronized (status) {
			if(status == RessourceStatus.HELD ||
					(status == RessourceStatus.WANTED && wantedTime < raRequest.getLogicalTime())) {
				
				// The request has to wait
				//queueRequest(raRequest);
				
				
				// ?????????
				
				System.out.println("WAIIITTTTT");
				try {
					status.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			sendResponse(raRequest.getSender());
		}
	}
	
	private void sendResponse(Process destination)
	{
		RAResponse response;
		synchronized (logicalTime) {
			response = new RAResponse(this, logicalTime);
		}
		destination.receiveMessage(response);
	}

	// RESPONSE
	public void process(RAResponse raResponse) {
		System.out.printf("%s received response from %s!\n",this, raResponse.getSender());
		
		incLogicalTime(raResponse.getLogicalTime());
		latch.countDown();
	}
	
	private void incLogicalTime(int p_logicalTime)
	{
		synchronized (logicalTime) {
			if(logicalTime < p_logicalTime)
				logicalTime = p_logicalTime;
		
			logicalTime++;
		}
	}
}
