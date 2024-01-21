package site.courseregistrationsystem.exception.registration_period;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class StartTimeBeforeCurrentTimeException extends CustomException {

	public StartTimeBeforeCurrentTimeException() {
		super(ErrorType.START_TIME_BEFORE_CURRENT_TIME);
	}

}
