package model;

@SuppressWarnings("serial")
public class AccountAlreadyExistsException extends AccountException {

	private static final String AccountAlreadyExistsMessage = "The following name has been used for an account: ";

	public AccountAlreadyExistsException(String name) {
		super(AccountAlreadyExistsMessage + name);
	}

}
