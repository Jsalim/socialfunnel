package exceptions;

public class InvalidParameterException extends GenericException{
	public InvalidParameterException(String errorMessage) {
		super(errorMessage);
	}
}
