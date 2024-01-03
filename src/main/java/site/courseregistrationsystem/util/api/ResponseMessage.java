package site.courseregistrationsystem.util.api;

import lombok.Getter;

@Getter
public enum ResponseMessage {

	// Auth
	LOGIN_SUCCESS("로그인에 성공했습니다"),
	LOGOUT_SUCCESS("로그아웃에 성공했습니다"),

	// Student
	STUDENT_INFORMATION_FETCH_SUCCESS("학생 정보 조회에 성공했습니다"),

	// Clock
	CURRENT_SERVER_TIME_FETCH_SUCCESS("현재 서버 시간 조회에 성공했습니다"),

	// Session
	RENEW_SESSION_DURATION_SUCCESS("세션 지속시간 갱신에 성공했습니다"),

	// Lecture
	LECTURE_SCHEDULE_FETCH_SUCCESS("강의 시간표 조회에 성공했습니다"),

	// Enrollment
	ENROLL_LECTURE_SUCCESS("수강 신청이 성공적으로 완료되었습니다"),
	ENROLLMENT_CANCEL_SUCCESS("수강 취소가 정상적으로 처리되었습니다");

	private final String message;

	ResponseMessage(String message) {
		this.message = message;
	}

}
