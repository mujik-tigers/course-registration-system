package site.courseregistrationsystem.registration.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.exception.registration_period.InvalidBasketTimeException;
import site.courseregistrationsystem.exception.registration_period.NonexistenceBasketRegistrationPeriodException;
import site.courseregistrationsystem.exception.registration_period.StartTimeAfterEndTimeException;
import site.courseregistrationsystem.exception.registration_period.StartTimeBeforeCurrentTimeException;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.registration.BasketRegistrationPeriod;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.registration.infrastructure.BasketRegistrationPeriodStorage;
import site.courseregistrationsystem.student.Grade;

class BasketRegistrationPeriodServiceTest extends IntegrationTestSupport {

	@Autowired
	private BasketRegistrationPeriodService basketRegistrationPeriodService;

	@Autowired
	private BasketRegistrationPeriodStorage basketRegistrationPeriodStorage;

	@AfterEach
	void clear() {
		basketRegistrationPeriodStorage.deleteAll();
	}

	@Test
	@DisplayName("운영자는 수강 바구니 신청 시작시간, 종료시간, 신청 학기를 입력하여, 수강 바구니 신청기간을 등록한다.")
	void saveBasketRegistrationPeriod() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Semester semester = Semester.FIRST;

		// when
		basketRegistrationPeriodService.saveBasketRegistrationPeriod(now, startTime, endTime, semester);

		// then
		BasketRegistrationPeriod registrationPeriod = basketRegistrationPeriodStorage.findById(Grade.COMMON.name()).get();

		assertThat(registrationPeriod.getStartTime()).isEqualTo(startTime);
		assertThat(registrationPeriod.getEndTime()).isEqualTo(endTime);
		assertThat(registrationPeriod.getSemester()).isEqualTo(semester.name());
		assertThat(registrationPeriod.getYear()).isEqualTo(startTime.getYear());
	}

	@Test
	@DisplayName("운영자가 설정한 수강 바구니 신청 시작기간이, 현재 등록하는 시간보다 빠르면 안된다.")
	void startTimeBeforeCurrentTimeFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 14, 9, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Semester semester = Semester.FIRST;

		// when & then
		assertThatThrownBy(() -> basketRegistrationPeriodService.saveBasketRegistrationPeriod(now, startTime, endTime, semester))
			.isInstanceOf(StartTimeBeforeCurrentTimeException.class);
	}

	@Test
	@DisplayName("운영자가 설정한 수강 바구니 신청 시작기간이, 수강 바구니 신청 종료기간보다 느리면 안된다.")
	void startTimeAfterEndTimeFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		Semester semester = Semester.FIRST;

		// when & then
		assertThatThrownBy(() -> basketRegistrationPeriodService.saveBasketRegistrationPeriod(now, startTime, endTime, semester))
			.isInstanceOf(StartTimeAfterEndTimeException.class);
	}

	@Test
	@DisplayName("학생이 수강 바구니 신청을 하려는 시간이, 운영자가 설정한 수강 바구니 신청 기간에 부합하면 등록 시간을 얻을 수 있다.")
	void validateBasketRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime, endTime, semester);

		LocalDateTime currentRegistrationTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);

		// when
		RegistrationDate registrationDate = basketRegistrationPeriodService.validateBasketRegistrationPeriod(currentRegistrationTime);

		// then
		assertThat(registrationDate.getYear().getValue()).isEqualTo(startTime.getYear());
		assertThat(registrationDate.getSemester()).isEqualTo(semester);
	}

	@Test
	@DisplayName("학생이 수강 바구니 신청을 하려는 시간이, 운영자가 설정한 수강 바구니 신청 기간에 부합하지 않으면 예외가 발생한다.")
	void invalidBasketRegistrationPeriodFail() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime, endTime, semester);

		LocalDateTime earlyRegistrationTime = LocalDateTime.of(2024, 1, 16, 9, 29, 0);
		LocalDateTime lateRegistrationTime = LocalDateTime.of(2024, 1, 16, 10, 1, 0);

		// when & then
		assertThatThrownBy(() -> basketRegistrationPeriodService.validateBasketRegistrationPeriod(earlyRegistrationTime))
			.isInstanceOf(InvalidBasketTimeException.class);

		assertThatThrownBy(() -> basketRegistrationPeriodService.validateBasketRegistrationPeriod(lateRegistrationTime))
			.isInstanceOf(InvalidBasketTimeException.class);
	}

	@Test
	@DisplayName("수강 바구니 신청 기간이 존재하지 않는다면 예외가 발생한다.")
	void nonExistenceRegistrationPeriodFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 16, 9, 45, 0);

		// when & then
		assertThatThrownBy(() -> basketRegistrationPeriodService.validateBasketRegistrationPeriod(now))
			.isInstanceOf(NonexistenceBasketRegistrationPeriodException.class);
	}

	private BasketRegistrationPeriod saveRegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime, Semester semester) {
		BasketRegistrationPeriod registrationPeriod = BasketRegistrationPeriod.builder()
			.startTime(startTime)
			.endTime(endTime)
			.semester(semester)
			.build();

		return basketRegistrationPeriodStorage.save(registrationPeriod);
	}

}
