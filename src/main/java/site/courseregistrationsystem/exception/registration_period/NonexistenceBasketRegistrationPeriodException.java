package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceBasketRegistrationPeriodException extends CustomException {

	public NonexistenceBasketRegistrationPeriodException() {
		super(ErrorType.BASKET_REGISTRATION_PERIOD_NONEXISTENT);
	}

}
