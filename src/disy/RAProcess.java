package disy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RAProcess extends Process {

	private int logicalTime;
	private CountDownLatch latch;
	private RessourceStatus status;

	protected RAProcess(int id) {
		super(id);
		logicalTime = 0;
		status = RessourceStatus.RELEASED;
	}

	public void process(RATrigger raTrigger) {
		aquireLock();
		holdLock();
		process();
		releaseLock();
	}
	private void aquireLock()
	{
		System.out.printf("%s trying to aquire lock...\n", this);
		
		incLogicalTime(logicalTime);
		status = RessourceStatus.WANTED;
		
		RARequest req = new RARequest(this, logicalTime);
		latch = new CountDownLatch(destinations.size());
		multicast(req);
		waitLatch();
		
	}
	
	private void holdLock()
	{
		status = RessourceStatus.HELD;
		System.out.printf("%s holds lock.\n", this);
	}
	
	private void releaseLock()
	{
		status = RessourceStatus.RELEASED;
		System.out.printf("%s released lock.\n", this);
	}
	
	private void process()
	{
		System.out.printf("%s processing ...\n", this);
		try
		{
			TimeUnit.SECONDS.sleep(5);
		}
		catch (Exception e)
		{
			System.out.printf("%s %s\n",this, e.getMessage());
		}
	}
	
	private void waitLatch()
	{
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
			System.out.println(this + ": " + e.getMessage());
		}
	}

	public void process(RARequest raRequest) {
		//System.out.printf("%s processing request from %s...\n", this, raRequest.getSender());
		
		incLogicalTime(raRequest.getLogicalTime());
		//TODO zeiten vergleich stimmt noch nicht?
		if(status == RessourceStatus.HELD ||
		  (status == RessourceStatus.WANTED && logicalTime < raRequest.getLogicalTime()))
			queueRequest(raRequest);
		else
			sendResponse(raRequest.getSender());
	}
	
	private void queueRequest(RARequest raRequest)
	{
		//System.out.printf("%s queued request.", this);
		msgQueue.add(raRequest);
	}
	
	private void sendResponse(Process destination)
	{
		RAResponse response = new RAResponse(this, logicalTime);
		destination.receiveMessage(response);
	}

	public void process(RAResponse raResponse) {
		System.out.printf("%s received response from %s!\n",this, raResponse.getSender());
		
		incLogicalTime(raResponse.getLogicalTime());
		latch.countDown();
	}
	
	private void incLogicalTime(int p_logicalTime)
	{
		if(logicalTime < p_logicalTime)
			logicalTime = p_logicalTime;
		
		logicalTime++;
	}
}
