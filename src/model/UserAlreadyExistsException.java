package model;

@SuppressWarnings("serial")
public class UserAlreadyExistsException extends UserException {

	private static final String UserAlreadyExistsMessage = "The following name has already been used for an user: ";

	public UserAlreadyExistsException(String name) {
		super( UserAlreadyExistsMessage + name);
	}


}
