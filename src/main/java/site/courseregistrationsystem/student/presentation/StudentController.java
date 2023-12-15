package site.courseregistrationsystem.student.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.application.StudentService;
import site.courseregistrationsystem.student.dto.LoginForm;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

	private static final String SESSION_KEY = "studentPK";

	private final StudentService studentService;

	@PostMapping("/login")
	public ApiResponse<Void> login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
		Student student = studentService.login(loginForm);  // 로그인 데이터 검증

		request.getSession().invalidate();  // 세션 파기
		HttpSession session = request.getSession();  // 세션 생성
		session.setAttribute(SESSION_KEY, student.getId());  // 세션에 studentPK 설정
		session.setMaxInactiveInterval(3600);  // 60분동안 세션 유지

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.LOGIN_SUCCESS.getMessage(), null);
	}

}
