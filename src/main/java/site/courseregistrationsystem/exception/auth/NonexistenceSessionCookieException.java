package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceSessionCookieException extends CustomException {

	public NonexistenceSessionCookieException() {
		super(ErrorType.NONEXISTENT_SESSION_COOKIE);
	}

}
