package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class CreditsLimitExceededException extends CustomException {

	public CreditsLimitExceededException() {
		super(ErrorType.SEMESTER_CREDIT_EXCEED);
	}

}
