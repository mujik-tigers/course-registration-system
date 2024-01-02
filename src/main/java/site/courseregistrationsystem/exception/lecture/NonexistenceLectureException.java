package site.courseregistrationsystem.exception.lecture;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceLectureException extends CustomException {

	public NonexistenceLectureException() {
		super(ErrorType.NONEXISTENT_LECTURE);
	}

}
