package site.courseregistrationsystem.exception.auth;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceStudentIdException extends CustomException {

	public NonexistenceStudentIdException() {
		super(ErrorType.NONEXISTENT_STUDENT_ID);
	}

}
