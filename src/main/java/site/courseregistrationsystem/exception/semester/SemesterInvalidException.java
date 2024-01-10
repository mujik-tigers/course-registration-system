package site.courseregistrationsystem.exception.semester;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class SemesterInvalidException extends CustomException {

	public SemesterInvalidException() {
		super(ErrorType.SEMESTER_INVALID);
	}

}
