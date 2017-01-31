package model;

import java.util.Iterator;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWithException;
import de.fhdw.ml.transactionFramework.transactions.TransactionAdministration;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class UserManager {
	private static UserManager theUserManager = null;
	
	public static UserManager getTheUserManager(){
		if( theUserManager == null ) theUserManager = new UserManager();
		return theUserManager;
	}
	private UserManager(){}

	public User find(String name) throws UserNotFoundException {
		Iterator<Object_Transactional> users = User.get$ByName(name).iterator();
		if (users.hasNext()) {
			return (User) users.next();
		}
		throw new UserNotFoundException(name);
	}
	private User createOperation(String name) throws UserException {
		this.checkName(name);
		try {
			this.find(name);
			throw new UserAlreadyExistsException(name);
		} catch (UserNotFoundException e) {
			return new User(name);
		}
	}
	public User create(String name) throws UserException{
		TEOTransactionWithException<User, UserException> transaction = new TEOTransactionWithException<User, UserException>() {
			@Override
			protected User operation() throws UserException {
				return createOperation(name);
			}
		};
		TransactionAdministration.getTheTransactionAdministration().handle(transaction);
		return transaction.getResult();
	}
	private void checkName(String name)  throws UserException {
		if (name.length() < 3) throw new UserNameFormatException(name);
	}
	public void addAccount(User user, String name) throws AccountException {
		user.addAccount(name);
	}


}
