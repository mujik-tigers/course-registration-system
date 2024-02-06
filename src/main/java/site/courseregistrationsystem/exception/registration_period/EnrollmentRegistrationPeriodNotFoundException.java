package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class EnrollmentRegistrationPeriodNotFoundException extends CustomException {

	public EnrollmentRegistrationPeriodNotFoundException() {
		super(ErrorType.ENROLLMENT_REGISTRATION_PERIOD_NONEXISTENT);
	}

}
