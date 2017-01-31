package viewModelSwing;

import java.util.Iterator;

import javax.swing.ListModel;

import model.Account;
import model.Entry;
import viewModel.AccountViewManager;
import viewModel.EntryView;

public class AccountViewSwing extends viewModel.AccountView {
	
	public static AccountViewSwing create(Account account, AccountViewManager manager){
		return new AccountViewSwing(account, manager);
	}

	public AccountViewSwing(Account account, AccountViewManager manager) {
		super( account, manager);
	}
	public ListModel<EntryView> getAccountEntries() {
		SpecialDefaultListModel<EntryView> result = new SpecialDefaultListModel<EntryView>();
		Iterator<Entry> entryIterator = account.getLastAccountEntries(NumberOfShownEntries).iterator();
		while (entryIterator.hasNext()) result.addElement(EntryView.create(entryIterator.next()));
		return result;
	}
	@Override
	public void update() {
		this.manager.handleAccountUpdate(this);
	}

}
