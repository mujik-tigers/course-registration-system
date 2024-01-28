package site.courseregistrationsystem.clock.application;

import static org.assertj.core.api.Assertions.*;

import java.time.Year;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.clock.infrastructure.ClockStorage;
import site.courseregistrationsystem.exception.clock.ClockNotFoundException;
import site.courseregistrationsystem.lecture.Semester;

class ClockServiceTest extends IntegrationTestSupport {

	@Autowired
	private ClockStorage clockStorage;

	@Autowired
	private ClockService clockService;

	@AfterEach
	void clear() {
		clockStorage.deleteAll();
	}

	@Test
	@DisplayName("현재 년도와 학기 정보를 조회한다.")
	void fetchCurrentYearAndSemester() throws Exception {
		// given
		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		Clock clock = Clock.builder()
			.year(YEAR)
			.semester(SEMESTER)
			.build();

		clockStorage.save(clock);

		// when
		CurrentYearAndSemester currentYearAndSemester = clockService.fetchCurrentClock();

		// then
		assertThat(currentYearAndSemester.getYear()).isEqualTo(YEAR.getValue());
		assertThat(currentYearAndSemester.getSemester()).isEqualTo(SEMESTER.name());
	}

	@Test
	@DisplayName("저장된 현재 시간 정보가 없다면 예외가 발생한다.")
	void nonexistenceClock() throws Exception {
		// when & then
		assertThatThrownBy(() -> clockService.fetchCurrentClock())
			.isInstanceOf(ClockNotFoundException.class);
	}

}
