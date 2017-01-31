package model;

@SuppressWarnings("serial")
public class AccountNotFoundException extends AccountException {

	private static final String AccountNotFoundMessage = "Following account cannot be found: ";

	public AccountNotFoundException(String name) {
		super(AccountNotFoundMessage + name);
	}

}
