package model;

import java.util.List;

import de.fhdw.ml.transactionFramework.typesAndCollections.LinkedListType;
import de.fhdw.ml.transactionFramework.typesAndCollections.List_Transactional;

public class Transaction implements TransferOrTransaction {
	
	private static final long serialVersionUID = 1L;

	protected static int nextTransactionNumber = 1;
	private final int number;

	public static Transaction create() {
		return new Transaction(nextTransactionNumber++);
	}

	private List_Transactional<Transfer> transfers;
	
	private Transaction(int number) {
		this.number = number;
		this.transfers = new List_Transactional<Transfer>(new LinkedListType<Transfer>());
	}
	public int getNumber(){
		return this.number;
	}
	public void addTransfer(Transfer transfer) {
		this.transfers.add(transfer);
	}
	public List<Transfer> getTransfers() {
		return this.transfers;
	}
	@Override
	public void book() {
		for (Transfer current : this.transfers) {
			current.book();
		}
	}
	@Override
	public void accept(TransferOrTransactionVisitor visitor) {
		visitor.handleTransaction(this);		
	}
}
