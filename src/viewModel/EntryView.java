package viewModel;

import model.Entry;
import model.EntryAbstract;
import model.EntryDebit;
import model.EntryVisitor;

public class EntryView {

	protected static final String DebitPrefix = "Debit: ";
	protected static final String CreditPrefix = "Credit: ";
	private static final String SubjectInfix = "; Subject: ";

	public static EntryView create(Entry entry) {
		return new EntryView(entry);
	}
	private Entry entry;
	
	public EntryView(Entry entry) {
		this.entry = entry;
	}
	
	public String toString(){
		return this.entry.acceptEntryVisitor(new EntryVisitor<String>(){
			@Override
			public String handleEntryDebit(EntryDebit debit) {
				return DebitPrefix;
			}
			@Override
			public String handleEntryCredit(EntryAbstract credit) {
				return CreditPrefix;
			}
		}) + this.entry.getAmount() + SubjectInfix + entry.getSubject();
	}

}
