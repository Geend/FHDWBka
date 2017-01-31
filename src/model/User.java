package model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.fhdw.ml.transactionFramework.annotations.Index_Field;
import de.fhdw.ml.transactionFramework.annotations.Index_Getter;
import de.fhdw.ml.transactionFramework.transactions.TEOTransactionWithTwoExceptions;
import de.fhdw.ml.transactionFramework.transactions.TransactionAdministration;
import de.fhdw.ml.transactionFramework.typesAndCollections.LinkedListType;
import de.fhdw.ml.transactionFramework.typesAndCollections.List_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.Map2Object_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public class User implements Object_Transactional {
	
	private static final long serialVersionUID = 1L;

	@Index_Getter( fieldName = "name")
	public static Collection<Object_Transactional> get$ByName( String name ){
		return null;
	}
	
	@Index_Field
	private String name;
	private Map2Object_Transactional<String, Account> accounts;
	private List_Transactional<TransferOrTransaction> pendingTransfers;
	transient private Collection<UserObserver> observers = null;
		
	User( String name ){
		this.name = name;
		this.accounts = new Map2Object_Transactional<String, Account>();
		this.pendingTransfers = new List_Transactional<TransferOrTransaction>(new LinkedListType<TransferOrTransaction>());
	}	
	public String getName() {
		return this.name;
	}	
	public boolean hasAccount( String name){
		return this.accounts.containsKey(name);
	}
	void addAccount( String name) throws AccountException {
		Account newAccount = AccountManager.getTheAccountManager().create(name);
		this.accounts.put( name, newAccount);
		this.notifyObserversOfNewAccount( newAccount);
	}
	public Account getAccount( String name ){
		return this.accounts.get(name);
	}	
	public Collection<Account> getAccounts(){
		return this.accounts.values();
	}
	public Collection<TransferOrTransaction> getOpenTransfers(){
		return this.pendingTransfers;
	}
	public void addTransferOrTransaction(TransferOrTransaction t){
		this.pendingTransfers.add(t);
		this.notifyObserversOfNewTransferOrTransaction(t);
	}
	private Collection<UserObserver> getObservers(){
		if( this.observers == null ){
			this.observers = new LinkedList<UserObserver>();
		}
		return this.observers;
	}
	public void register(UserObserver observer) {
		this.getObservers().add(observer);
	}	
	
	public void deregister( UserObserver observer){
		this.getObservers().remove(observer);
	}
	
	private void notifyObserversOfNewTransferOrTransaction(TransferOrTransaction t) {
		for( UserObserver current : getObservers()) {
			current.handleNewTransferOrTransaction(t);
		}		
	}
	private void notifyObserversOfNewAccount(Account account){
		for( UserObserver current : getObservers()) {
			current.handleNewAccount(account);
		}
	}
	private void notifyObserversOfNewTransferInTransaction(Transaction transaction, Transfer transfer) {
		for( UserObserver current : getObservers()) {
			current.handleNewTransferInTransaction(transaction, transfer);
		}
	}
	private void notifyObserversOfBookedTransaction(TransferOrTransaction transferOrTransaction) {
		for (UserObserver current : getObservers()) {
			current.handleBookedTransferOrTransaction(transferOrTransaction);
		}
	}
	
	public void createTransaction() {
		Transaction transaction = Transaction.create();
		this.addTransferOrTransaction(transaction);
	}
	public void createTransfer(Account from, Account to, long amount, String purpose) throws TransferException {
		Transfer transfer = Transfer.create(from,to,amount,purpose);
		this.addTransferOrTransaction(transfer);		
	}
	public void createTransferInTransaction(Account from, Account to, long amount, String purpose,
			Transaction transaction) throws TransferException {
		Transfer transfer = Transfer.create(from, to, amount, purpose);
		transaction.addTransfer(transfer);
		this.notifyObserversOfNewTransferInTransaction(transaction, transfer);
	}
	public void book(TransferOrTransaction transferOrTransaction) throws AccountException, TransferException {
		TEOTransactionWithTwoExceptions<Void, AccountException, TransferException> t = new TEOTransactionWithTwoExceptions<Void, AccountException, TransferException>() {
			@Override
			protected Void operation() throws AccountException, TransferException {
				bookOperation(transferOrTransaction);
				return null;
			}
		};
		TransactionAdministration.getTheTransactionAdministration().handle(t);
		t.getResult();
	}
	public void bookOperation(TransferOrTransaction transferOrTransaction) throws AccountException, TransferException {
		Iterator<TransferOrTransaction> pending = this.pendingTransfers.iterator();
		while (pending.hasNext()) {
			TransferOrTransaction current = pending.next();
			if (transferOrTransaction.equals(current)){
				transferOrTransaction.book();
				pending.remove();
				this.notifyObserversOfBookedTransaction(transferOrTransaction);
				return;
			}
		}
		throw new TransferException("No such transfer or transaction!");
	}
}
