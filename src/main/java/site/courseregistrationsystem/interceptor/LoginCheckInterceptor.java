package site.courseregistrationsystem.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.application.SessionManager;
import site.courseregistrationsystem.auth.presentation.CookieProperties;
import site.courseregistrationsystem.exception.auth.NonexistenceSessionCookieException;
import site.courseregistrationsystem.util.ProjectConstant;

@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

	private final SessionManager sessionManager;
	private final CookieProperties cookieProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (CorsUtils.isPreFlightRequest(request))
			return true;

		Cookie sessionCookie = findSessionCookie(request.getCookies());
		StudentSession session = sessionManager.fetch(sessionCookie.getValue());
		request.setAttribute(ProjectConstant.STUDENT_PK, session.getStudentPk());
		request.setAttribute(ProjectConstant.SESSION_TIME, session.getExpiration());

		return true;
	}

	private Cookie findSessionCookie(Cookie[] cookies) {
		if (cookies == null) {
			throw new NonexistenceSessionCookieException();
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieProperties.getName())) {
				return cookie;
			}
		}

		throw new NonexistenceSessionCookieException();
	}

}
