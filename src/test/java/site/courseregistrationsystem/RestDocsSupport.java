package site.courseregistrationsystem;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.application.AuthService;
import site.courseregistrationsystem.auth.application.SessionManager;
import site.courseregistrationsystem.auth.presentation.AuthController;
import site.courseregistrationsystem.auth.presentation.CookieProperties;
import site.courseregistrationsystem.basket.application.BasketService;
import site.courseregistrationsystem.basket.presentation.BasketController;
import site.courseregistrationsystem.clock.presentation.ClockController;
import site.courseregistrationsystem.enrollment.application.EnrollmentService;
import site.courseregistrationsystem.enrollment.presentation.EnrollmentController;
import site.courseregistrationsystem.exception.auth.NonexistenceSessionException;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.application.LectureService;
import site.courseregistrationsystem.lecture.presentation.LectureController;
import site.courseregistrationsystem.student.application.StudentService;
import site.courseregistrationsystem.student.presentation.StudentController;
import site.courseregistrationsystem.util.encryption.Aes256Manager;

@WebMvcTest(controllers = {
	AuthController.class,
	ClockController.class,
	StudentController.class,
	LectureController.class,
	BasketController.class,
	EnrollmentController.class
})
@AutoConfigureRestDocs
public abstract class RestDocsSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected CookieProperties cookieProperties;

	@MockBean
	protected AuthService authService;

	@MockBean
	protected StudentService studentService;

	@MockBean
	protected Aes256Manager aes256Manager;

	@MockBean
	protected SessionManager sessionManager;

	@MockBean
	protected LectureService lectureService;

	@MockBean
	protected EnrollmentService enrollmentService;

	@MockBean
	protected BasketService basketService;

	protected MockedStatic<Semester> semester;

	@BeforeEach
	void setUp() {
		String cookieName = "SESSIONID";
		String cookieDomain = "course-registration-system.site";
		String cookiePath = "/";
		int cookieExpiry = 3600;
		given(cookieProperties.getName()).willReturn(cookieName);
		given(cookieProperties.getDomain()).willReturn(cookieDomain);
		given(cookieProperties.getPath()).willReturn(cookiePath);
		given(cookieProperties.getExpiry()).willReturn(cookieExpiry);

		given(sessionManager.fetch(any(String.class)))
			.will(invocation -> {
				String sessionId = invocation.getArgument(0, String.class);

				if (sessionId.length() == 36) {
					return StudentSession.builder()
						.id(sessionId)
						.studentPk(1L)
						.expiration(3600L)
						.build();
				}

				throw new NonexistenceSessionException();
			});

		semester = mockStatic(Semester.class);
		given(Semester.getCurrentSemester())
			.willReturn(Semester.FIRST);
	}

	@AfterEach
	void close() {
		semester.close();
	}

}
