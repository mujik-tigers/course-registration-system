package site.courseregistrationsystem.auth.presentation;

import static site.courseregistrationsystem.util.ProjectConstant.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.application.AuthService;
import site.courseregistrationsystem.auth.application.SessionManager;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final CookieProperties cookieProperties;
	private final AuthService authService;
	private final SessionManager sessionManager;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/login")
	public ApiResponse<Void> login(@RequestBody @Valid LoginForm loginForm, HttpServletResponse response) {
		StudentSession session = authService.login(loginForm);

		Cookie cookie = generateCookieBy(session);
		response.addCookie(cookie);

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.LOGIN_SUCCESS.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/session")
	public ApiResponse<Void> renewSession(@CookieValue(value = SESSION_ID) Cookie sessionCookie,
		HttpServletResponse response) {
		StudentSession renewSession = sessionManager.renew(sessionCookie.getValue());

		Cookie cookie = generateCookieBy(renewSession);
		response.addCookie(cookie);

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.SESSION_RENEW_SUCCESS.getMessage(), null);
	}

	@DeleteMapping("/logout")
	public ApiResponse<Void> logout(@CookieValue(value = SESSION_ID) Cookie sessionCookie,
		HttpServletResponse response) {
		sessionManager.invalidate(sessionCookie.getValue());

		invalidateCookie(sessionCookie);
		response.addCookie(sessionCookie);

		return ApiResponse.ok(ResponseMessage.LOGOUT_SUCCESS.getMessage(), null);
	}

	private Cookie generateCookieBy(StudentSession renewSession) {
		Cookie cookie = new Cookie(cookieProperties.getName(), renewSession.getId());
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setDomain(cookieProperties.getDomain());
		cookie.setPath(cookieProperties.getPath());
		cookie.setMaxAge(cookieProperties.getExpiry());

		return cookie;
	}

	private void invalidateCookie(Cookie sessionCookie) {
		sessionCookie.setHttpOnly(true);
		sessionCookie.setSecure(true);
		sessionCookie.setDomain(cookieProperties.getDomain());
		sessionCookie.setPath(cookieProperties.getPath());
		sessionCookie.setMaxAge(0);
	}

}







