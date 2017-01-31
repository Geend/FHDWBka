package model;

@SuppressWarnings("serial")
abstract public class AccountException extends Exception {

	public AccountException(String message) {
		super(message);
	}
}
