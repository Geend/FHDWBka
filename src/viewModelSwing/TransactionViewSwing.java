package viewModelSwing;

import java.util.Enumeration;

import model.Transaction;
import viewModel.TransactionView;
import viewModel.TransferOrTransactionView;
import viewModel.TransferView;

public class TransactionViewSwing extends TransactionView  {

	public static TransactionViewSwing create() {
		return new TransactionViewSwing();
	}
	public static TransactionViewSwing create( Transaction transaction ) {
		return new TransactionViewSwing(transaction);
	}
	private SpecialDefaultListModel<TransferOrTransactionView> transfers;
	
	private TransactionViewSwing() {
		super();
		this.transfers = new SpecialDefaultListModel<TransferOrTransactionView>();
	}
	private TransactionViewSwing( Transaction transaction ) {
		super( transaction );
		this.transfers = new SpecialDefaultListModel<TransferOrTransactionView>();
	}
	Transaction getTransaction() {
		return transaction;
	}
	public SpecialDefaultListModel<TransferOrTransactionView> getDetails(){
		return this.transfers;
	}
	@Override
	public void addToTransferLst( TransferView newTransfer) {
		this.transfers.addElement( newTransfer);
	}
	@Override
	public void deregister() {
		Enumeration<TransferOrTransactionView> transfers = this.transfers.elements();
		while (transfers.hasMoreElements()) {
			transfers.nextElement().deregister();
		}
	}
}
