package viewModel;

import model.Transaction;
import model.TransferException;
import model.TransferOrTransaction;

public abstract class TransactionView implements TransferOrTransactionView{

	private static final String TransactionPrefix = "TRANSACTION ";
	protected Transaction transaction;

	protected TransactionView(){
		this.transaction = Transaction.create();		
	}
	protected TransactionView( Transaction transaction){
		this.transaction = transaction;
	}
	public String toString() {
		return TransactionPrefix + this.transaction.getNumber();
	}

	public TransferOrTransaction getTransferOrTransaction() {
		return this.transaction;
	}

	public void addTransfer( AccountView from, AccountView to, long amount, String purpose) throws TransferException {
		TransferView newTransfer = TransferView.create( from, to, amount, purpose);
		this.transaction.addTransfer( newTransfer.getTransfer() );
		this.addToTransferLst( newTransfer);
	}

	public abstract void addToTransferLst(TransferView newTransfer);

	public void accept(TransferOrTransactionViewVisitor visitor) {
		visitor.handleTransactionView(this);
	}
	@Override
	abstract public void deregister();

}
