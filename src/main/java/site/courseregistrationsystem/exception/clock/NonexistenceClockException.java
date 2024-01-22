package site.courseregistrationsystem.exception.clock;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceClockException extends CustomException {

	public NonexistenceClockException() {
		super(ErrorType.CLOCK_NONEXISTENT);
	}

}
