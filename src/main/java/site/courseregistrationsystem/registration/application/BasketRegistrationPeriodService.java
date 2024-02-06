package site.courseregistrationsystem.registration.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.clock.application.ClockService;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.exception.registration_period.BasketRegistrationPeriodNotFoundException;
import site.courseregistrationsystem.exception.registration_period.InvalidBasketTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.registration.BasketRegistrationPeriod;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.registration.infrastructure.BasketRegistrationPeriodStorage;
import site.courseregistrationsystem.student.Grade;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasketRegistrationPeriodService {

	private final ClockService clockService;

	private final BasketRegistrationPeriodStorage basketRegistrationPeriodStorage;

	public BasketRegistrationPeriod fetchBasketRegistrationPeriod() {
		return basketRegistrationPeriodStorage.findById(Grade.COMMON.name())
			.orElseThrow(BasketRegistrationPeriodNotFoundException::new);
	}

	@Transactional
	public void saveBasketRegistrationPeriod(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime) {
		checkInvalidTime(now, startTime, endTime);

		BasketRegistrationPeriod basketRegistrationPeriod = BasketRegistrationPeriod.builder()
			.startTime(startTime)
			.endTime(endTime)
			.build();

		basketRegistrationPeriodStorage.save(basketRegistrationPeriod);
	}

	public RegistrationDate validateBasketRegistrationPeriod(LocalDateTime now) {
		BasketRegistrationPeriod registrationPeriod = basketRegistrationPeriodStorage.findById(Grade.COMMON.name())
			.orElseThrow(BasketRegistrationPeriodNotFoundException::new);

		if (!registrationPeriod.isWithinTimeRange(now)) {
			throw new InvalidBasketTimeException();
		}

		CurrentYearAndSemester currentYearAndSemester = clockService.fetchCurrentClock();
		return new RegistrationDate(currentYearAndSemester);
	}

	private void checkInvalidTime(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime.isBefore(now)) {
			throw new StartTimeBeforeCurrentTimeException();
		}

		if (startTime.isAfter(endTime)) {
			throw new StartTimeAfterEndTimeException();
		}
	}
}
