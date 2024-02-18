package site.courseregistrationsystem.enrollment.facade;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.registration.dto.RegistrationDate;

@Sql("/sql/create_multithread_test_fixture.sql")
@Sql(scripts = "/sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RedissonEnrollmentLockFacadeTest extends IntegrationTestSupport {

	private static final LocalDateTime YEAR_2024_SEMESTER_SECOND = LocalDateTime.of(2024, 8, 15, 9, 0, 0);

	@Autowired
	private RedissonEnrollmentLockFacade redissonEnrollmentLockFacade;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Test
	@Transactional(propagation = Propagation.NEVER)
	@DisplayName("특정 강의에 대한 수강신청이 한번에 몰려도, 특정 강의의 수강 신청 인원 제한을 넘는 인원이 수강 신청할 수 없다.")
	void enrollmentMultiThread() throws Exception {
		// given
		int studentCount = 100;
		int LECTURE_NUMBER = 202400001;
		long LECTURE_ID = 1;
		long LECTURE_TOTAL_CAPACITY = 20L;

		RegistrationDate registrationDate = createRegistrationDate(Year.of(2024), Semester.SECOND);
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(studentCount);

		// when
		for (long i = 1; i <= studentCount; i++) {
			long studentPk = i;
			boolean usingLectureNumber = i % 2 == 0;  // lectureNumber를 사용하는 빠른 신청과 lecutreId를 사용하는 일반 신청이 섞여서 요청된다

			executorService.submit(() -> {
				try {
					if (usingLectureNumber) {
						redissonEnrollmentLockFacade.enrollLectureByNumber(YEAR_2024_SEMESTER_SECOND, studentPk, LECTURE_NUMBER);
					} else {
						redissonEnrollmentLockFacade.enrollLecture(YEAR_2024_SEMESTER_SECOND, studentPk, LECTURE_ID);
					}
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		long count = enrollmentRepository.count();

		// then
		assertThat(count).isEqualTo(LECTURE_TOTAL_CAPACITY);
	}

	private static RegistrationDate createRegistrationDate(Year year, Semester semester) {
		Clock clock = Clock.builder()
			.year(year)
			.semester(semester)
			.build();
		CurrentYearAndSemester currentYearAndSemester = new CurrentYearAndSemester(clock);
		return new RegistrationDate(currentYearAndSemester);
	}

}
