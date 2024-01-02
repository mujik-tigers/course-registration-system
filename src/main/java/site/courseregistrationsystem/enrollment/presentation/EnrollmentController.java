package site.courseregistrationsystem.enrollment.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;
import site.courseregistrationsystem.util.resolver.Login;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

	private final EnrollmentService enrollmentService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/{lectureId}")
	public ApiResponse<EnrolledLecture> enrollLecture(@Login Long studentPk, @PathVariable Long lectureId) {
		EnrolledLecture enrolledLecture = new EnrolledLecture(enrollmentService.enrollLecture(studentPk, lectureId));
		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.ENROLL_LECTURE_SUCCESS.getMessage(), enrolledLecture);
	}

}
