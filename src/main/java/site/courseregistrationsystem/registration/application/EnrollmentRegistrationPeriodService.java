package site.courseregistrationsystem.registration.application;

import static site.courseregistrationsystem.util.ProjectConstant.*;

import java.time.LocalDateTime;

import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.clock.application.ClockService;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.exception.registration_period.InvalidEnrollmentTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.registration.EnrollmentRegistrationPeriod;
import site.courseregistrationsystem.registration.dto.EnrollmentRegistrationPeriods;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.registration.infrastructure.EnrollmentRegistrationPeriodStorage;
import site.courseregistrationsystem.student.Grade;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentRegistrationPeriodService {

	private final ClockService clockService;
	private final EnrollmentRegistrationPeriodStorage enrollmentRegistrationPeriodStorage;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public EnrollmentRegistrationPeriods fetchEnrollmentRegistrationPeriods() {
		return new EnrollmentRegistrationPeriods(enrollmentRegistrationPeriodStorage.findAll());
	}

	@Transactional
	public void saveEnrollmentRegistrationPeriod(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime, Grade targetGrade) {
		checkInvalidTime(now, startTime, endTime);

		EnrollmentRegistrationPeriod enrollmentRegistrationPeriod = EnrollmentRegistrationPeriod.builder()
			.targetGrade(targetGrade)
			.startTime(startTime)
			.endTime(endTime)
			.build();

		enrollmentRegistrationPeriodStorage.save(enrollmentRegistrationPeriod);
	}

	public RegistrationDate validateEnrollmentRegistrationPeriod(LocalDateTime now, Grade grade) {
		redisTemplate.executePipelined(
				(RedisCallback<Object>)connection -> {
					StringRedisConnection stringRedisConnection = (StringRedisConnection)connection;
					stringRedisConnection.hGetAll(ENROLLMENT_REGISTRATION_PERIOD_PREFIX + grade.name());
					stringRedisConnection.hGetAll(ENROLLMENT_REGISTRATION_PERIOD_PREFIX + Grade.COMMON.name());
					return null;
				}
			).stream()
			.map(lhm -> objectMapper.convertValue(lhm, EnrollmentRegistrationPeriod.class))
			.filter(period -> period.isWithinTimeRange(now))
			.findAny()
			.orElseThrow(InvalidEnrollmentTimeException::new);

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
