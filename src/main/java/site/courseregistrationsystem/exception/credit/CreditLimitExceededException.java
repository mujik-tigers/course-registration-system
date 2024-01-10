package site.courseregistrationsystem.exception.credit;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class CreditLimitExceededException extends CustomException {

	public CreditLimitExceededException() {
		super(ErrorType.CREDIT_LIMIT_EXCEEDED);
	}

}
