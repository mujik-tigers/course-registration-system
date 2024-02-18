package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class DuplicateSubjectException extends CustomException {

	public DuplicateSubjectException() {
		super(ErrorType.SUBJECT_DUPLICATION);
	}

}
