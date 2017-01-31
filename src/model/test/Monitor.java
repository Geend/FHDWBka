package model.test;

import java.util.List;
import java.util.Vector;

public class Monitor {
	private int nextNumber;
	private List<Integer> numbers;
 	private int downCounter;
	
	public Monitor( ){
		this.numbers = new Vector<Integer>();
		this.nextNumber = 0;
		this.downCounter = 0;
	}
	synchronized public void waitForThreads() {
		while( this.downCounter > 0 ) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new Error(e);
			}
		}
	}
	synchronized public int reserveThreadNumber() {
		this.numbers.add( this.nextNumber++, this.nextNumber);
		this.downCounter++;
		return this.nextNumber;
	}
	synchronized public void freeThreadNumber(int number) {
		if( this.numbers.size() < number ) throw new Error("Unknown thread number: " + number);
		if( this.numbers.get(number-1) == 0 ) throw new Error ("Thread already finished: " + number); 
		this.numbers.set( number-1, 0); 
		this.downCounter--;
		if( this.downCounter == 0) {
			this.nextNumber = 0;
			this.notify();
		}
	}
}
