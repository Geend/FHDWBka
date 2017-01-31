package model;

import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

/** An entry (debit or credit) for an account, typically result of a booking process. */
public interface Entry extends Object_Transactional{

	abstract public <T> T acceptEntryVisitor(EntryVisitor<T> visitor);	
	public long getAmount();
	public abstract String getSubject();
	
}
