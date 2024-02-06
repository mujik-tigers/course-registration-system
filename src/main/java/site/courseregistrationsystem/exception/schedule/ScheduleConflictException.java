package site.courseregistrationsystem.exception.schedule;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class ScheduleConflictException extends CustomException {

	public ScheduleConflictException() {
		super(ErrorType.SCHEDULE_CONFLICT);
	}

}
