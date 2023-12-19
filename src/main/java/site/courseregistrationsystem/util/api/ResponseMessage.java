package site.courseregistrationsystem.util.api;

public enum ResponseMessage {

	// Student
	LOGIN_SUCCESS("로그인에 성공했습니다"),

	// Time
	CURRENT_SERVER_TIME_FETCH_SUCCESS("현재 서버 시간 조회에 성공했습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
