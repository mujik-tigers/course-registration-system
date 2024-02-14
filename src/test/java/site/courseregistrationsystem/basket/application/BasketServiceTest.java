package site.courseregistrationsystem.basket.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.dto.BasketDetail;
import site.courseregistrationsystem.basket.dto.BasketList;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.exception.basket.BasketNotFoundException;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.registration_period.InvalidBasketTimeException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.registration.dto.RegistrationDate;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class BasketServiceTest extends IntegrationTestSupport {

	@Autowired
	private BasketRepository basketRepository;

	@Autowired
	private BasketService basketService;

	@Autowired
	private EntityManager entityManager;

	private static final LocalDateTime CURRENT_REGISTRATION_TIME = LocalDateTime.of(2024, 1, 15, 9, 0, 0);

	@Test
	@DisplayName("학생이 수강 바구니에 원하는 수업을 성공적으로 담으면, 담은 수업(Lecture)의 id 를 반환한다.")
	void addLectureToBasket() throws Exception {
		// given
		Subject subject = save3CreditSubject("선형대수학");
		Student savedStudent = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = saveLecture(subject, YEAR, SEMESTER);

		// when
		Long basketSavedLectureId = basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, savedStudent.getId(), savedLecture.getId());

		// then
		List<Basket> baskets = basketRepository.findAll();
		assertThat(basketSavedLectureId).isEqualTo(savedLecture.getId());
		assertThat(baskets).hasSize(1);

		Basket savedBasket = baskets.get(0);
		assertThat(savedBasket.getStudent()).isEqualTo(savedStudent);
		assertThat(savedBasket.getLecture()).isEqualTo(savedLecture);
	}

	@Test
	@DisplayName("이미 수강 바구니에 담은 과목(Subject)은 중복하여 담을 수 없다.")
	void duplicateBasket() throws Exception {
		// given
		Subject subject = save3CreditSubject("선형대수학");

		Student savedStudent = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = saveLecture(subject, YEAR, SEMESTER);

		saveBasket(savedStudent, savedLecture);

		Lecture duplicateSubjectLecture = saveLecture(subject, YEAR, SEMESTER);

		// when & then
		assertThatThrownBy(
			() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, savedStudent.getId(), duplicateSubjectLecture.getId()))
			.isInstanceOf(DuplicateBasketException.class);
	}

	@Test
	@DisplayName("한 학생은 총 18학점 상당의 수업을 담을 수 있다.")
	void addManyLectureToBasket() throws Exception {
		// given
		Student student = saveStudent();
		int LECTURE_COUNT = 6;
		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		// when
		for (int i = 0; i < LECTURE_COUNT; i++) {                                        // 주어진 학생이 18학점의 수업을 수강바구니에 담음
			Subject subject = save3CreditSubject("선형대수학" + i);
			Lecture lecture = saveLecture(subject, YEAR, SEMESTER);

			basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, student.getId(), lecture.getId());
		}

		// then
		List<Basket> baskets = basketRepository.findAll();
		assertThat(baskets).hasSize(LECTURE_COUNT);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들의 총 학점이 한 학기 제한 학점을 넘을 수 없다.")
	void exceededDefaultCreditLimit() throws Exception {
		// given
		Student student = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		for (int i = 0; i < 6; i++) {                                        // 주어진 학생이 18학점의 수업을 수강바구니에 담음
			Subject subject = save3CreditSubject("선형대수학" + i);
			Lecture lecture = saveLecture(subject, YEAR, SEMESTER);

			saveBasket(student, lecture);
		}

		Subject subject = save3CreditSubject("법학입문");
		Lecture lecture = saveLecture(subject, YEAR, SEMESTER);

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, student.getId(), lecture.getId()))
			.isInstanceOf(CreditLimitExceededException.class);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들의 시간표가 겹치지 않는다면 담을 수 있다.")
	void scheduleNoConflict() throws Exception {
		// given
		Student student = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = saveScheduledLecture(save3CreditSubject("선형대수학"), YEAR, SEMESTER, DayOfWeek.MON, Period.ONE, Period.FIVE);
		saveBasket(student, savedLecture);

		Lecture lectureToAdd = saveScheduledLecture(save3CreditSubject("미분적분학"), YEAR, SEMESTER, DayOfWeek.THU, Period.ONE, Period.FIVE);

		entityManager.flush();
		entityManager.clear();

		// when
		basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, student.getId(), lectureToAdd.getId());

		// then
		List<Basket> baskets = basketRepository.findAllByStudent(student);
		assertThat(baskets).hasSize(2);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들의 시간표가 겹친다면 담을 수 없다.")
	void scheduleConflict() throws Exception {
		// given
		Student student = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = saveScheduledLecture(save3CreditSubject("선형대수학"), YEAR, SEMESTER, DayOfWeek.MON, Period.ONE, Period.FIVE);
		saveBasket(student, savedLecture);

		Lecture lectureToAdd = saveScheduledLecture(save3CreditSubject("미분적분학"), YEAR, SEMESTER, DayOfWeek.MON, Period.FIVE, Period.NINE);

		entityManager.flush();
		entityManager.clear();

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, student.getId(), lectureToAdd.getId()))
			.isInstanceOf(ScheduleConflictException.class);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들 목록을 가져올 수 있다.")
	void fetchBasketList() throws Exception {
		// given
		Professor professor = saveProfessor();
		Student student = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		Subject subject1 = save3CreditSubject("선형대수학");
		studentAddLectureToBasket(student, subject1, professor, YEAR, SEMESTER, DayOfWeek.MON, Period.ONE, Period.FIVE);

		Subject subject2 = save3CreditSubject("미분적분학");
		studentAddLectureToBasket(student, subject2, professor, YEAR, SEMESTER, DayOfWeek.THU, Period.ONE, Period.FIVE);

		entityManager.flush();
		entityManager.clear();

		// when
		BasketList basketList = basketService.fetchBaskets(student.getId());

		// then
		List<BasketDetail> baskets = basketList.getBaskets();
		assertThat(baskets).hasSize(2)
			.extracting("subjectName", "professorName")
			.containsExactlyInAnyOrder(
				tuple(subject1.getName(), professor.getName()),
				tuple(subject2.getName(), professor.getName())
			);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업이 없는 경우 빈 리스트를 반환한다.")
	void fetchEmptyBasketList() throws Exception {
		// given
		Student student = saveStudent();

		// when
		BasketList basketList = basketService.fetchBaskets(student.getId());

		// then
		List<BasketDetail> baskets = basketList.getBaskets();
		assertThat(baskets).isEmpty();
	}

	@Test
	@DisplayName("수강 바구니에 담긴 수업 중 하나를 삭제한다.")
	void deleteBasket() throws Exception {
		// given
		Student student = saveStudent();
		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		Basket basket = studentAddLectureToBasket(student, save3CreditSubject("미분적분학"), YEAR, SEMESTER);

		// when
		Long deleteBasketId = basketService.deleteBasket(student.getId(), basket.getId());

		// then
		assertThat(deleteBasketId).isEqualTo(basket.getId());

		List<Basket> baskets = basketRepository.findAll();
		assertThat(baskets).isEmpty();
	}

	@Test
	@DisplayName("학생 자신의 수강 바구니에 존재하지 않는 수업은 삭제할 수 없다.")
	void nonexistenceDeleteFail() throws Exception {
		// given
		Student student1 = saveStudent();
		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;
		Basket student1Basket = studentAddLectureToBasket(student1, save3CreditSubject("미분적분학"), YEAR, SEMESTER);

		Student student2 = saveStudent();

		// when & then
		assertThatThrownBy(() -> basketService.deleteBasket(student2.getId(), student1Basket.getId()))
			.isInstanceOf(BasketNotFoundException.class);
	}

	@DisplayName("수강 바구니에 담으려는 강의의 진행 학기와 현재 신청학기가 일치하지 않는 경우 해당 강의를 담을 수 없다.")
	@CsvSource({"2024,SECOND", "2025,FIRST", "2025,SECOND"})
	@ParameterizedTest
	void lectureAndCurrentSemesterDifferent(int year, String semester) throws Exception {
		// given
		Student student = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		Lecture lecture = saveLecture(save3CreditSubject("미분적분학"), YEAR, SEMESTER);

		RegistrationDate registrationDate = createRegistrationDate(Year.of(year), Semester.valueOf(semester));
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, student.getId(), lecture.getId()))
			.isInstanceOf(LectureNotInCurrentSemesterException.class);
	}

	@Test
	@DisplayName("수강 바구니 신청 기간이 아닌 경우, 강의를 수강 바구니에 담을 수 없다.")
	void invalidBasketRegistrationTime() throws Exception {
		// given
		Student savedStudent = saveStudent();

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		BDDMockito.doThrow(new InvalidBasketTimeException())
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = saveLecture(save3CreditSubject("선형대수학"), YEAR, SEMESTER);

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, savedStudent.getId(), savedLecture.getId()))
			.isInstanceOf(InvalidBasketTimeException.class);
	}

	private Student saveStudent() {
		Student student = Student.builder().build();

		entityManager.persist(student);
		return student;
	}

	private Professor saveProfessor() {
		Professor professor = new Professor("김서연");

		entityManager.persist(professor);
		return professor;
	}

	private Subject save3CreditSubject(String name) {
		Subject subject = Subject.builder()
			.name(name)
			.credits(3)
			.targetGrade(Grade.SENIOR)
			.subjectDivision(SubjectDivision.GR)
			.build();

		entityManager.persist(subject);
		return subject;
	}

	private Lecture saveLecture(Subject subject, Year year, Semester semester) {
		Lecture lecture = Lecture.builder()
			.lectureNumber(5349)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.openingYear(year)
			.semester(semester)
			.build();

		entityManager.persist(lecture);
		return lecture;
	}

	private Lecture saveLecture(Subject subject, Professor professor, Year year, Semester semester) {
		Lecture lecture = Lecture.builder()
			.lectureNumber(5349)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.professor(professor)
			.openingYear(year)
			.semester(semester)
			.build();

		entityManager.persist(lecture);
		return lecture;
	}

	private Schedule saveSchedule(Lecture lecture, DayOfWeek dayOfWeek, Period firstPeriod, Period lastPeriod) {
		Schedule schedule = Schedule.builder()
			.lecture(lecture)
			.dayOfWeek(dayOfWeek)
			.firstPeriod(firstPeriod)
			.lastPeriod(lastPeriod)
			.build();

		entityManager.persist(schedule);
		return schedule;
	}

	private Basket saveBasket(Student student, Lecture lecture) {
		Basket basket = Basket.builder()
			.student(student)
			.lecture(lecture)
			.build();

		entityManager.persist(basket);
		return basket;
	}

	private Lecture saveScheduledLecture(Subject subject, Year year, Semester semester, DayOfWeek dayOfWeek, Period startPeriod,
		Period endPeriod) {
		Lecture lecture = saveLecture(subject, year, semester);
		saveSchedule(lecture, dayOfWeek, startPeriod, endPeriod);

		return lecture;
	}

	private void studentAddLectureToBasket(Student student, Subject subject, Professor professor, Year year, Semester semester,
		DayOfWeek dayOfWeek, Period startPeriod, Period endPeriod) {

		Lecture lecture = saveLecture(subject, professor, year, semester);
		saveSchedule(lecture, dayOfWeek, startPeriod, endPeriod);
		saveBasket(student, lecture);
	}

	private Basket studentAddLectureToBasket(Student student, Subject subject, Year year, Semester semester) {
		Lecture lecture = saveLecture(subject, year, semester);
		return saveBasket(student, lecture);
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
