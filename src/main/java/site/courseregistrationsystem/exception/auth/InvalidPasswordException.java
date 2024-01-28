package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class InvalidPasswordException extends CustomException {

	public InvalidPasswordException() {
		super(ErrorType.PASSWORD_INVALID);
	}

}
