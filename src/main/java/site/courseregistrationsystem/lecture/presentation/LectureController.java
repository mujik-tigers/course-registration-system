package site.courseregistrationsystem.lecture.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.lecture.application.LectureService;
import site.courseregistrationsystem.lecture.dto.LectureDetailPage;
import site.courseregistrationsystem.subject.SubjectDivision;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class LectureController {

	private final LectureService lectureService;

	@GetMapping("/lectures")
	public ApiResponse<LectureDetailPage> fetch(Pageable pageable,
		@RequestParam(required = false) SubjectDivision subjectDivision,
		@RequestParam(required = false) Long departmentId,
		@RequestParam(required = false) String subjectName) {
		return ApiResponse.ok(ResponseMessage.LECTURE_LIST_FETCH_SUCCESS.getMessage(),
			lectureService.fetch(pageable, subjectDivision, departmentId, subjectName));
	}

}
