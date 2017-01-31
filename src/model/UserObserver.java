package model;

public interface UserObserver {

	void handleNewAccount( Account account);
	void handleNewTransferOrTransaction(TransferOrTransaction t);
	void handleNewTransferInTransaction(Transaction transaction, Transfer transfer);
	void handleBookedTransferOrTransaction(TransferOrTransaction transferOrTransaction);

}
