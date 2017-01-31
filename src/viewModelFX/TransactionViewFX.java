package viewModelFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Transaction;
import viewModel.TransactionView;
import viewModel.TransferOrTransactionView;
import viewModel.TransferView;

public class TransactionViewFX extends TransactionView {

	public static TransactionViewFX create() {
		return new TransactionViewFX();
	}
	public static TransactionViewFX create( Transaction transaction){
		return new TransactionViewFX(transaction);
	}

	private ObservableList<TransferOrTransactionView> transfers = null;

	private TransactionViewFX() {
		super();
		this.transfers = FXCollections.observableArrayList();
	}
	private TransactionViewFX(Transaction transaction) {
		super(transaction);
		this.transfers = FXCollections.observableArrayList();
	}
	public ObservableList<TransferOrTransactionView> getDetails() {
		return this.transfers;
	}

	@Override
	public void addToTransferLst(TransferView newTransfer) {
		this.transfers.add( newTransfer );
	}
	@Override
	public void deregister() {
		for (TransferOrTransactionView current : this.transfers) {
			current.deregister();
		}
	}
}
