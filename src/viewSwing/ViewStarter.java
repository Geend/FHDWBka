package viewSwing;

import javax.swing.JFrame;

import model.UserException;
import model.UserManager;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class ViewStarter extends JFrame {

	protected static final int OffSetIncrement = 40;
	private static final String UserPrefix = "User ";
	private int offset = 80; 
	private int userNumber = 1;
	
	public ViewStarter() {
		initialize();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				startNewUser();
				startNewUser();
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setBounds(10, 10, 150, 120);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));	
		JButton newUserButton = new JButton("New user");
		newUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startNewUser();
			}
		});
		this.getContentPane().add(newUserButton, BorderLayout.CENTER);
	}
	private void startNewUser() {		
		View newUserWindow;
		try {
			newUserWindow = new View(offset, UserManager.getTheUserManager().create(UserPrefix + userNumber++));
		} catch (UserException e) {
			throw new Error(e);
		}
		offset = offset + OffSetIncrement;
		newUserWindow.pack();
		newUserWindow.setVisible(true);
		newUserWindow.getAccountNameInput().requestFocus();
	}

}
