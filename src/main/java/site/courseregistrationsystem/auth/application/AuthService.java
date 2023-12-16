package site.courseregistrationsystem.auth.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final StudentRepository studentRepository;

	public StudentSession login(LoginForm loginForm) {
		return null;
	}

}
