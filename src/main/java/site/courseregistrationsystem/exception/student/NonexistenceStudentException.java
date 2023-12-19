package site.courseregistrationsystem.exception.student;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class NonexistenceStudentException extends CustomException {

	public NonexistenceStudentException() {
		super(ErrorType.NONEXISTENT_STUDENT);
	}

}
