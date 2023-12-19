package site.courseregistrationsystem.auth.presentation;

import org.springframework.http.HttpStatus;
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
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final CookieProperties cookieProperties;
	private final AuthService authService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/login")
	public ApiResponse<Void> login(@RequestBody @Valid LoginForm loginForm, HttpServletResponse response) {
		StudentSession session = authService.login(loginForm);  // 로그인 데이터 검증

		Cookie cookie = new Cookie(cookieProperties.getName(), session.getId());  // 쿠키 생성
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setDomain(cookieProperties.getDomain());
		cookie.setPath(cookieProperties.getPath());
		cookie.setMaxAge(cookieProperties.getExpiry());

		response.addCookie(cookie); // 쿠키 설정

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.LOGIN_SUCCESS.getMessage(), null);
	}

}







