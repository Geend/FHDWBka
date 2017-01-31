package viewModel;

public interface TransferOrTransactionViewVisitor {

	void handleTransactionView(TransactionView transactionView);
	void handleTransferView(TransferView transferView);

}
