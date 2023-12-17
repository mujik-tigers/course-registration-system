package site.courseregistrationsystem.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {

	// Auth
	INVALID_LOGIN_REQUEST(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 정보입니다"),

	// AES256
	AES256_SETTING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 설정에 실패했습니다");

	private final HttpStatus status;
	private final String message;

	ErrorType(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}
