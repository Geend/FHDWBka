package model;

@SuppressWarnings("serial")
public class UserNameFormatException extends UserException {

	private static final String WrongUserNameFormatMessage = "This name cannot be used as an user name: ";

	public UserNameFormatException(String name) {
		super(WrongUserNameFormatMessage  + name);
	}

}
