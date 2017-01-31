package model;


public class Transfer implements TransferOrTransaction {
	
	private static final long serialVersionUID = 1L;

	public static Transfer create(Account from, Account to,	long amount, String purpose) throws TransferException {
		return new Transfer(from, to, amount, purpose);
	}
	final private Account fromAccount;
	final private Account toAccount;
	final private long amount;
	final private String subject;

	public Transfer( Account from, Account to, long amount, String purpose) throws TransferException {
		if( amount <= 0 ) throw new TransferException("Amount must be positive!");
		this.fromAccount = from;
		this.toAccount = to;
		
		this.amount = amount;
		this.subject = purpose;
	}
	public Account getFromAccount() {
		return fromAccount;
	}
	public Account getToAccount() {
		return toAccount;
	}
	public long getAmount() {
		return amount;
	}
	public String getSubject() {
		return this.subject;
	}
	@Override
	public void book() {
		this.fromAccount.debit(this);
		this.toAccount.credit( this);
	}
	@Override
	public void accept(TransferOrTransactionVisitor visitor) {
		visitor.handleTransfer(this);
	}

}
