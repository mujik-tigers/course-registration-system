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
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.NonexistenceBasketException;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.registration_period.InvalidBasketTimeException;
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
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class BasketServiceTest extends IntegrationTestSupport {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private LectureRepository lectureRepository;

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
		Subject subject = create3CreditSubject("선형대수학");
		entityManager.persist(subject);

		Student savedStudent = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

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
		Subject subject = create3CreditSubject("선형대수학");
		entityManager.persist(subject);

		Student savedStudent = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

		basketRepository.save(createBasket(savedStudent, savedLecture));

		Lecture duplicateSubjectLecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

		// when & then
		assertThatThrownBy(
			() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, savedStudent.getId(), duplicateSubjectLecture.getId()))
			.isInstanceOf(DuplicateBasketException.class);
	}

	@Test
	@DisplayName("한 학생은 총 18학점 상당의 수업을 담을 수 있다.")
	void addManyLectureToBasket() throws Exception {
		// given
		Student student = studentRepository.save(createStudent());
		int LECTURE_COUNT = 6;
		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		// when
		for (int i = 0; i < LECTURE_COUNT; i++) {                                        // 주어진 학생이 18학점의 수업을 수강바구니에 담음
			Subject subject = create3CreditSubject("선형대수학" + i);
			entityManager.persist(subject);
			Lecture lecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

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
		Student student = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		for (int i = 0; i < 6; i++) {                                        // 주어진 학생이 18학점의 수업을 수강바구니에 담음
			Subject subject = create3CreditSubject("선형대수학" + i);
			entityManager.persist(subject);
			Lecture lecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

			basketRepository.save(createBasket(student, lecture));
		}

		Subject subject = create3CreditSubject("법학입문");
		entityManager.persist(subject);
		Lecture lecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, student.getId(), lecture.getId()))
			.isInstanceOf(CreditLimitExceededException.class);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들의 시간표가 겹치지 않는다면 담을 수 있다.")
	void scheduleNoConflict() throws Exception {
		// given
		Student student = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Subject subject1 = create3CreditSubject("선형대수학");
		entityManager.persist(subject1);
		Lecture savedLecture = lectureRepository.save(createLecture(subject1, YEAR, SEMESTER));
		entityManager.persist(createSchedule(savedLecture, DayOfWeek.MON, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, savedLecture));

		Subject subject2 = create3CreditSubject("미분적분학");
		entityManager.persist(subject2);
		Lecture lectureToAdd = lectureRepository.save(createLecture(subject2, YEAR, SEMESTER));
		entityManager.persist(createSchedule(lectureToAdd, DayOfWeek.THU, Period.ONE, Period.FIVE));

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
		Student student = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		RegistrationDate registrationDate = createRegistrationDate(YEAR, SEMESTER);
		BDDMockito.doReturn(registrationDate)
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Subject subject1 = create3CreditSubject("선형대수학");
		entityManager.persist(subject1);
		Lecture savedLecture = lectureRepository.save(createLecture(subject1, YEAR, SEMESTER));
		entityManager.persist(createSchedule(savedLecture, DayOfWeek.MON, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, savedLecture));

		Subject subject2 = create3CreditSubject("미분적분학");
		entityManager.persist(subject2);
		Lecture lectureToAdd = lectureRepository.save(createLecture(subject2, YEAR, SEMESTER));
		entityManager.persist(createSchedule(lectureToAdd, DayOfWeek.MON, Period.FIVE, Period.NINE));

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
		Professor professor = createProfessor();
		entityManager.persist(professor);
		Student student = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		String SUBJECT1_NAME = "선형대수학";                                        // 주어진 student 가 `선형대수학` 수업을 수강바구니에 담는 과정
		Subject subject1 = create3CreditSubject(SUBJECT1_NAME);
		entityManager.persist(subject1);
		Lecture lecture1 = lectureRepository.save(createLecture(subject1, professor, YEAR, SEMESTER));
		entityManager.persist(createSchedule(lecture1, DayOfWeek.MON, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, lecture1));

		String SUBJECT2_NAME = "미분적분학";                                        // 주어진 student 가 `미분적분학` 수업을 수강바구니에 담는 과정
		Subject subject2 = create3CreditSubject(SUBJECT2_NAME);
		entityManager.persist(subject2);
		Lecture lecture2 = lectureRepository.save(createLecture(subject2, professor, YEAR, SEMESTER));
		entityManager.persist(createSchedule(lecture2, DayOfWeek.THU, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, lecture2));

		entityManager.flush();
		entityManager.clear();

		// when
		BasketList basketList = basketService.fetchBaskets(student.getId());

		// then
		List<BasketDetail> baskets = basketList.getBaskets();
		assertThat(baskets).hasSize(2)
			.extracting("subjectName", "professorName")
			.containsExactlyInAnyOrder(
				tuple(SUBJECT1_NAME, professor.getName()),
				tuple(SUBJECT2_NAME, professor.getName())
			);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업이 없는 경우 빈 리스트를 반환한다.")
	void fetchEmptyBasketList() throws Exception {
		// given
		Student student = studentRepository.save(createStudent());

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
		Student student = studentRepository.save(createStudent());
		Subject subject = create3CreditSubject("미분적분학");
		entityManager.persist(subject);

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;
		Lecture lecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));
		Basket basket = basketRepository.save(createBasket(student, lecture));

		// when
		Long deleteBasketId = basketService.deleteBasket(student.getId(), basket.getId());

		// then
		assertThat(deleteBasketId).isEqualTo(basket.getId());

		List<Basket> baskets = basketRepository.findAll();
		assertThat(baskets).isEmpty();
	}

	@Test
	@DisplayName("학생의 수강 바구니에 존재하지 않는 수업은 삭제할 수 없다.")
	void nonexistenceDeleteFail() throws Exception {
		// given
		Student student1 = studentRepository.save(createStudent());
		Subject subject = create3CreditSubject("미분적분학");
		entityManager.persist(subject);

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;
		Lecture lecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));
		Basket basket = basketRepository.save(createBasket(student1, lecture));

		Student student2 = studentRepository.save(createStudent());

		// when & then
		assertThatThrownBy(() -> basketService.deleteBasket(student2.getId(), basket.getId()))
			.isInstanceOf(NonexistenceBasketException.class);
	}

	@DisplayName("수강 바구니에 담으려는 강의의 진행 학기와 현재 신청학기가 일치하지 않는 경우 해당 강의를 담을 수 없다.")
	@CsvSource({"2024,SECOND", "2025,FIRST", "2025,SECOND"})
	@ParameterizedTest
	void lectureAndCurrentSemesterDifferent(int year, String semester) throws Exception {
		// given
		Student student = studentRepository.save(createStudent());
		Subject subject = create3CreditSubject("미분적분학");
		entityManager.persist(subject);

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		Lecture lecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

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
		Subject subject = create3CreditSubject("선형대수학");
		entityManager.persist(subject);

		Student savedStudent = studentRepository.save(createStudent());

		Year YEAR = Year.of(2024);
		Semester SEMESTER = Semester.FIRST;

		BDDMockito.doThrow(new InvalidBasketTimeException())
			.when(basketRegistrationPeriodService)
			.validateBasketRegistrationPeriod(any());

		Lecture savedLecture = lectureRepository.save(createLecture(subject, YEAR, SEMESTER));

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(CURRENT_REGISTRATION_TIME, savedStudent.getId(), savedLecture.getId()))
			.isInstanceOf(InvalidBasketTimeException.class);
	}

	private Student createStudent() {
		return Student.builder().build();
	}

	private Professor createProfessor() {
		return new Professor("김서연");
	}

	private Subject create3CreditSubject(String name) {
		return Subject.builder()
			.name(name)
			.credits(3)
			.targetGrade(Grade.SENIOR)
			.subjectDivision(SubjectDivision.GR)
			.build();
	}

	private Lecture createLecture(Subject subject, Year year, Semester semester) {
		return Lecture.builder()
			.lectureNumber(5349)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.openingYear(year)
			.semester(semester)
			.build();
	}

	private Lecture createLecture(Subject subject, Professor professor, Year year, Semester semester) {
		return Lecture.builder()
			.lectureNumber(5349)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.professor(professor)
			.openingYear(year)
			.semester(semester)
			.build();
	}

	private Schedule createSchedule(Lecture lecture, DayOfWeek dayOfWeek, Period firstPeriod, Period lastPeriod) {
		return Schedule.builder()
			.lecture(lecture)
			.dayOfWeek(dayOfWeek)
			.firstPeriod(firstPeriod)
			.lastPeriod(lastPeriod)
			.build();
	}

	private Basket createBasket(Student student, Lecture lecture) {
		return Basket.builder()
			.student(student)
			.lecture(lecture)
			.build();
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
