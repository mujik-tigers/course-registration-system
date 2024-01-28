package site.courseregistrationsystem.exception.clock;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class ClockNotFoundException extends CustomException {

	public ClockNotFoundException() {
		super(ErrorType.CLOCK_NONEXISTENT);
	}

}
