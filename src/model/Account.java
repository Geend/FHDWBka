package model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.fhdw.ml.transactionFramework.annotations.Index_Field;
import de.fhdw.ml.transactionFramework.annotations.Index_Getter;
import de.fhdw.ml.transactionFramework.typesAndCollections.LinkedListType;
import de.fhdw.ml.transactionFramework.typesAndCollections.List_Transactional;
import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

/** A simple account that possesses a list of account entries, 
 *  the sum of which constitutes the account's balance.
 */
public class Account implements Object_Transactional {
	
	private static final long serialVersionUID = 1L;
	/** The balance of all accounts shall be greater or equal than this limit. */
	public static final long UniversalAccountLimit = -1000; 

	public static Account create(String name) {
		return new Account(name);
	}
	
	@Index_Getter( fieldName = "name")
	public static Collection<Object_Transactional> get$ByName( String name ){
		return null;
	}
	
	@Index_Field
	private String name;
	private long balance;
	private List_Transactional<Entry> accountEntries;
	transient private List<AccountObserver> observers;

	public Account(String name) {
		this.name = name;
		this.balance = 0;
		this.accountEntries = new List_Transactional<Entry>( new LinkedListType<Entry>());
	}
	public long getBalance() {
		return this.balance;
	}
	public String getName() {
		return this.name;
	}
	public List<Entry> getLastAccountEntries(int number) {
		List<Entry> result = new LinkedList<Entry>();
		java.util.Iterator<Entry> entries = this.accountEntries.iterator();
		int counter = 0;
		while (entries.hasNext() && counter ++ < number) {
			result.add(entries.next());
		}
		return result;
	}
	private List<AccountObserver> getObservers() {
		if( this.observers == null ){
			this.observers = new LinkedList<AccountObserver>();
		}
		return this.observers;
	}
	public void register(AccountObserver observer) {
		if (this.getObservers().contains(observer)) return;
		this.getObservers().add(observer);
	}
	public void deregister(AccountObserver observer) {
		this.getObservers().remove(observer);
	}
	private void notifyObservers() {
		Iterator<AccountObserver> currentObservers = this.getObservers().iterator();
		while (currentObservers.hasNext()) currentObservers.next().update();
	}
	public void debit(Transfer transfer) {
		this.balance = this.balance - transfer.getAmount();
		this.accountEntries.add(0, EntryDebit.create( transfer ) );
		this.notifyObservers();
	}
	public void credit(Transfer transfer) {
		this.balance = this.balance + transfer.getAmount();
		this.accountEntries.add(0, EntryCredit.create( transfer ) );
		this.notifyObservers();
	}
	public void setName(String newName) {
		this.name = newName;
		this.notifyObservers();
	}
}
