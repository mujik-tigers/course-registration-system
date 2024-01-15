package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class InvalidBasketTimeException extends CustomException {

	public InvalidBasketTimeException() {
		super(ErrorType.BASKET_REGISTRATION_PERIOD_INVALID_TIME);
	}

}
