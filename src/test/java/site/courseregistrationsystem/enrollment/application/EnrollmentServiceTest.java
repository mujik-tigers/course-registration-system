package site.courseregistrationsystem.enrollment.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.enrollment.dto.EnrolledLecture;
import site.courseregistrationsystem.enrollment.dto.EnrolledLectures;
import site.courseregistrationsystem.exception.ErrorType;
import site.courseregistrationsystem.exception.enrollment.CreditsLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.DuplicateEnrollmentException;
import site.courseregistrationsystem.exception.enrollment.EnrollmentNotFoundException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.enrollment.ScheduleConflictException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class EnrollmentServiceTest extends IntegrationTestSupport {

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private LectureRepository lectureRepository;

	@Test
	@DisplayName("빠른 수강 신청에 성공하면 신청된 강의의 PK를 반환한다")
	void enrollFastSuccess() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Subject subject = saveSubject("미술사", 2);
		Lecture lecture = saveLecture(department, subject, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture, DayOfWeek.MON, Period.ONE, Period.THREE);

		// when
		EnrolledLecture enrolledLecture = enrollmentService.enrollLectureByNumber(student.getId(),
			lecture.getLectureNumber());

		// then
		assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
	}

	@Test
	@DisplayName("수강 신청에 성공하면 신청된 강의의 PK를 반환한다")
	void enrollSuccess() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Subject subject = saveSubject("미술사", 2);
		Lecture lecture = saveLecture(department, subject, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture, DayOfWeek.MON, Period.ONE, Period.THREE);

		// when
		EnrolledLecture enrolledLecture = enrollmentService.enrollLecture(student.getId(), lecture.getId());

		// then
		assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
	}

	@Test
	@DisplayName("지난 해의 강의를 신청할 수 없다")
	void enrollFailPastYearLecture() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);

		Subject subject1 = saveSubject("동양미술사", 3);
		Lecture pastLecture = saveLecture(department, subject1, Year.of(2023), Semester.FIRST);
		saveSchedule(pastLecture, DayOfWeek.MON, Period.ONE, Period.THREE);  // 작년 강의 개설

		// when & then
		assertThatThrownBy(() -> enrollmentService.enrollLecture(student.getId(), pastLecture.getId()))
			.isInstanceOf(LectureNotInCurrentSemesterException.class)
			.hasMessage(ErrorType.LECTURE_NOT_IN_CURRENT_SEMESTER.getMessage());
	}

	@Test
	@DisplayName("지난 학기의 강의를 신청할 수 없다")
	void enrollFailPastSemesterLecture() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);

		Subject subject1 = saveSubject("동양미술사", 3);
		Lecture pastLecture = saveLecture(department, subject1, Year.of(2023), Semester.SECOND);
		saveSchedule(pastLecture, DayOfWeek.MON, Period.ONE, Period.THREE);  // 작년 강의 개설

		// when & then
		assertThatThrownBy(() -> enrollmentService.enrollLecture(student.getId(), pastLecture.getId()))
			.isInstanceOf(LectureNotInCurrentSemesterException.class)
			.hasMessage(ErrorType.LECTURE_NOT_IN_CURRENT_SEMESTER.getMessage());
	}

	@Test
	@DisplayName("신청 년도의 학기 내, 최대 학점을 초과해서 신청할 수 없다")
	void enrollFailCreditsExceed() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Year openingYear = Year.of(2024);
		Semester semester = Semester.FIRST;

		int maxCredit = 18;
		Subject subject1 = saveSubject("동양미술사", maxCredit);
		Lecture lectureWithMaxCredits = saveLecture(department, subject1, openingYear, semester);  // 최대 학점을 갖는 강의를 생성
		saveSchedule(lectureWithMaxCredits, DayOfWeek.MON, Period.ONE, Period.THREE);
		enrollmentService.enrollLecture(student.getId(), lectureWithMaxCredits.getId());  // 최대 학점을 채워서 신청한 상황

		int minCredit = 1;
		Subject subject2 = saveSubject("서양미술사", minCredit);
		Lecture lectureWithMinCredits = saveLecture(department, subject2, openingYear, semester);  // 최소 학점을 갖는 강의를 생성
		saveSchedule(lectureWithMinCredits, DayOfWeek.TUE, Period.ONE, Period.THREE);

		// when & then
		assertThatThrownBy(() -> enrollmentService.enrollLecture(student.getId(), lectureWithMinCredits.getId()))
			.isInstanceOf(CreditsLimitExceededException.class)
			.hasMessage(ErrorType.SEMESTER_CREDIT_EXCEED.getMessage());
	}

	@Test
	@DisplayName("신청 년도의 학기 내, 같은 과목을 2개 이상 신청할 수 없다")
	void enrollFailDuplicateSubject() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Subject subject = saveSubject("미술사", 2);
		Year openingYear = Year.of(2024);
		Semester semester = Semester.FIRST;

		Lecture lectureOnMonday = saveLecture(department, subject, openingYear, semester);  // 과목은 동일하나 요일은 다른 강의 2개 생성
		Lecture LectureOnFriday = saveLecture(department, subject, openingYear, semester);
		saveSchedule(lectureOnMonday, DayOfWeek.MON, Period.ONE, Period.THREE);
		saveSchedule(LectureOnFriday, DayOfWeek.FRI, Period.ONE, Period.THREE);

		enrollmentService.enrollLecture(student.getId(), lectureOnMonday.getId());  // 월요일 강의를 이미 수강 신청한 상황

		// when & then
		assertThatThrownBy(() -> enrollmentService.enrollLecture(student.getId(), LectureOnFriday.getId()))
			.isInstanceOf(DuplicateEnrollmentException.class)
			.hasMessage(ErrorType.ENROLLMENT_DUPLICATION.getMessage());
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
	@DisplayName("신청 년도의 학기 내, 시간이 겹치는 수업은 신청할 수 없다")
	void enrollFailScheduleConflict(Period firstPeriod, Period lastPeriod) {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Year openingYear = Year.of(2024);
		Semester semester = Semester.FIRST;

		Subject subject1 = saveSubject("동양미술사", 2);
		Lecture lecture1 = saveLecture(department, subject1, openingYear, semester);
		saveSchedule(lecture1, DayOfWeek.MON, Period.THREE, Period.FIVE);  // 월요일 3-5교시 수업 생성
		enrollmentService.enrollLecture(student.getId(), lecture1.getId());  // 수업 신청

		Subject subject2 = saveSubject("서양미술사", 2);
		Lecture lecture2 = saveLecture(department, subject2, openingYear, semester);
		saveSchedule(lecture2, DayOfWeek.MON, firstPeriod, lastPeriod);  // 시간이 겹치도록 생성

		// when & then
		assertThatThrownBy(() -> enrollmentService.enrollLecture(student.getId(), lecture2.getId()))
			.isInstanceOf(ScheduleConflictException.class)
			.hasMessage(ErrorType.SCHEDULE_CONFLICT.getMessage());
	}

	@TestFactory
	@DisplayName("최대 학점 내에서 중복되지 않은 과목과 시간을 갖는 수업 신청 시, 등록에 성공한다")
	Collection<DynamicTest> enrollNoConflict() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Year openingYear = Year.of(2024);
		Semester semester = Semester.FIRST;

		return List.of(
			dynamicTest("월요일 1-3교시 수업 수강 신청", () -> {
				Subject subject = saveSubject("동양미술사", 3);
				Lecture lecture = saveLecture(department, subject, openingYear, semester);
				saveSchedule(lecture, DayOfWeek.MON, Period.ONE, Period.THREE);

				// when
				EnrolledLecture enrolledLecture = enrollmentService.enrollLecture(student.getId(), lecture.getId());

				// then
				assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
			}),
			dynamicTest("월요일 4-6교시 수업 수강 신청", () -> {
				Subject subject = saveSubject("서양미술사", 3);
				Lecture lecture = saveLecture(department, subject, openingYear, semester);
				saveSchedule(lecture, DayOfWeek.MON, Period.FOUR, Period.SIX);

				// when
				EnrolledLecture enrolledLecture = enrollmentService.enrollLecture(student.getId(), lecture.getId());

				// then
				assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
			}),
			dynamicTest("화요일 6-9교시 수업 수강 신청", () -> {
				Subject subject = saveSubject("금속공예기초", 4);
				Lecture lecture = saveLecture(department, subject, openingYear, semester);
				saveSchedule(lecture, DayOfWeek.TUE, Period.SIX, Period.NINE);

				// when
				EnrolledLecture enrolledLecture = enrollmentService.enrollLecture(student.getId(), lecture.getId());

				// then
				assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
			}),
			dynamicTest("수요일 2-4교시 수업 수강 신청", () -> {
				Subject subject = saveSubject("창의적사고", 4);
				Lecture lecture = saveLecture(department, subject, openingYear, semester);
				saveSchedule(lecture, DayOfWeek.WED, Period.TWO, Period.FOUR);

				// when
				EnrolledLecture enrolledLecture = enrollmentService.enrollLecture(student.getId(), lecture.getId());

				// then
				assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
			}),
			dynamicTest("목요일 2-4교시 수업 수강 신청", () -> {
				Subject subject = saveSubject("철학개론", 4);
				Lecture lecture = saveLecture(department, subject, openingYear, semester);
				saveSchedule(lecture, DayOfWeek.THU, Period.TWO, Period.FOUR);

				// when
				EnrolledLecture enrolledLecture = enrollmentService.enrollLecture(student.getId(), lecture.getId());

				// then
				assertThat(enrolledLecture.getEnrolledLectureId()).isEqualTo(lecture.getId());
			})
		);
	}

	@Test
	@DisplayName("해당 학기 내 수강 신청을 취소한다")
	void cancelEnrollmentSuccess() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Subject subject = saveSubject("미술사", 2);

		Lecture lecture = saveLecture(department, subject, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture, DayOfWeek.MON, Period.ONE, Period.THREE);

		enrollmentService.enrollLecture(student.getId(), lecture.getId());  // 강의 수강 신청 완료

		// when
		enrollmentService.cancel(student.getId(), lecture.getId());  // 수강 신청 취소 시

		// then
		assertThatCode(() -> enrollmentService.enrollLecture(student.getId(), lecture.getId()))
			.doesNotThrowAnyException();  // 다시 수강 신청해도 예외가 발생하지 않는다
	}

	@Test
	@DisplayName("신청한 적 없는 강의를 취소하려는 경우 오류 메세지를 응답한다")
	void cancelEnrollmentFail() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);
		Subject subject = saveSubject("미술사", 2);

		Lecture lecture = saveLecture(department, subject, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture, DayOfWeek.MON, Period.ONE, Period.THREE);

		// when & then
		assertThatThrownBy(() -> enrollmentService.cancel(student.getId(), lecture.getId()))
			.isInstanceOf(EnrollmentNotFoundException.class)
			.hasMessage(ErrorType.NONEXISTENT_ENROLLMENT.getMessage());
	}

	@Test
	@DisplayName("해당 학기 내 수강 신청 내역을 조회한다")
	void fetchEnrollmentsSuccess() {
		// given
		Department department = saveDepartment();
		Student student = saveStudent(department);

		Subject subject1 = saveSubject("미술사", 2);
		Lecture lecture1 = saveLecture(department, subject1, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture1, DayOfWeek.MON, Period.ONE, Period.THREE);

		Subject subject2 = saveSubject("창의적사고", 2);
		Lecture lecture2 = saveLecture(department, subject2, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture2, DayOfWeek.THU, Period.ONE, Period.THREE);

		Subject subject3 = saveSubject("철학개론", 3);
		Lecture lecture3 = saveLecture(department, subject3, Year.of(2024), Semester.FIRST);
		saveSchedule(lecture3, DayOfWeek.FRI, Period.ONE, Period.THREE);

		enrollmentService.enrollLecture(student.getId(), lecture1.getId());  // 수강 신청 1
		enrollmentService.enrollLecture(student.getId(), lecture2.getId());  // 수강 신청 2
		enrollmentService.enrollLecture(student.getId(), lecture3.getId());  // 수강 신청 3

		// when
		EnrolledLectures enrolledLectures = enrollmentService.fetchAll(student.getId());

		// then
		assertThat(enrolledLectures.getEnrolledLectures()).hasSize(3);
	}

	private Department saveDepartment() {
		Department department = new Department("금속공예디자인학과");
		entityManager.persist(department);

		return department;
	}

	private Student saveStudent(Department department) {
		Student student = Student.builder()
			.studentId("171000962")
			.name("김서연")
			.grade(Grade.SENIOR)
			.department(department)
			.build();
		entityManager.persist(student);

		return student;
	}

	private Subject saveSubject(String name, Integer credits) {
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

	private Lecture saveLecture(Department department, Subject subject, Year openingYear, Semester semester) {
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
			.totalCapacity(20)
			.build();

		return lectureRepository.save(lecture);
	}

	private void saveSchedule(Lecture lecture, DayOfWeek dayOfWeek, Period firstPeriod, Period lastPeriod) {
		entityManager.flush();
		entityManager.clear();

		Schedule schedule = Schedule.builder()
			.lecture(lecture)
			.dayOfWeek(dayOfWeek)
			.firstPeriod(firstPeriod)
			.lastPeriod(lastPeriod)
			.build();
		entityManager.persist(schedule);
	}

}
