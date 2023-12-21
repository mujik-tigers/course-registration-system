package site.courseregistrationsystem.util.api;

public enum ResponseMessage {

	// Student
	LOGIN_SUCCESS("로그인에 성공했습니다"),
	STUDENT_INFORMATION_FETCH_SUCCESS("학생 정보 조회에 성공했습니다"),

	// Time
	CURRENT_SERVER_TIME_FETCH_SUCCESS("현재 서버 시간 조회에 성공했습니다"),

	// Session
	RENEW_SESSION_DURATION_SUCCESS("세션 지속시간 갱신에 성공했습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
