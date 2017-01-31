import java.awt.EventQueue;

import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import viewSwing.ViewStarter;



public class Starter {

	private static final float fontSize = 12;
	
	public static void main(String[] args) {
		changeFontSizes();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ToolTipManager.sharedInstance().setDismissDelay(8000);
					ToolTipManager.sharedInstance().setInitialDelay(1500);
					EventQueue.invokeLater(new Runnable() {						
						@Override
						public void run() {
							ViewStarter window = new ViewStarter();
							window.pack();
							window.setVisible(true);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void changeFontSizes() {
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value != null && value instanceof javax.swing.plaf.FontUIResource){
	    	javax.swing.plaf.FontUIResource font = (javax.swing.plaf.FontUIResource) value;
	    	javax.swing.plaf.FontUIResource derivedFont = new javax.swing.plaf.FontUIResource(font.deriveFont(fontSize));
	        UIManager.put (key, derivedFont);
	      }
	    } 		
	}


}
