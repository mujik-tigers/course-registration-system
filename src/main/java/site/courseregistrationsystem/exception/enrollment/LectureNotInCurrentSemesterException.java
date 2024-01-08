package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class LectureNotInCurrentSemesterException extends CustomException {

	public LectureNotInCurrentSemesterException() {
		super(ErrorType.LECTURE_NOT_IN_CURRENT_SEMESTER);
	}

}
