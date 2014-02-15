package uk.co.immutablefix.multicorecontrol;

public class BusyThread {
	private boolean running = false;
	private Runnable runnable = null;
	private Thread thread = null;

	public void BusyThread(){
		runnable = new Runnable() {
			@Override
			public void run() {
				while(running);
				return;
			}
		};
	}
	
	public void run() {
		while (running);
	}
	
	public void start(){
		
	}
}
