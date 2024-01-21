package site.courseregistrationsystem.registration.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
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

class EnrollmentRegistrationPeriodServiceTest extends IntegrationTestSupport {

	@Autowired
	private EnrollmentRegistrationPeriodService enrollmentRegistrationPeriodService;

	@Autowired
	private EnrollmentRegistrationPeriodStorage enrollmentRegistrationPeriodStorage;

	@AfterEach
	void clear() {
		enrollmentRegistrationPeriodStorage.deleteAll();
	}

	private static Stream<Arguments> gradeType() {
		return Stream.of(
			Arguments.of(Grade.COMMON),
			Arguments.of(Grade.FRESHMAN),
			Arguments.of(Grade.SOPHOMORE),
			Arguments.of(Grade.JUNIOR),
			Arguments.of(Grade.SENIOR)
		);
	}

	@DisplayName("운영자는 수강신청 시작시간, 수강신청 종료시간, 수강신청 대상 학년, 수강신청 학기를 입력하여, 수강신청 기간을 등록한다.")
	@MethodSource("gradeType")
	@ParameterizedTest
	void saveEnrollmentRegistrationPeriod(Grade targetGrade) throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Semester semester = Semester.FIRST;

		// when
		enrollmentRegistrationPeriodService.saveEnrollmentRegistrationPeriod(now, startTime, endTime, targetGrade, semester);

		// then
		EnrollmentRegistrationPeriod registrationPeriod = enrollmentRegistrationPeriodStorage.findById(targetGrade.name()).get();

