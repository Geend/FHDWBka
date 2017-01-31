package viewModelFX;

import java.util.Iterator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Account;
import model.AccountObserver;
import model.Transaction;
import model.TransferOrTransaction;
import model.User;
import viewModel.AccountTransactionFacadeView;
import viewModel.AccountView;
import viewModel.EntryView;
import viewModel.TransactionView;
import viewModel.TransferOrTransactionView;
import viewModel.UserView;

public class UserViewFX extends UserView {
	public static UserViewFX create(User user, AccountTransactionFacadeView view) {
		return new UserViewFX( user, view);
	}

	private AccountViewFX selectedAccount;
	private ObservableList<AccountViewFX> myAccounts = null;
	private ObservableList<EntryView> currentEntries;
	private ObservableList<TransferOrTransactionView> pendingTransfers;
	
	protected UserViewFX( User user, AccountTransactionFacadeView view ){
		super(user, view);
		this.initialize();
	}
	
	private void initialize() {
		this.myAccounts = FXCollections.observableArrayList();
		for (Account current : this.user.getAccounts()) {
			this.myAccounts.add( AccountViewFX.create(current, this));
		}
		this.pendingTransfers = FXCollections.observableArrayList();
		for (TransferOrTransaction current : this.user.getOpenTransfers()) {
			this.addTransferOrTransaction(current);
		}
	}

	public ObservableList<AccountViewFX> getAccounts() {
		return this.myAccounts;
	}
	public ObservableList<TransferOrTransactionView> getPendingTransfers(){
		return pendingTransfers;		
	}

	@Override
	public void handleNewAccount( Account account) {
		this.getAccounts().add( AccountViewFX.create( account, this));		
	}

	@Override
	public void handleAccountUpdate(AccountObserver accountView) {
		Platform.runLater(new Runnable() {				
			@Override
			public void run() {
				if( UserViewFX.this.getAccounts().contains( accountView )){
					int index = UserViewFX.this.getAccounts().indexOf( accountView );
					UserViewFX.this.getAccounts().set( index, (AccountViewFX) accountView);
				}		
			}
		});
	}

	public void changeAccountSelection(AccountViewFX account) {
		this.selectedAccount  = account;
		this.currentEntries = account.getLastAccountEntries();
		this.view.updateEntriesOfSelectedAccount();				
	}

	public ObservableList<EntryView> getCurrentAccountEntries() {
		return this.currentEntries;
	}

	public AccountViewFX getSelectedAccount() {
		return this.selectedAccount;
	}

	@Override
	public void addToPendingTransfers(TransferOrTransactionView t) {
		this.pendingTransfers.add(t);		
	}

	@Override
	public AccountView fetchAccountView(Account account) {
		return new AccountViewFX(account, this);
	}

	public Iterator<TransferOrTransactionView> iteratorOnPendingTransfers(){
		return this.pendingTransfers.iterator();
	}

	@Override
	protected void clearOwnAccounts() {
		for (AccountView current : this.myAccounts) {
			current.deregister();
		}
	}

	@Override
	protected void clearPendingTransfers() {
		for (TransferOrTransactionView current : this.pendingTransfers) {
			current.deregister();
		}
	}

	@Override
	protected TransactionView createTransactionView(Transaction transaction) {
		return TransactionViewFX.create(transaction);
	}

	@Override
	public void handleBookedTransferOrTransaction(TransferOrTransaction transferOrTransaction) {
		Iterator<TransferOrTransactionView> pendingTransfers = this.pendingTransfers.iterator();
		while (pendingTransfers.hasNext()){
			TransferOrTransactionView current = pendingTransfers.next();
			if (current.getTransferOrTransaction().equals(transferOrTransaction)){
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						pendingTransfers.remove();
					}
				});
				return;
			}
		}
	}
}
