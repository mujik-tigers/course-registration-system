package site.courseregistrationsystem.student.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.student.application.StudentService;
import site.courseregistrationsystem.student.dto.LoginForm;
import site.courseregistrationsystem.student.dto.StudentSession;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@PostMapping
	public ApiResponse<StudentSession> login(@RequestBody LoginForm loginForm) {
		StudentSession studentSession = studentService.login(loginForm);

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.LOGIN_SUCCESS.getMessage(), studentSession);
	}

}
