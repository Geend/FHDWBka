package viewModel;

import java.util.Iterator;

import model.Account;
import model.AccountException;
import model.Transaction;
import model.Transfer;
import model.TransferException;
import model.TransferOrTransaction;
import model.User;
import model.UserManager;
import model.UserObserver;

abstract public class UserView implements UserObserver, AccountViewManager{

	protected User user = null;
	protected AccountTransactionFacadeView view;

	protected UserView( User user, AccountTransactionFacadeView view) {
		this.user = user;
		this.view = view;
		this.user.register( this );
	}

	public String toString(){
		return this.user.getName();
	}
	
	public void addAccount(String name) throws AccountException {
		UserManager.getTheUserManager().addAccount(this.user, name);
	}

	abstract protected void addToPendingTransfers(TransferOrTransactionView t);
	
	@Override
	abstract public void handleNewAccount(Account account);

	protected abstract AccountView fetchAccountView(Account account);

	protected void addTransferOrTransaction(TransferOrTransaction t) {
		t.accept(new model.TransferOrTransactionVisitor(){
			@Override
			public void handleTransaction(Transaction transaction) {
				TransactionView transactionView = createTransactionView(transaction);
				addToPendingTransfers(transactionView);
				for (Transfer current : transaction.getTransfers()) {
					AccountView from = fetchAccountView(current.getFromAccount());
					AccountView to = fetchAccountView(current.getToAccount());
					transactionView.addToTransferLst(TransferView.create( current, from, to ));
				}
			}
			@Override
			public void handleTransfer(Transfer transfer) {
				AccountView from = fetchAccountView(transfer.getFromAccount());
				AccountView to = fetchAccountView(transfer.getToAccount());
				addToPendingTransfers(TransferView.create( transfer, from, to ));
			}
		});
	}

	abstract protected TransactionView createTransactionView(Transaction transaction);

	@Override
	public void handleNewTransferOrTransaction(TransferOrTransaction t) {
		this.addTransferOrTransaction(t);
	}

	public void createTransaction() {	
		this.user.createTransaction();
	}

	public void createTransfer(AccountView from, AccountView to, long amount, String purpose) throws TransferException {
		this.user.createTransfer(from.getAccount(), to.getAccount(), amount, purpose);
	}

	public void createTransferInTransaction(AccountView from, AccountView to, long amount, String purpose,
			TransactionView transaction) throws TransferException {
		this.user.createTransferInTransaction(from.getAccount(), to.getAccount(), amount, purpose, 
													(Transaction) transaction.getTransferOrTransaction());
	}
	abstract protected Iterator<TransferOrTransactionView> iteratorOnPendingTransfers();

	@Override
	public void handleNewTransferInTransaction(Transaction transaction, Transfer transfer) {
		Iterator<TransferOrTransactionView> pendingTransfers = this.iteratorOnPendingTransfers();
		while(pendingTransfers.hasNext()){
			TransferOrTransactionView current = pendingTransfers.next();
			current.accept(new TransferOrTransactionViewVisitor() {				
				@Override
				public void handleTransferView(TransferView transferView) {
				}				
				@Override
				public void handleTransactionView(TransactionView transactionView) {
					if( transactionView.getTransferOrTransaction().equals(transaction)){
						transactionView.addToTransferLst(TransferView.create(transfer, fetchAccountView(transfer.getFromAccount()), fetchAccountView(transfer.getToAccount())));
					}
				}
			});	
		}
	}

	public void deregister() {
		this.user.deregister(this);
		this.clearOwnAccounts();
		this.clearPendingTransfers();
	}

	abstract protected void clearPendingTransfers();

	abstract protected void clearOwnAccounts();

	public void book(TransferOrTransactionView transferOrTransaction) throws AccountException, TransferException {
		this.user.book(transferOrTransaction.getTransferOrTransaction());
	}
}
