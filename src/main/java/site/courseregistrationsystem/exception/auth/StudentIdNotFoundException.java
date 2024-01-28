package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class StudentIdNotFoundException extends CustomException {

	public StudentIdNotFoundException() {
		super(ErrorType.STUDENT_ID_NONEXISTENT);
	}

}
