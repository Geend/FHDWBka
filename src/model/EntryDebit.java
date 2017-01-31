package model;

public class EntryDebit extends EntryAbstract {
	private static final long serialVersionUID = 1L;

	private EntryDebit(Transfer transfer) {
		super( transfer );
	}

	@Override
	public <T> T acceptEntryVisitor(EntryVisitor<T> visitor) {
		return visitor.handleEntryDebit( this );
	}

	public static Entry create(Transfer transfer) {
		return new EntryDebit( transfer );
	}

}
