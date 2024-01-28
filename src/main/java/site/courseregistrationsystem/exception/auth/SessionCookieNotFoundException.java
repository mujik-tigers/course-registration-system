package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class SessionCookieNotFoundException extends CustomException {

	public SessionCookieNotFoundException() {
		super(ErrorType.SESSION_COOKIE_NONEXISTENT);
	}

}
