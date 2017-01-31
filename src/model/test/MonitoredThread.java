package model.test;

public class MonitoredThread extends Thread{

	public MonitoredThread( MonitoredRunnable runnable){
		super(runnable);
	}
}
