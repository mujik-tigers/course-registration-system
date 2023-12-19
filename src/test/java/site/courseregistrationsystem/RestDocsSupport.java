package site.courseregistrationsystem;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.courseregistrationsystem.auth.application.AuthService;
import site.courseregistrationsystem.auth.application.SessionManager;
import site.courseregistrationsystem.auth.presentation.AuthController;
import site.courseregistrationsystem.auth.presentation.CookieProperties;
import site.courseregistrationsystem.clock.presentation.ClockController;
import site.courseregistrationsystem.util.encryption.Aes256Manager;

@WebMvcTest(controllers = {AuthController.class, ClockController.class})
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
	protected Aes256Manager aes256Manager;

	@MockBean
	protected SessionManager sessionManager;

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
	}

}
