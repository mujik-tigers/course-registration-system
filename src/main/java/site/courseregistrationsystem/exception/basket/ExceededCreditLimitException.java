package site.courseregistrationsystem.exception.basket;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class ExceededCreditLimitException extends CustomException {

	public ExceededCreditLimitException() {
		super(ErrorType.EXCEEDED_CREDIT_EXCEPTION);
	}

}
