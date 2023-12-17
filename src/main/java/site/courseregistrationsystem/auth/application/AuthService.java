package site.courseregistrationsystem.auth.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.exception.auth.InvalidLoginException;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.Aes256Manager;
import site.courseregistrationsystem.util.encryption.BCryptManager;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final StudentRepository studentRepository;
	private final Aes256Manager aes256Manager;
	private final SessionManager sessionManager;

	public StudentSession login(LoginForm loginForm) {
		String encryptedStudentId = aes256Manager.encrypt(loginForm.getStudentId());
		String encryptedPassword = BCryptManager.encrypt(loginForm.getPassword());
		Student student = studentRepository.findByLoginForm(encryptedStudentId, encryptedPassword)
			.orElseThrow(InvalidLoginException::new);

		return sessionManager.generate(student.getId());
	}

}
