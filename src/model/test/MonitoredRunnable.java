package model.test;

public abstract class MonitoredRunnable implements Runnable{

	final private Monitor monitor;
	final private int number;

	public MonitoredRunnable( Monitor monitor){
		this.monitor = monitor;
		this.number = this.monitor.reserveThreadNumber();		
	}
	@Override
	public void run() {
		this.executeRun();
		this.monitor.freeThreadNumber( this.number);
	}

	abstract public void executeRun();
}
