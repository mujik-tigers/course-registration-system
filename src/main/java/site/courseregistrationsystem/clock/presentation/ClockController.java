package site.courseregistrationsystem.clock.presentation;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import site.courseregistrationsystem.clock.dto.CurrentServerTime;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
public class ClockController {

	@GetMapping("/clock/server")
	public ApiResponse<CurrentServerTime> checkCurrentServerTime() {
		CurrentServerTime currentServerTime = new CurrentServerTime(LocalDateTime.now());
		return ApiResponse.ok(ResponseMessage.CURRENT_SERVER_TIME_FETCH_SUCCESS.getMessage(), currentServerTime);
	}

}
