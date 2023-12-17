package site.courseregistrationsystem.auth.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import site.courseregistrationsystem.auth.StudentSession;

@Component
public class SessionManager {

	public StudentSession generate(Long studentPk) {
		String sessionId = UUID.randomUUID().toString();

		return new StudentSession(sessionId, studentPk);
	}

}
