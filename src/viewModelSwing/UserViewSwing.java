package viewModelSwing;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

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

public class UserViewSwing extends UserView {
	public static UserViewSwing create(User user, AccountTransactionFacadeView view) {
		return new UserViewSwing( user, view );
	}

	private SpecialDefaultListModel<AccountViewSwing> myAccounts;
	private AccountViewSwing selectedAccount;
	private ListModel<EntryView> currentEntries;
	private DefaultListModel<TransferOrTransactionView> pendingTransfers;
	
	protected UserViewSwing( User user, AccountTransactionFacadeView view){
		super(user, view);
		this.initialize();
	}
	private void initialize() {
		this.myAccounts = new SpecialDefaultListModel<AccountViewSwing>();
		this.currentEntries = new DefaultListModel<EntryView>();
		for (Account current : this.user.getAccounts()) {
			this.myAccounts.addElement( AccountViewSwing.create(current, this));
		}
		this.pendingTransfers = new SpecialDefaultListModel<TransferOrTransactionView>();
		for (TransferOrTransaction current : this.user.getOpenTransfers()) {
			this.addTransferOrTransaction(current);
		}
	}
	public DefaultListModel<TransferOrTransactionView> getPendingTransfers(){
		return pendingTransfers;		
	}

	public SpecialDefaultListModel<AccountViewSwing> getAccounts() {
		return this.myAccounts;
	}
	@Override
	public void handleNewAccount( Account account) {
		this.getAccounts().addElement( AccountViewSwing.create( account, this));		
	}

	@Override
	public void handleAccountUpdate(AccountObserver accountView) {
		int index = 0;
		Enumeration<AccountViewSwing> myAccountsEnumeration = this.myAccounts.elements();
		while (myAccountsEnumeration.hasMoreElements()) {
			viewModel.AccountView current = myAccountsEnumeration.nextElement();
			if (current.equals(accountView)){
				this.myAccounts.fireEntryChanged(index);
				break;
			}
			index++;
		}
		if (this.selectedAccount != null && this.selectedAccount.equals(accountView)) 
			this.changeAccountSelection(this.selectedAccount);		
	}
	@Override
	public void handleBookedTransferOrTransaction(TransferOrTransaction transferOrTransaction) {
		Enumeration<TransferOrTransactionView> pendingTransfers = this.pendingTransfers.elements();
		while (pendingTransfers.hasMoreElements()){
			TransferOrTransactionView current = pendingTransfers.nextElement();
			if (current.getTransferOrTransaction().equals(transferOrTransaction)){
				this.pendingTransfers.removeElement(current);
				return;
			}
		}
	}

	public void changeAccountSelection(AccountViewSwing account) {
		this.selectedAccount  = account;
		this.currentEntries = account.getAccountEntries();
		this.view.updateEntriesOfSelectedAccount();				
	}

	public ListModel<EntryView> getCurrentAccountEntries() {
		return this.currentEntries;
	}

	public AccountView getSelectedAccount() {
		return this.selectedAccount;
	}
	@Override
	protected void addToPendingTransfers(TransferOrTransactionView t) {
		this.pendingTransfers.addElement(t);
		
	}
	protected AccountView fetchAccountView(Account account) {
		return new AccountViewSwing(account, this);
	}
	@Override
	protected Iterator<TransferOrTransactionView> iteratorOnPendingTransfers() {
		LinkedList<TransferOrTransactionView> resultList = new LinkedList<TransferOrTransactionView>();
		Enumeration<TransferOrTransactionView> pendingTransferEnumeration = this.pendingTransfers.elements();
		while (pendingTransferEnumeration.hasMoreElements()) {
			resultList.add(pendingTransferEnumeration.nextElement());
		}
		return resultList.iterator();
	}
	@Override
	protected void clearOwnAccounts() {
		Enumeration<AccountViewSwing> myAccountsEnumeration = this.myAccounts.elements();
		while (myAccountsEnumeration.hasMoreElements()) {
			myAccountsEnumeration.nextElement().deregister();
		}
	}
	@Override
	protected void clearPendingTransfers() {
		Enumeration<TransferOrTransactionView> transfers = this.pendingTransfers.elements();
		while (transfers.hasMoreElements()) {
			transfers.nextElement().deregister();
		}
	}
	@Override
	protected TransactionView createTransactionView(Transaction transaction) {
		return TransactionViewSwing.create(transaction);
	}

}
