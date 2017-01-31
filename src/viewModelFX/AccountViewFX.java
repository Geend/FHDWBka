package viewModelFX;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Account;
import model.Entry;
import viewModel.AccountView;
import viewModel.AccountViewManager;
import viewModel.EntryView;

public class AccountViewFX extends AccountView {

	public static AccountViewFX create(Account account, AccountViewManager manager) {
		return new AccountViewFX( account, manager);
	}

	public AccountViewFX( Account account, AccountViewManager manager) {
		super( account, manager );
	}

	@Override
	public void update() {
		this.manager.handleAccountUpdate(this);
	}

	public ObservableList<EntryView> getLastAccountEntries() {
		ObservableList<EntryView> result = FXCollections.observableArrayList();
		for (Entry current : this.getAccount().getLastAccountEntries(NumberOfShownEntries)) {
			result.add( EntryView.create(current));
		}
		return result;
	}
	
}
