package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class SessionNotFoundException extends CustomException {

	public SessionNotFoundException() {
		super(ErrorType.SESSION_NONEXISTENT);
	}

}
