package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class UnauthorizedAccessException extends CustomException {

	public UnauthorizedAccessException() {
		super(ErrorType.UNAUTHORIZED_ACCESS);
	}

}
