package model;

@SuppressWarnings("serial")
public class AccountNameFormatException extends AccountException {

	private static final String WrongAccountNameFormatMessage = "This name cannot be used as an account name: ";

	public AccountNameFormatException(String name) {
		super(WrongAccountNameFormatMessage  + name);
	}
}
