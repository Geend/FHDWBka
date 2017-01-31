package model;

@SuppressWarnings("serial")
public class UserNotFoundException extends UserException {
	private static final String UserNotFoundMessage =  "There is no user with name: ";

	public UserNotFoundException(String name) {
		super( UserNotFoundMessage + name);
	}

}
