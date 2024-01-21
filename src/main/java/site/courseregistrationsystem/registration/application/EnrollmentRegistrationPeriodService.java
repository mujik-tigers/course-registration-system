package site.courseregistrationsystem.registration.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.exception.registration_period.InvalidEnrollmentTimeException;
import site.courseregistrationsystem.exception.registration_period.NonexistenceCommonEnrollmentRegistrationPeriodException;
import site.courseregistrationsystem.exception.registration_period.NonexistenceEnrollmentRegistrationPeriodException;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.registration.EnrollmentRegistrationPeriod;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.registration.infrastructure.EnrollmentRegistrationPeriodStorage;
import site.courseregistrationsystem.student.Grade;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentRegistrationPeriodService {

	private final EnrollmentRegistrationPeriodStorage enrollmentRegistrationPeriodStorage;

	@Transactional
	public void saveEnrollmentRegistrationPeriod(LocalDateTime now,
		LocalDateTime startTime, LocalDateTime endTime,
		Grade targetGrade, Semester semester) {

		checkInvalidTime(now, startTime, endTime);

		EnrollmentRegistrationPeriod enrollmentRegistrationPeriod = EnrollmentRegistrationPeriod.builder()
			.targetGrade(targetGrade)
			.startTime(startTime)
			.endTime(endTime)
			.semester(semester)
			.build();

		enrollmentRegistrationPeriodStorage.save(enrollmentRegistrationPeriod);
	}

	public RegistrationDate validateEnrollmentRegistrationPeriod(LocalDateTime now, Grade grade) {
		EnrollmentRegistrationPeriod registrationPeriodInGrade = enrollmentRegistrationPeriodStorage.findById(grade.name())
			.orElseThrow(NonexistenceEnrollmentRegistrationPeriodException::new);

		if (registrationPeriodInGrade.isWithinTimeRange(now)) {
			return new RegistrationDate(
				registrationPeriodInGrade.getYear(),
				registrationPeriodInGrade.getSemester()
			);
		}

		EnrollmentRegistrationPeriod registrationPeriodInCommon = enrollmentRegistrationPeriodStorage.findById(Grade.COMMON.name())
			.orElseThrow(NonexistenceCommonEnrollmentRegistrationPeriodException::new);

		if (registrationPeriodInCommon.isWithinTimeRange(now)) {
			return new RegistrationDate(
				registrationPeriodInCommon.getYear(),
				registrationPeriodInCommon.getSemester()
			);
		}

		throw new InvalidEnrollmentTimeException();
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
