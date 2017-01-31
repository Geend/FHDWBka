package viewFX;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class StatusBar extends Label {
	static private final long StatusBarResetTime = 10000;
	private Thread statusResetter;
	
	public StatusBar(){
		super();
		this.setTextFill( Color.RED );
		this.setVisible(false);
	}
	
	public void showError(String message) {		
		this.setText( message );
		this.setVisible(true);
		if( this.statusResetter != null && this.statusResetter.isAlive()) {
			this.statusResetter.interrupt();
		}
		this.statusResetter = new Thread( new Runnable() {			
			@Override
			public void run() {
				synchronized (StatusBar.this) {
					try {
						StatusBar.this.wait(StatusBarResetTime);
						Platform.runLater(new Runnable() {							
							@Override
							public void run() {
								setVisible(false);
							}
						});
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		});
		this.statusResetter.start();
	}


}
