package site.courseregistrationsystem.auth.presentation;

import static site.courseregistrationsystem.util.ProjectConstant.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.application.SessionManager;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class SessionController {

	private final SessionManager sessionManager;
	private final CookieProperties cookieProperties;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/session")
	public ApiResponse<Void> renewSessionDuration(@CookieValue(value = SESSION_ID) Cookie sessionCookie, HttpServletResponse response) {
		StudentSession renewSession = sessionManager.renew(sessionCookie.getValue());

		Cookie cookie = new Cookie(cookieProperties.getName(), renewSession.getId());
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setDomain(cookieProperties.getDomain());
		cookie.setPath(cookieProperties.getPath());
		cookie.setMaxAge(cookieProperties.getExpiry());

		response.addCookie(cookie); // 쿠키 설정

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.RENEW_SESSION_DURATION_SUCCESS.getMessage(), null);
	}

}
