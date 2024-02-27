package site.courseregistrationsystem.enrollment.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.persistence.EntityManager;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectures;
import site.courseregistrationsystem.enrollment.dto.EnrollmentCapacity;
import site.courseregistrationsystem.enrollment.infrastructure.EnrollmentRepository;
import site.courseregistrationsystem.exception.ErrorType;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.DuplicateSubjectException;
import site.courseregistrationsystem.exception.enrollment.EnrollmentNotFoundException;
import site.courseregistrationsystem.exception.enrollment.LectureApplicantsLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.registration_period.InvalidEnrollmentTimeException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class EnrollmentServiceTest extends IntegrationTestSupport {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	private static final LocalDateTime YEAR_2024_SEMESTER_SECOND = LocalDateTime.of(2024, 8, 15, 9, 0, 0);

	@BeforeEach
	void clearRedisCache() {
		Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();
	}

	@Test
	@DisplayName("수강 신청에 성공하면 신청된 강의의 PK를 반환한다")
	void enrollSuccess() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 20, 2);
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		// when
		Enrollment enrollment = enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture);

		// then
		assertThat(enrollment.getLecture().getId()).isEqualTo(lecture.getId());
	}

	@Test
	@DisplayName("강의의 정원을 초과하여 신청할 수 없다")
	void enrollFailApplicantsLimitExceeded() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 0, 2);  // 정원이 0인 강의를 생성합니다
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture))
			.isInstanceOf(LectureApplicantsLimitExceededException.class)
			.hasMessage(ErrorType.LECTURE_APPLICANTS_LIMIT_EXCEEDED.getMessage());
	}

	@Test
	@DisplayName("지난 해의 강의를 신청할 수 없다")
	void enrollFailPastYearLecture() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2023), Semester.SECOND, 20, 2);  // 2023년 2학기 강의를 생성합니다
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(Year.of(2024), Semester.SECOND);
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture))
			.isInstanceOf(LectureNotInCurrentSemesterException.class)
			.hasMessage(ErrorType.LECTURE_NOT_IN_CURRENT_SEMESTER.getMessage());
	}

	@Test
	@DisplayName("지난 학기의 강의를 신청할 수 없다")
	void enrollFailPastSemesterLecture() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.FIRST, 20, 2);  // 2024년 1학기 강의를 생성합니다
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(Year.of(2024), Semester.SECOND);
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture))
			.isInstanceOf(LectureNotInCurrentSemesterException.class)
			.hasMessage(ErrorType.LECTURE_NOT_IN_CURRENT_SEMESTER.getMessage());
	}

	@Test
	@DisplayName("수강 신청 기간이 아닌 경우, 수강 신청을 진행할 수 없다.")
	void invalidEnrollmentRegistrationTime() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 20, 2);
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		BDDMockito.doThrow(new InvalidEnrollmentTimeException())
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture))
			.isInstanceOf(InvalidEnrollmentTimeException.class);
	}

	@Test
	@DisplayName("학기 내 최대 18 학점을 초과해서 신청할 수 없다")
	void enrollFailCreditsExceed() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 20, 19);  // 19 학점의 강의를 생성합니다
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture))
			.isInstanceOf(CreditLimitExceededException.class)
			.hasMessage(ErrorType.CREDIT_LIMIT_EXCEEDED.getMessage());
	}

	@Test
	@DisplayName("학기 내 같은 과목을 2개 이상 신청할 수 없다")
	void enrollFailDuplicateSubject() {
		// given
		Fixtures fixtures = createStudentAndLecturesWithSameSubject();
		Student student = fixtures.student();
		Lecture lecture1 = fixtures.lectures().get(0);
		Lecture lecture2 = fixtures.lectures().get(1);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture1);

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture2))
			.isInstanceOf(DuplicateSubjectException.class)
			.hasMessage(ErrorType.SUBJECT_DUPLICATION.getMessage());
	}

	static Stream<Arguments> conflictedSchedules() {
		return Stream.of(
			arguments(Period.ONE, Period.THREE),
			arguments(Period.THREE, Period.FOUR),
			arguments(Period.FOUR, Period.FIVE),
			arguments(Period.FOUR, Period.SEVEN)
		);
	}

	@ParameterizedTest
	@MethodSource("conflictedSchedules")
	@DisplayName("학기 내 시간이 겹치는 수업은 신청할 수 없다")
	void enrollFailScheduleConflict(Period firstPeriod, Period lastPeriod) {
		// given
		Fixtures fixtures = createStudentAndLecturesWithConflictSchedule(firstPeriod, lastPeriod);
		Student student = fixtures.student();
		Lecture lecture1 = fixtures.lectures().get(0);
		Lecture lecture2 = fixtures.lectures().get(1);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture1);

		// when & then
		assertThatThrownBy(() -> enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture2))
			.isInstanceOf(ScheduleConflictException.class)
			.hasMessage(ErrorType.SCHEDULE_CONFLICT.getMessage());
	}

	@Test
	@DisplayName("해당 학기 내 수강 신청을 취소한다")
	void cancelEnrollmentSuccess() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 20, 2);
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		Enrollment enrollment = enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture);

		// when
		enrollmentService.cancel(student.getId(), enrollment.getId());

		// then
		assertThat(enrollmentRepository.findAllBy(student.getId())).hasSize(0);
	}

	@Test
	@DisplayName("신청한 적 없는 강의를 취소하려는 경우 오류 메세지를 응답한다")
	void cancelEnrollmentFail() {
		// given
		Department department = createDepartment();
		Student student = createStudent(department);

		Long invalidEnrollmentId = 10000L;

		// when & then
		assertThatThrownBy(() -> enrollmentService.cancel(student.getId(), invalidEnrollmentId))
			.isInstanceOf(EnrollmentNotFoundException.class)
			.hasMessage(ErrorType.ENROLLMENT_NONEXISTENT.getMessage());
	}

	@Test
	@DisplayName("다른 사람의 수강 신청을 취소하려고 시도하는 경우 오류 메세지를 응답한다")
	void cancelEnrollmentUnauthorizedFail() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 20, 2);
		Student student = fixtures.student();
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		Enrollment enrollment = enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture);  // student가 수강 신청을 합니다

		Student otherStudent = createStudent(fixtures.student.getDepartment());

		// when & then
		assertThatThrownBy(() -> enrollmentService.cancel(otherStudent.getId(), enrollment.getId()))  // otherStudent가 student의 수강 신청을 취소하려고 시도합니다
			.isInstanceOf(EnrollmentNotFoundException.class)
			.hasMessage(ErrorType.ENROLLMENT_NONEXISTENT.getMessage());
	}

	@Test
	@DisplayName("해당 학기 내 수강 신청 내역을 조회한다")
	void fetchEnrollmentsSuccess() {
		// given
		Fixtures fixtures = createStudentAndThreeLectures();
		Student student = fixtures.student();
		Lecture lecture1 = fixtures.lectures().get(0);
		Lecture lecture2 = fixtures.lectures().get(1);
		Lecture lecture3 = fixtures.lectures().get(2);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture1);
		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture2);
		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student, lecture3);

		// when
		EnrolledLectures enrolledLectures = enrollmentService.fetchAll(student.getId());

		// then
		assertThat(enrolledLectures.getEnrolledLectures()).hasSize(3);
	}

	@Test
	@DisplayName("강의의 현재 수강 신청 인원을 조회한다")
	void countEnrollmentsSuccess() {
		// given
		Fixtures fixtures = createStudentAndLecture(Year.of(2024), Semester.SECOND, 20, 2);
		Student student1 = fixtures.student();
		Student student2 = createStudent(student1.getDepartment());
		Student student3 = createStudent(student1.getDepartment());
		Lecture lecture = fixtures.lectures().get(0);

		RegistrationDate registrationDate = createRegistrationDate(fixtures.openingYear(), fixtures.semester());
		BDDMockito.doReturn(registrationDate)
			.when(enrollmentRegistrationPeriodService)
			.validateEnrollmentRegistrationPeriod(any(), any());

		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student1, lecture);
		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student2, lecture);
		enrollmentService.enroll(YEAR_2024_SEMESTER_SECOND, student3, lecture);

		// when
		EnrollmentCapacity enrollmentCapacity = enrollmentService.fetchCountBy(fixtures.openingYear(), fixtures.semester(), lecture.getId());

		// then
		assertThat(enrollmentCapacity.getCapacity()).isEqualTo(lecture.getTotalCapacity());
		assertThat(enrollmentCapacity.getCurrentEnrollmentCount()).isEqualTo(3);
	}

	private Department createDepartment() {
		Department department = new Department("금속공예디자인학과");
		entityManager.persist(department);

		return department;
	}

	private Student createStudent(Department department) {
		Student student = Student.builder()
			.studentId("171000962")
			.name("김서연")
			.grade(Grade.SENIOR)
			.department(department)
			.build();
		entityManager.persist(student);

		return student;
	}

	private Subject createSubject(String name, Integer credits) {
		Subject subject = Subject.builder()
			.credits(credits)
			.hoursPerWeek(3)
			.subjectDivision(SubjectDivision.GE)
			.name(name)
			.targetGrade(Grade.FRESHMAN)
			.build();
		entityManager.persist(subject);

		return subject;
	}

	private Lecture createLecture(Department department, Subject subject, Year openingYear, Semester semester, int totalCapacity) {
		Professor professor = new Professor("남유진");
		entityManager.persist(professor);

		Lecture lecture = Lecture.builder()
			.department(department)
			.openingYear(openingYear)
			.semester(semester)
			.lectureNumber(100100)
			.lectureRoom("다빈치관 401호")
			.professor(professor)
			.subject(subject)
			.totalCapacity(totalCapacity)
			.build();

		return lectureRepository.save(lecture);
	}

	private void createSchedule(Lecture lecture, DayOfWeek dayOfWeek, Period firstPeriod, Period lastPeriod) {
		Schedule schedule = Schedule.builder()
			.lecture(lecture)
			.dayOfWeek(dayOfWeek)
			.firstPeriod(firstPeriod)
			.lastPeriod(lastPeriod)
			.build();

		entityManager.persist(schedule);
		entityManager.flush();
		entityManager.clear();
	}

	private static RegistrationDate createRegistrationDate(Year year, Semester semester) {
		Clock clock = Clock.builder()
			.year(year)
			.semester(semester)
			.build();
		CurrentYearAndSemester currentYearAndSemester = new CurrentYearAndSemester(clock);
		return new RegistrationDate(currentYearAndSemester);
	}

	private record Fixtures(Student student, Year openingYear, Semester semester, List<Lecture> lectures) {
	}

	private Fixtures createStudentAndLecture(Year openingYear, Semester semester, int lectureCapacity, int credits) {
		Department department = createDepartment();
		Subject subject = createSubject("미술사", credits);
		Lecture lecture = createLecture(department, subject, openingYear, semester, lectureCapacity);
		createSchedule(lecture, DayOfWeek.MON, Period.ONE, Period.THREE);

		Student student = createStudent(department);

		return new Fixtures(student, openingYear, semester, List.of(lecture));
	}

	private Fixtures createStudentAndLecturesWithSameSubject() {
		Department department = createDepartment();
		Subject subject = createSubject("미술사", 2);
		Lecture lecture1 = createLecture(department, subject, Year.of(2024), Semester.SECOND, 20);
		Lecture lecture2 = createLecture(department, subject, Year.of(2024), Semester.SECOND, 20);
		createSchedule(lecture1, DayOfWeek.MON, Period.ONE, Period.THREE);
		createSchedule(lecture2, DayOfWeek.TUE, Period.ONE, Period.THREE);

		Student student = createStudent(department);

		return new Fixtures(student, Year.of(2024), Semester.SECOND, List.of(lecture1, lecture2));
	}

	private Fixtures createStudentAndLecturesWithConflictSchedule(Period firstPeriod, Period lastPeriod) {
		Department department = createDepartment();
		Subject subject1 = createSubject("미술사", 2);
		Subject subject2 = createSubject("세계사", 2);
		Lecture lecture1 = createLecture(department, subject1, Year.of(2024), Semester.SECOND, 20);
		Lecture lecture2 = createLecture(department, subject2, Year.of(2024), Semester.SECOND, 20);
		createSchedule(lecture1, DayOfWeek.MON, Period.THREE, Period.FIVE);
		createSchedule(lecture2, DayOfWeek.MON, firstPeriod, lastPeriod);
		List<Lecture> lectures = lectureRepository.findAllById(List.of(lecture1.getId(), lecture2.getId()));

		Student student = createStudent(department);

		return new Fixtures(student, Year.of(2024), Semester.SECOND, lectures);
	}

	private Fixtures createStudentAndThreeLectures() {
		Department department = createDepartment();

		Subject subject1 = createSubject("subject1", 2);
		Lecture lecture1 = createLecture(department, subject1, Year.of(2024), Semester.SECOND, 20);
		createSchedule(lecture1, DayOfWeek.MON, Period.ONE, Period.THREE);

		Subject subject2 = createSubject("subject2", 2);
		Lecture lecture2 = createLecture(department, subject2, Year.of(2024), Semester.SECOND, 20);
		createSchedule(lecture2, DayOfWeek.TUE, Period.ONE, Period.THREE);

		Subject subject3 = createSubject("subject3", 2);
		Lecture lecture3 = createLecture(department, subject3, Year.of(2024), Semester.SECOND, 20);
		createSchedule(lecture3, DayOfWeek.WED, Period.ONE, Period.THREE);

		Student student = createStudent(department);

		return new Fixtures(student, Year.of(2024), Semester.SECOND, List.of(lecture1, lecture2, lecture3));
	}

}
