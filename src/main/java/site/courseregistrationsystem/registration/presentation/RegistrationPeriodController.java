package site.courseregistrationsystem.registration.presentation;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.registration.BasketRegistrationPeriod;
import site.courseregistrationsystem.registration.application.BasketRegistrationPeriodService;
import site.courseregistrationsystem.registration.application.EnrollmentRegistrationPeriodService;
import site.courseregistrationsystem.registration.dto.BasketRegistrationPeriodSaveForm;
import site.courseregistrationsystem.registration.dto.EnrollmentRegistrationPeriodSaveForm;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class RegistrationPeriodController {

	private final BasketRegistrationPeriodService basketRegistrationPeriodService;
	private final EnrollmentRegistrationPeriodService enrollmentRegistrationPeriodService;

	@GetMapping("/registration-period/baskets")
	public ApiResponse<BasketRegistrationPeriod> fetchBasketRegistrationPeriod() {
		BasketRegistrationPeriod basketRegistrationPeriod = basketRegistrationPeriodService.fetchBasketRegistrationPeriod();
		return ApiResponse.ok(ResponseMessage.BASKET_REGISTRATION_PERIOD_FETCH_SUCCESS.getMessage(), basketRegistrationPeriod);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/registration-period/baskets")
	public ApiResponse<Void> saveBasketRegistrationPeriod(@RequestBody BasketRegistrationPeriodSaveForm saveForm) {
		basketRegistrationPeriodService.saveBasketRegistrationPeriod(LocalDateTime.now(), saveForm.getStartTime(), saveForm.getEndTime());

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.BASKET_REGISTRATION_PERIOD_SAVE_SUCCESS.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/registration-period/enrollments")
	public ApiResponse<Void> saveEnrollmentRegistrationPeriod(@RequestBody EnrollmentRegistrationPeriodSaveForm saveForm) {
		enrollmentRegistrationPeriodService.saveEnrollmentRegistrationPeriod(LocalDateTime.now(), saveForm.getStartTime(), saveForm.getEndTime(),
			saveForm.getGrade());

		return ApiResponse.of(HttpStatus.CREATED, ResponseMessage.ENROLLMENT_REGISTRATION_PERIOD_SAVE_SUCCESS.getMessage(), null);
	}

}
