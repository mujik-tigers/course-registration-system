package site.courseregistrationsystem.exception.student;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class StudentNotFoundException extends CustomException {

	public StudentNotFoundException() {
		super(ErrorType.STUDENT_NONEXISTENT);
	}

}
