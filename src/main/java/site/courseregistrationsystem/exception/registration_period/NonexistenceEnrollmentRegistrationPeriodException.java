package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceEnrollmentRegistrationPeriodException extends CustomException {

	public NonexistenceEnrollmentRegistrationPeriodException() {
		super(ErrorType.ENROLLMENT_REGISTRATION_PERIOD_NONEXISTENT);
	}

}
