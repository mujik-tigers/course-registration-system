package site.courseregistrationsystem.exception.enrollment;

import site.courseregistrationsystem.exception.CustomException;
import site.courseregistrationsystem.exception.ErrorType;

public class LectureApplicantsLimitExceededException extends CustomException {

	public LectureApplicantsLimitExceededException() {
		super(ErrorType.LECTURE_APPLICANTS_LIMIT_EXCEEDED);
	}

}
