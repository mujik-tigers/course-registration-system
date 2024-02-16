package site.courseregistrationsystem.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Auth
	PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 비밀번호입니다"),
	STUDENT_ID_NONEXISTENT(HttpStatus.BAD_REQUEST, "존재하지 않는 학번입니다"),
	SESSION_NONEXISTENT(HttpStatus.BAD_REQUEST, "존재하지 않는 세션입니다"),
	SESSION_COOKIE_NONEXISTENT(HttpStatus.UNAUTHORIZED, "세션 쿠키가 없습니다"),
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "해당 작업에 대한 권한이 없습니다"),

	// Student
	STUDENT_NONEXISTENT(HttpStatus.BAD_REQUEST, "존재하지 않는 학생입니다"),

	// Lecture
	LECTURE_NONEXISTENT(HttpStatus.BAD_REQUEST, "존재하지 않는 강의입니다"),

	// Basket
	BASKET_DUPLICATION(HttpStatus.BAD_REQUEST, "이미 수강바구니로 담은 강의입니다"),
	BASKET_NONEXISTENT(HttpStatus.BAD_REQUEST, "수강바구니에 담지 않아 존재하지 않는 강의입니다"),

	// Credit
	CREDIT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "학기 내 신청 가능한 최대 학점을 초과했습니다"),

	// Schedule
	SCHEDULE_CONFLICT(HttpStatus.BAD_REQUEST, "다른 수업과 시간이 겹칩니다"),

	// AES256
	AES256_SETTING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 설정에 실패했습니다"),

	// Semester
	SEMESTER_INVALID(HttpStatus.BAD_REQUEST, "수강신청 기간이 아닙니다"),

	// Registration Period
	START_TIME_BEFORE_CURRENT_TIME(HttpStatus.BAD_REQUEST, "시작 시간이 현재 시간보다 앞에 있습니다"),
	START_TIME_AFTER_END_TIME(HttpStatus.BAD_REQUEST, "시작 시간이 종료 시간보다 뒤에 있습니다"),
	ENROLLMENT_REGISTRATION_PERIOD_NONEXISTENT(HttpStatus.BAD_REQUEST, "수강 신청 기간이 없습니다"),
	ENROLLMENT_REGISTRATION_PERIOD_INVALID_TIME(HttpStatus.BAD_REQUEST, "지금은 수강 신청 기간이 아닙니다"),
	BASKET_REGISTRATION_PERIOD_NONEXISTENT(HttpStatus.BAD_REQUEST, "등록된 수강 바구니 기간이 없습니다"),
	BASKET_REGISTRATION_PERIOD_INVALID_TIME(HttpStatus.BAD_REQUEST, "지금은 수강 바구니 신청 기간이 아닙니다"),

	// Enrollment
	ENROLLMENT_DUPLICATION(HttpStatus.BAD_REQUEST, "중복된 과목을 수강 신청할 수 없습니다"),
	ENROLLMENT_NONEXISTENT(HttpStatus.BAD_REQUEST, "수강 신청 내역이 존재하지 않습니다"),
	LECTURE_NOT_IN_CURRENT_SEMESTER(HttpStatus.BAD_REQUEST, "현재 학기에 개강하는 강의가 아닙니다"),
	LECTURE_APPLICANTS_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "강의 정원을 초과했습니다"),

	// Clock
	CLOCK_NONEXISTENT(HttpStatus.BAD_REQUEST, "등록된 현재 년도-학기 정보가 없습니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
