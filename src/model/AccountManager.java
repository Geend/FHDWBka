package model;

import java.util.Iterator;

import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWithException;
import de.fhdw.ml.transactionFramework.transactions.TransactionAdministration;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class AccountManager {
	
	private static AccountManager theAccountManager = null;
	
	public static AccountManager getTheAccountManager(){
		if (theAccountManager == null) theAccountManager = new AccountManager();
		return theAccountManager;
	}
	private AccountManager(){}
	
	public Account find (String name) throws AccountNotFoundException{
		Iterator<Object_Transactional> accounts = Account.get$ByName(name).iterator();
		if (accounts.hasNext()) {
			return (Account) accounts.next();
		}
		throw new AccountNotFoundException(name);
	}
	private Account createOperation (String name) throws AccountException {
		this.checkName(name);
		try {
			this.find(name);
			throw new AccountAlreadyExistsException(name);
		} catch (AccountNotFoundException e) {
			return Account.create(name);
		}		
	}
	public Account create (String name) throws AccountException {
		TEOTransactionWithException<Account, AccountException> transaction = new TEOTransactionWithException<Account,	AccountException>() {
			@Override
			protected Account operation() throws AccountException {				
				return createOperation(name);
			}
		};
		TransactionAdministration.getTheTransactionAdministration().handle(transaction);
		return transaction.getResult();
	}
	private void checkName(String name)  throws AccountException {
		if (name.length() < 3) throw new AccountNameFormatException(name);
	}
	private void renameAccountOperation(Account account, String newName) throws AccountException {
		this.checkName(newName);
		try {			
			this.find( newName );
			throw new AccountAlreadyExistsException( newName);
		} catch (AccountNotFoundException e) {
			account.setName( newName );
		}		
	}
	public void renameAccount(Account account, String newName) throws AccountException{
		TEOTransactionWithException<Void, AccountException> transaction = new TEOTransactionWithException<Void, AccountException>() {
			@Override
			protected Void operation() throws AccountException {
				renameAccountOperation(account, newName);
				return null;
			}
		};
		TransactionAdministration.getTheTransactionAdministration().handle(transaction);
		transaction.getResult();
	}
}
