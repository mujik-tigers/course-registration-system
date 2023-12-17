package site.courseregistrationsystem.auth.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final StudentRepository studentRepository;
	private final SessionManager sessionManager;

	public StudentSession login(LoginForm loginForm) {
		Student student = studentRepository.findByLoginForm(loginForm.getStudentId(), loginForm.getPassword())
			.orElseThrow();

		return sessionManager.generate(student.getId());
	}

}
