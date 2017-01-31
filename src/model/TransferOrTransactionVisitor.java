package model;

public interface TransferOrTransactionVisitor {

	void handleTransaction(Transaction transaction);

	void handleTransfer(Transfer transfer);

}
