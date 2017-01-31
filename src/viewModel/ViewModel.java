package viewModel;

import model.AccountException;
import model.TransferException;

public abstract class ViewModel implements AccountViewManager{

	public void createTransferInTransaction(AccountView from, AccountView to, long amount, String purpose, TransactionView transaction) throws TransferException {
		this.getUserView().createTransferInTransaction(from, to, amount, purpose, transaction);	
	}

	abstract protected UserView getUserView();

	public void finaliseYourself() {
		this.getUserView().deregister();
		this.clearOtherAccounts();
	}

	protected abstract void clearOtherAccounts();

	public void createTransaction() {
		this.getUserView().createTransaction();
	}

	public void book(TransferOrTransactionView transferOrTransaction) throws AccountException, TransferException {
		this.getUserView().book(transferOrTransaction);
	}

	public void createTransfer(AccountView from, AccountView to, long amount,
			String purpose) throws TransferException {
				this.getUserView().createTransfer(from, to, amount, purpose);
			}

}
