package site.courseregistrationsystem.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Auth
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "유효하지 않은 비밀번호입니다"),
	NONEXISTENT_STUDENT_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 학번입니다"),
	NONEXISTENT_SESSION(HttpStatus.BAD_REQUEST, "존재하지 않는 세션입니다"),
	NONEXISTENT_SESSION_COOKIE(HttpStatus.UNAUTHORIZED, "세션 쿠키가 없습니다"),

	// Student
	NONEXISTENT_STUDENT(HttpStatus.BAD_REQUEST, "존재하지 않는 학생입니다"),

	// Lecture
	NONEXISTENT_LECTURE(HttpStatus.BAD_REQUEST, "존재하지 않는 강의입니다"),

	// Basket
	DUPLICATE_BASKET(HttpStatus.BAD_REQUEST, "이미 수강바구니로 담은 강의입니다"),
	EXCEEDED_CREDIT_EXCEPTION(HttpStatus.BAD_REQUEST, "수강바구니로 담을 수 있는 학점을 초과하였습니다"),

	// AES256
	AES256_SETTING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 설정에 실패했습니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
