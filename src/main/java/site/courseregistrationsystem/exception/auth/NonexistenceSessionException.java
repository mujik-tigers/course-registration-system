package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceSessionException extends CustomException {

	public NonexistenceSessionException() {
		super(ErrorType.NONEXISTENT_SESSION);
	}

}
