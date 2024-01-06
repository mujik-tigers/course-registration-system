package site.courseregistrationsystem.enrollment.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.enrollment.application.EnrollmentService;
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectures;
import site.courseregistrationsystem.enrollment.dto.EnrollmentCapacity;
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
		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.ENROLL_LECTURE_SUCCESS.getMessage(),
			enrollmentService.enrollLecture(studentPk, lectureId));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/fast/{lectureNumber}")
	public ApiResponse<EnrolledLecture> enrollLectureByNumber(@Login Long studentPk,
		@PathVariable Integer lectureNumber) {
		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.ENROLL_LECTURE_SUCCESS.getMessage(),
			enrollmentService.enrollLectureByNumber(studentPk, lectureNumber));
	}

	@DeleteMapping("/{enrollmentId}")
	public ApiResponse<Void> cancelEnrollment(@Login Long studentPk, @PathVariable Long enrollmentId) {
		enrollmentService.cancel(studentPk, enrollmentId);

		return ApiResponse.ok(ResponseMessage.ENROLLMENT_CANCEL_SUCCESS.getMessage(), null);
	}

	@GetMapping
	public ApiResponse<EnrolledLectures> fetchEnrollments(@Login Long studentPk) {
		return ApiResponse.ok(ResponseMessage.ENROLLMENT_FETCH_SUCCESS.getMessage(),
			enrollmentService.fetchAll(studentPk));
	}

	@GetMapping("/{lectureId}/enrollment-count")
	public ApiResponse<EnrollmentCapacity> fetchEnrollmentCapacity(@PathVariable Long lectureId) {
		return ApiResponse.ok(ResponseMessage.ENROLLMENT_CAPACITY_FETCH_SUCCESS.getMessage(),
			enrollmentService.fetchCountBy(lectureId));
	}

}
