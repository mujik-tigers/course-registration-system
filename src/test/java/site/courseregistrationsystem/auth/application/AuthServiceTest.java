package site.courseregistrationsystem.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.exception.ErrorType;
import site.courseregistrationsystem.exception.auth.InvalidPasswordException;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.Aes256Manager;
import site.courseregistrationsystem.util.encryption.BCryptManager;

class AuthServiceTest extends IntegrationTestSupport {

	@Autowired
	private AuthService authService;

	@Autowired
	private Aes256Manager aes256Manager;

	@Autowired
	private SessionManager sessionManager;

	@Autowired
	private StudentRepository studentRepository;

	@Test
	@DisplayName("학번과 비밀번호를 통해 로그인에 성공하면 세션을 생성한다")
	void loginSuccess() {
		// given
		String studentId = "123456789";
		String password = "test1234!";

		Student saved = saveStudent(studentId, password);
		LoginForm loginForm = createLoginForm(studentId, password);

		// when
		StudentSession session = authService.login(loginForm);
		StudentSession fetched = sessionManager.fetch(session.getId());

		// then
		assertThat(session.getStudentPk()).isEqualTo(saved.getId());
		assertThat(fetched.getStudentPk()).isEqualTo(session.getStudentPk());
		assertThat(fetched.getId()).isEqualTo(session.getId());
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않는 경우 로그인에 실패한다")
	void loginFail() {
		// given
		String studentId = "123456789";
		String password = "test1234!";
		String wrongPassword = "test0123!";

		Student saved = saveStudent(studentId, password);
		LoginForm loginForm = createLoginForm(studentId, wrongPassword);

		// when / then
		assertThatThrownBy(() -> authService.login(loginForm))
			.isInstanceOf(InvalidPasswordException.class)
			.hasMessage(ErrorType.INVALID_PASSWORD.getMessage());
	}

	private static LoginForm createLoginForm(String studentId, String password) {
		return LoginForm.builder()
			.studentId(studentId)
			.password(password)
			.build();
	}

	private Student saveStudent(String studentId, String password) {
		String encryptedStudentId = aes256Manager.encrypt(studentId);
		String encryptedPassword = BCryptManager.encrypt(password);

		return studentRepository.save(new Student(encryptedStudentId, encryptedPassword));
	}

}
