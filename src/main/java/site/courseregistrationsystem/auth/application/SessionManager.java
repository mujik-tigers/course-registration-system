package site.courseregistrationsystem.auth.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.infrastructure.SessionStorage;

@Component
@RequiredArgsConstructor
public class SessionManager {

	private static final Long SESSION_EXPIRY = 3600L;  // 60분

	private final SessionStorage sessionStorage;

	public StudentSession generate(Long studentPk) {  // 세션 생성 및 저장
		String sessionId = UUID.randomUUID().toString();
		StudentSession session = new StudentSession(sessionId, studentPk, SESSION_EXPIRY);

		return sessionStorage.save(session);
	}

	public StudentSession fetch(String sessionId) {  // 세션 조회
		return sessionStorage.findById(sessionId).orElseThrow();
	}

	public void invalidate(String sessionId) {  // 세션 무효화
		sessionStorage.deleteById(sessionId);
	}

	public StudentSession renew(String sessionId) {  // 세션 갱신
		StudentSession session = fetch(sessionId);
		StudentSession newSession = generate(session.getStudentPk());

		invalidate(sessionId);

		return newSession;
	}

}
