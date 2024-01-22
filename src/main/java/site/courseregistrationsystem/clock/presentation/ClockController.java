package site.courseregistrationsystem.clock.presentation;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.clock.application.ClockService;
import site.courseregistrationsystem.clock.dto.CurrentServerTime;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.clock.dto.SessionRemainingTime;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;
import site.courseregistrationsystem.util.resolver.SessionTime;

@RestController
@RequiredArgsConstructor
public class ClockController {

	private final ClockService clockService;

	@GetMapping("/clock/server")
	public ApiResponse<CurrentServerTime> checkCurrentServerTime() {
		CurrentServerTime currentServerTime = new CurrentServerTime(LocalDateTime.now());

		return ApiResponse.ok(ResponseMessage.CURRENT_SERVER_TIME_FETCH_SUCCESS.getMessage(), currentServerTime);
	}

	@GetMapping("/clock/session")
	public ApiResponse<SessionRemainingTime> fetchSessionRemainingTime(@SessionTime Long sessionTime) {
		return ApiResponse.ok(ResponseMessage.SESSION_REMAINING_TIME_FETCH_SUCCESS.getMessage(), new SessionRemainingTime(sessionTime));
	}

	@GetMapping("/clock/current-year-and-semester")
	public ApiResponse<CurrentYearAndSemester> fetchCurrentYearAndSemester() {
		CurrentYearAndSemester currentYearAndSemester = clockService.fetchCurrentClock();

		return ApiResponse.ok(ResponseMessage.CURRENT_YEAR_AND_SEMESTER_FETCH_SUCCESS.getMessage(), currentYearAndSemester);
	}

}
