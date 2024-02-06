package site.courseregistrationsystem.exception.lecture;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class LectureNotFoundException extends CustomException {

	public LectureNotFoundException() {
		super(ErrorType.LECTURE_NONEXISTENT);
	}

}