		assertThat(registrationPeriod.getStartTime()).isEqualTo(startTime);
		assertThat(registrationPeriod.getEndTime()).isEqualTo(endTime);
		assertThat(registrationPeriod.getTargetGrade()).isEqualTo(targetGrade.name());
		assertThat(registrationPeriod.getSemester()).isEqualTo(semester.name());
		assertThat(registrationPeriod.getYear()).isEqualTo(startTime.getYear());
	}

	@Test
	@DisplayName("운영자가 설정한 수강신청 시작기간이, 현재 등록하는 시간보다 빠르면 안된다.")
	void startTimeBeforeCurrentTimeFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 14, 9, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Grade targetGrade = Grade.FRESHMAN;
		Semester semester = Semester.FIRST;

		// when & then
		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.saveEnrollmentRegistrationPeriod(now, startTime, endTime, targetGrade, semester))
			.isInstanceOf(StartTimeBeforeCurrentTimeException.class);
	}

	@Test
	@DisplayName("운영자가 설정한 수강신청 시작기간이, 수강신청 종료기간보다 느리면 안된다.")
	void startTimeAfterEndTimeFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 15, 9, 0, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		Grade targetGrade = Grade.FRESHMAN;
		Semester semester = Semester.FIRST;

		// when & then
		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.saveEnrollmentRegistrationPeriod(now, startTime, endTime, targetGrade, semester))
			.isInstanceOf(StartTimeAfterEndTimeException.class);
	}

	@Test
	@DisplayName("학생이 수강신청을 하려는 시간과 학생의 학년이, 운영자가 설정한 수강신청 기간과 학년에 부합하면 등록 시간을 얻을 수 있다.")
	void validateEnrollmentRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Grade targetGrade = Grade.FRESHMAN;
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime, endTime, targetGrade, semester);

		LocalDateTime currentRegistrationTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);

		// when
		RegistrationDate registrationDate = enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(currentRegistrationTime,
			targetGrade);

		// then
		assertThat(registrationDate.getYear().getValue()).isEqualTo(startTime.getYear());
		assertThat(registrationDate.getSemester()).isEqualTo(semester);
	}

	@Test
	@DisplayName("학생이 수강신청을 하려는 시간이, 운영자가 설정한 공통 수강신청 기간에 부합하면 등록 시간을 얻을 수 있다.")
	void validateCommonEnrollmentRegistrationPeriod() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Grade studentGrade = Grade.FRESHMAN;
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime.plusDays(1), endTime.plusDays(1), Grade.FRESHMAN, semester);
		saveRegistrationPeriod(startTime, endTime, Grade.COMMON, semester);

		LocalDateTime commonRegistrationTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);

		// when
		RegistrationDate registrationDate = enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(commonRegistrationTime,
			studentGrade);

		// then
		assertThat(registrationDate.getYear().getValue()).isEqualTo(startTime.getYear());
		assertThat(registrationDate.getSemester()).isEqualTo(semester);
	}

	@Test
	@DisplayName("학생이 수강신청을 하려는 시간이, 운영자가 설정한 수강신청 기간에 부합하지 않으면 예외가 발생한다.")
	void invalidEnrollmentRegistrationPeriodFail() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Grade targetGrade = Grade.FRESHMAN;
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime, endTime, targetGrade, semester);
		saveRegistrationPeriod(startTime.plusYears(1), endTime.plusYears(1), Grade.COMMON, semester);

		LocalDateTime earlyRegistrationTime = LocalDateTime.of(2024, 1, 16, 9, 29, 0);
		LocalDateTime lateRegistrationTime = LocalDateTime.of(2024, 1, 16, 10, 1, 0);

		// when & then
		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(earlyRegistrationTime, targetGrade))
			.isInstanceOf(InvalidEnrollmentTimeException.class);

		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(lateRegistrationTime, targetGrade))
			.isInstanceOf(InvalidEnrollmentTimeException.class);
	}

	@Test
	@DisplayName("학생의 학년과 수강신청을 하려는 시간이, 운영자가 설정한 학년 수강신청 기간과 공통 수강신청 기간 중 어느것에도 해당되지 않으면 예외가 발생한다.")
	void invalidEnrollmentRegistrationPeriodFailDifferentGrade() throws Exception {
		// given
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime, endTime, Grade.FRESHMAN, semester);
		saveRegistrationPeriod(startTime.plusDays(1), endTime.plusDays(1), Grade.SOPHOMORE, semester);
		saveRegistrationPeriod(startTime.plusYears(1), endTime.plusYears(1), Grade.COMMON, semester);

		LocalDateTime freshmanRegistrationTime = LocalDateTime.of(2024, 1, 16, 9, 45, 0);

		// when & then
		enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(freshmanRegistrationTime, Grade.FRESHMAN);// 1학년에게는 적합한 수강신청기간

		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(freshmanRegistrationTime, Grade.SOPHOMORE))
			.isInstanceOf(InvalidEnrollmentTimeException.class);        // 2학년에게는 적합한 수강신청 기간이 아님
	}

	@Test
	@DisplayName("수강신청 기간이 존재하지 않는다면 예외가 발생한다.")
	void nonExistenceRegistrationPeriodFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 16, 9, 45, 0);

		// when & then
		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(now, Grade.FRESHMAN))
			.isInstanceOf(NonexistenceEnrollmentRegistrationPeriodException.class);
	}

	@Test
	@DisplayName("학생의 현재 수강신청 시간이, 등록된 학년 수강신청 기간에 부합하지 않고, 공통 수강신청 기간이 존재하지 않는다면 예외가 발생한다.")
	void nonExistenceCommonRegistrationPeriodFail() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.of(2024, 1, 17, 9, 40, 0);
		LocalDateTime startTime = LocalDateTime.of(2024, 1, 16, 9, 30, 0);
		LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 10, 0, 0);
		Grade targetGrade = Grade.FRESHMAN;
		Semester semester = Semester.FIRST;

		saveRegistrationPeriod(startTime, endTime, targetGrade, semester);

		// when & then
		assertThatThrownBy(() -> enrollmentRegistrationPeriodService.validateEnrollmentRegistrationPeriod(now, targetGrade))
			.isInstanceOf(NonexistenceCommonEnrollmentRegistrationPeriodException.class);
	}

	private EnrollmentRegistrationPeriod saveRegistrationPeriod(LocalDateTime startTime, LocalDateTime endTime, Grade targetGrade,
		Semester semester) {
		EnrollmentRegistrationPeriod registrationPeriod = EnrollmentRegistrationPeriod.builder()
			.startTime(startTime)
			.endTime(endTime)
			.targetGrade(targetGrade)
			.semester(semester)
			.build();

		return enrollmentRegistrationPeriodStorage.save(registrationPeriod);
	}

}
