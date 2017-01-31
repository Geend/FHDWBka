package model;

public interface EntryVisitor<T> {

	T handleEntryDebit(EntryDebit entryDebit);

	T handleEntryCredit(EntryAbstract entryCredit);

}
