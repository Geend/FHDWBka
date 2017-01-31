package model;

import de.fhdw.ml.transactionFramework.typesAndCollections.Object_Transactional;

public interface TransferOrTransaction extends Object_Transactional {
	
	/** Removes the receiver's <amount> from the receiver's <fromAccount> (debit)
	 *  and adds the receiver's <amount> to the receiver's <toAccount> (credit).
	 * @throws AccountException if the booking violates account limits. 
	 */
	public void book() throws AccountException ;

	public void accept(TransferOrTransactionVisitor visitor);

}
