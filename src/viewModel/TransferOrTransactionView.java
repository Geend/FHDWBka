package viewModel;

import model.TransferOrTransaction;

public interface TransferOrTransactionView {

	void accept(TransferOrTransactionViewVisitor visitor);

	TransferOrTransaction getTransferOrTransaction();

	void deregister();

}
