package model.test;

import static org.junit.Assert.assertEquals;
import model.User;
import model.UserException;
import model.UserManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fhdw.ml.transactionFramework.administration.ObjectAdministration;

public class UserTest {
	private static String UserNameTest = "TestUser";
	private Monitor monitor = new Monitor();
	
	@Before
	public void initializeObjectStore(){
		ObjectAdministration.getCurrentAdministration().initialiseApplication();		
	}
	
	@After
	public void finalizeObjectStore(){
		ObjectAdministration.getCurrentAdministration().finaliseApplication();	
		ObjectAdministration.getCurrentAdministration().deleteObjectStore();
	}
	
	@Test
	public void testUserNameViolation() {
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.monitor.waitForThreads();
		assertEquals(1, User.get$ByName(UserNameTest).size());
	}

	@Test
	public void testUserNameViolation2() {
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.prepareUser(UserNameTest);			
		this.monitor.waitForThreads();
		assertEquals(1, User.get$ByName(UserNameTest).size());
	}

	private void prepareUser(String name) {
		new MonitoredThread( new MonitoredRunnable( this.monitor ) {			
			@Override
			public void executeRun() {
				try {
					UserManager.getTheUserManager().create(name);
				} catch (UserException e) {}
			}
		}).start();		
	}

}
