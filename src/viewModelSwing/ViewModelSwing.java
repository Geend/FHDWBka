package viewModelSwing;

import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import model.Account;
import model.AccountException;
import model.AccountManager;
import model.AccountObserver;
import viewExceptions.AccountAlreadyShownException;
import viewModel.AccountView;
import viewModel.EntryView;
import viewModel.TransferOrTransactionView;
import viewModel.UserView;
import viewModel.ViewModel;

public class ViewModelSwing extends ViewModel {
	
	public static ViewModelSwing create( UserViewSwing userView) {
		return new ViewModelSwing( userView);
	}

	private SpecialDefaultListModel<AccountViewSwing> otherAccounts;
	public DefaultListModel<TransferOrTransactionView> pendingTransfersAndOrTransactions;
	private TransactionViewSwing selectedTransaction;
	private DefaultListModel<TransferOrTransactionView> currentTransactionDetails;
	private UserViewSwing userView = null;

	private ViewModelSwing ( UserViewSwing userView) {
		this.userView  = userView;
		this.otherAccounts = new SpecialDefaultListModel<AccountViewSwing>();
		this.pendingTransfersAndOrTransactions = new SpecialDefaultListModel<TransferOrTransactionView>();
		this.currentTransactionDetails = new SpecialDefaultListModel<TransferOrTransactionView>();
	}
	public String toString(){
		return this.userView.toString();
	}
	public ListModel<AccountViewSwing> getMyAccountList() {
		return this.userView.getAccounts();
	}
	public ListModel<AccountViewSwing> getOtherAccountList() {
		return this.otherAccounts;
	}
	public ListModel<EntryView> getCurrentAccountEntries() {
		return this.userView.getCurrentAccountEntries();
	}
	public ListModel<TransferOrTransactionView> getPendingTransfersAndOrTransactions() {
		return this.userView.getPendingTransfers();
	}
	public void createAccount(String name) throws AccountException {
		this.userView.addAccount(name);
	}
	public void findAccount(String name) throws AccountException {
		Account foundAccount = AccountManager.getTheAccountManager().find(name);
		if (this.containsInOtherAccounts(foundAccount)) throw new AccountAlreadyShownException(name);
		this.otherAccounts.addElement(AccountViewSwing.create(foundAccount, this));
	}
	private boolean containsInOtherAccounts(Account account) {
		Enumeration<AccountViewSwing> otherAccountsEnumeration = this.otherAccounts.elements();
		while (otherAccountsEnumeration.hasMoreElements()){
			AccountView current = otherAccountsEnumeration.nextElement();
			if (current.isFor(account)) return true;
		}
		return false;
	}
	public void clearOtherAccounts() {
		Enumeration<AccountViewSwing> otherAccountsEnumeration = this.otherAccounts.elements();
		while (otherAccountsEnumeration.hasMoreElements()){
			AccountView current = otherAccountsEnumeration.nextElement();
			current.release();
		}
		this.otherAccounts.clear();
	}
	public void changeAccountSelection(AccountViewSwing selectedAccount) {
		this.userView.changeAccountSelection( selectedAccount );
	}
	public void changeTransactionSelection(TransactionViewSwing selectedTransaction) {
		this.selectedTransaction = selectedTransaction;
		this.currentTransactionDetails = this.selectedTransaction.getDetails();
	}
	
	@Override
	public void handleAccountUpdate(AccountObserver accountView) {
		int index = 0;
		Enumeration<AccountViewSwing> otherAccountsEnumeration = this.otherAccounts.elements();
		while (otherAccountsEnumeration.hasMoreElements()) {
			viewModel.AccountView current = otherAccountsEnumeration.nextElement();
			if (current.equals(accountView)){
				this.otherAccounts.fireEntryChanged(index);
				break;
			}
			index++;
		}
	}
	public ListModel<TransferOrTransactionView> getCurrentTransactionDetails() {
		return this.currentTransactionDetails;
	}
	@Override
	protected UserView getUserView() {
		return this.userView;
	}

}
