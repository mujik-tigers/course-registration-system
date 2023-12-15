package site.courseregistrationsystem.util.api;

public enum ResponseMessage {

	// Student
	LOGIN_SUCCESS("로그인에 성공했습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
