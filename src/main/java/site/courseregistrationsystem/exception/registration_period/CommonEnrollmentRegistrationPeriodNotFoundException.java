package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class CommonEnrollmentRegistrationPeriodNotFoundException extends CustomException {

	public CommonEnrollmentRegistrationPeriodNotFoundException() {
		super(ErrorType.COMMON_ENROLLMENT_REGISTRATION_PERIOD_NONEXISTENT);
	}

}
