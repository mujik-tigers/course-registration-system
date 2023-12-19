package site.courseregistrationsystem.student.presentation;

import static site.courseregistrationsystem.util.api.ResponseMessage.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.student.application.StudentService;
import site.courseregistrationsystem.student.dto.StudentInformation;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.resolver.Login;

@RestController
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@GetMapping("/students")
	public ApiResponse<StudentInformation> fetchStudentInformation(@Login Long studentPK) {
		StudentInformation studentInformation = studentService.fetchStudentInformation(studentPK);

		return ApiResponse.ok(STUDENT_INFORMATION_FETCH_SUCCESS.getMessage(), studentInformation);
	}

}
