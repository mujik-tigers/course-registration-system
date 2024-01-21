package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class StartTimeAfterEndTimeException extends CustomException {

	public StartTimeAfterEndTimeException() {
		super(ErrorType.START_TIME_AFTER_END_TIME);
	}

}
