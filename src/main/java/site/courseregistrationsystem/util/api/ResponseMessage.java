package site.courseregistrationsystem.util.api;

public enum ResponseMessage {

	// Auth
	LOGIN_SUCCESS("로그인에 성공했습니다"),

	// Lecture
	LECTURE_LIST_FETCH_SUCCESS("강의 목록 조회에 성공했습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
