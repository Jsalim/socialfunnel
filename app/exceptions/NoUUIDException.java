package exceptions;

/**
 * This exception is thrown whenever a {@link UserService#getUserSession(play.mvc.Http.Session)} method is called and there's no session uuid on the session object
 * */
public class NoUUIDException extends GenericException{
	
	public NoUUIDException(String errorMessage) {
		super(errorMessage);
	}
}
