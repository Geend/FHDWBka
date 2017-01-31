package viewExceptions;

import model.AccountException;

@SuppressWarnings("serial")
public class AccountAlreadyShownException extends AccountException {

	private static final String AccountAlreadyShownMessage = "Acount with the following name is already shown: ";

	public AccountAlreadyShownException(String name) {
		super(AccountAlreadyShownMessage + name);
	}	

}
