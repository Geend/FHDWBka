package viewModel;

import model.Account;
import model.AccountObserver;

abstract public class AccountView  implements AccountObserver {

	protected static final int NumberOfShownEntries = 10;

	private static final String SaldoOpenBracket = " (";
	private static final String SaldoCloseBracket = ")";

	protected Account account = null;

	protected final AccountViewManager manager;

	protected AccountView(Account account, AccountViewManager manager) {
		this.account = account;
		this.manager = manager;
		this.account.register(this);
	}
	public Account getAccount() {
		return this.account;
	}
	public String getName() {
		return this.account.getName();
	}
	public String toString() {
		return this.account.getName() + SaldoOpenBracket + this.account.getBalance() + SaldoCloseBracket;
	}
	public boolean isFor(Account account) {
		return this.account.equals(account);
	}
	public void release() {
		this.account.deregister(this);
	}
	public void finalize(){
		this.release();
	}
	public void deregister() {
		this.account.deregister(this);
	}
}
