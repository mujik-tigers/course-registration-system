package site.courseregistrationsystem.enrollment.facade;

import static org.assertj.core.api.Assertions.*;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class RedissonEnrollmentLockFacadeTest extends IntegrationTestSupport {

	private static final LocalDateTime YEAR_2024_SEMESTER_SECOND = LocalDateTime.of(2024, 8, 15, 9, 0, 0);

	@Autowired
	private EntityManagerFactory factory;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private RedissonEnrollmentLockFacade redissonEnrollmentLockFacade;

	@Test
	@DisplayName("동시에 여러 학생이 같은 강의를 신청했을 때 강의의 최대 정원을 초과하여 요청을 처리하지 않는다")
	void enrollWithMultiThread() throws InterruptedException {
		// given
		int studentCount = 100;
		Fixtures fixtures = createLectureAndStudents(studentCount);
		Lecture lecture = fixtures.lecture;

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		final ExecutorService executorService = Executors.newFixedThreadPool(32);
		final CountDownLatch latch = new CountDownLatch(studentCount);

		// when
		for (long i = 0; i < studentCount; i++) {
			long studentPk = fixtures.startStudentId + i;
			boolean usingLectureNumber = i % 2 == 0;  // lectureNumber를 사용하는 빠른 신청과 lecutreId를 사용하는 일반 신청이 섞여서 요청된다

			executorService.submit(() -> {
				try {
					if (usingLectureNumber) {
						redissonEnrollmentLockFacade.enrollLectureByNumber(YEAR_2024_SEMESTER_SECOND, studentPk, lecture.getLectureNumber());
					} else {
						redissonEnrollmentLockFacade.enrollLecture(YEAR_2024_SEMESTER_SECOND, studentPk, lecture.getId());
					}
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		long count = enrollmentRepository.count();

		// then
		assertThat(count).isEqualTo((long)lecture.getTotalCapacity());
	}

	private static RegistrationDate createRegistrationDate(Year year, Semester semester) {
		Clock clock = Clock.builder()
			.year(year)
			.semester(semester)
			.build();
		CurrentYearAndSemester currentYearAndSemester = new CurrentYearAndSemester(clock);
		return new RegistrationDate(currentYearAndSemester);
	}

	private Fixtures createLectureAndStudents(int studentCount) {
		EntityManager entityManager = factory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		Department department = new Department("department");
		entityManager.persist(department);

		Professor professor = new Professor("professor");
		entityManager.persist(professor);

		Subject subject = Subject.builder()
			.targetGrade(Grade.FRESHMAN)
			.name("subject")
			.subjectDivision(SubjectDivision.GR)
			.hoursPerWeek(2)
			.credits(2)
			.build();
		entityManager.persist(subject);

		// studentCount만큼 학생 생성
		Student student = Student.builder()
			.studentId("studentId")
			.grade(Grade.FRESHMAN)
			.name("name")
			.password("password")
			.department(department)
			.build();
		entityManager.persist(student);

		Long startStudentId = student.getId();

		for (int i = 0; i < studentCount - 1; i++) {
			entityManager.persist(Student.builder()
				.studentId("studentId")
				.grade(Grade.FRESHMAN)
				.name("name")
				.password("password")
				.department(department)
				.build());
		}

		int totalCapacity = 20;

		// 1개의 강의 생성
		Lecture lecture = Lecture.builder()
			.totalCapacity(totalCapacity)
			.lectureRoom("lectureRoom")
			.lectureNumber(202420001)
			.openingYear(Year.of(2024))
			.semester(Semester.SECOND)
			.subject(subject)
			.department(department)
			.professor(professor)
			.build();
		entityManager.persist(lecture);

		Schedule schedule = Schedule.builder()
			.dayOfWeek(DayOfWeek.MON)
			.firstPeriod(Period.ONE)
			.lastPeriod(Period.THREE)
			.lecture(lecture)
			.build();
		entityManager.persist(schedule);

		entityManager.flush();
		entityManager.clear();

		transaction.commit();

		return new Fixtures(lecture, Year.of(2024), Semester.SECOND, startStudentId);
	}

	private record Fixtures(Lecture lecture, Year openingYear, Semester semester, Long startStudentId) {
	}

}
