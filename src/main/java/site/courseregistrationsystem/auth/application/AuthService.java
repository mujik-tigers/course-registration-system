package site.courseregistrationsystem.auth.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.BCryptManager;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final StudentRepository studentRepository;
	private final SessionManager sessionManager;

	public StudentSession login(LoginForm loginForm) {
		String encryptedPassword = BCryptManager.encrypt(loginForm.getPassword());
		Student student = studentRepository.findByLoginForm(loginForm.getStudentId(), encryptedPassword)
			.orElseThrow();

		return sessionManager.generate(student.getId());
	}

}
