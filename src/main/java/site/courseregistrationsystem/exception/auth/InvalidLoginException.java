package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class InvalidLoginException extends CustomException {

	public InvalidLoginException() {
		super(ErrorType.INVALID_LOGIN_REQUEST);
	}

}
