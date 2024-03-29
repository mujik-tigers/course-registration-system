package site.courseregistrationsystem.lecture.presentation;

import java.time.Year;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.application.LectureService;
import site.courseregistrationsystem.lecture.dto.BasketStoringCount;
import site.courseregistrationsystem.lecture.dto.LectureFilterOptions;
import site.courseregistrationsystem.lecture.dto.LectureSchedulePage;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class LectureController {

	private final LectureService lectureService;

	@GetMapping("/lectures")
	public ApiResponse<LectureSchedulePage> fetchLectureSchedule(
		@PageableDefault(size = 20) Pageable pageable, @Valid LectureFilterOptions lectureFilterOptions) {
		return ApiResponse.ok(ResponseMessage.LECTURE_SCHEDULE_FETCH_SUCCESS.getMessage(),
			lectureService.fetchLectureSchedule(pageable, lectureFilterOptions));
	}

	@GetMapping("/lectures/{lectureId}/basket-count")
	public ApiResponse<BasketStoringCount> fetchBasketStoringCount(@PathVariable Long lectureId) {
		BasketStoringCount basketStoringCount = lectureService.fetchBasketStoringCount(Year.now(), Semester.getCurrentSemester(), lectureId);
		// TODO: 현재 년도, 현재 학기를 어떤식으로 계산할 지? 혹은 저장된 값을 조회할 지? 결정하면 좋을 듯

		return ApiResponse.ok(ResponseMessage.BASKET_STORING_COUNT_FETCH_SUCCESS.getMessage(), basketStoringCount);
	}

}
