package site.courseregistrationsystem.registration.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.exception.registration_period.InvalidBasketTimeException;
import site.courseregistrationsystem.exception.registration_period.NonexistenceBasketRegistrationPeriodException;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.registration.BasketRegistrationPeriod;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.registration.infrastructure.BasketRegistrationPeriodStorage;
import site.courseregistrationsystem.student.Grade;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasketRegistrationPeriodService {

	private final BasketRegistrationPeriodStorage basketRegistrationPeriodStorage;

	@Transactional
	public void saveBasketRegistrationPeriod(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime, Semester semester) {
		checkInvalidTime(now, startTime, endTime);

		BasketRegistrationPeriod basketRegistrationPeriod = BasketRegistrationPeriod.builder()
			.startTime(startTime)
			.endTime(endTime)
			.semester(semester)
			.build();

		basketRegistrationPeriodStorage.save(basketRegistrationPeriod);
	}

	public RegistrationDate validateBasketRegistrationPeriod(LocalDateTime now) {
		BasketRegistrationPeriod registrationPeriod = basketRegistrationPeriodStorage.findById(Grade.COMMON.name())
			.orElseThrow(NonexistenceBasketRegistrationPeriodException::new);

		if (!registrationPeriod.isWithinTimeRange(now)) {
			throw new InvalidBasketTimeException();
		}

		return new RegistrationDate(
			registrationPeriod.getYear(),
			registrationPeriod.getSemester()
		);
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
