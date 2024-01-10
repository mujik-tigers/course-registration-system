package site.courseregistrationsystem.basket.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.dto.BasketDetail;
import site.courseregistrationsystem.basket.dto.BasketList;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.NonexistenceBasketException;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.professor.Professor;
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

	@Test
	@DisplayName("학생이 수강 바구니에 원하는 수업을 성공적으로 담으면, 담은 수업(Lecture)의 id 를 반환한다.")
	void addLectureToBasket() throws Exception {
		// given
		Subject subject = create3CreditSubject("선형대수학");
		entityManager.persist(subject);

		Student savedStudent = studentRepository.save(createStudent());
		Lecture savedLecture = lectureRepository.save(createLecture(subject));

		// when
		Long basketSavedLectureId = basketService.addLectureToBasket(savedStudent.getId(), savedLecture.getId());

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
		Lecture savedLecture = lectureRepository.save(createLecture(subject));

		basketRepository.save(createBasket(savedStudent, savedLecture));

		Lecture duplicateSubjectLecture = lectureRepository.save(createLecture(subject));

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(savedStudent.getId(), duplicateSubjectLecture.getId()))
			.isInstanceOf(DuplicateBasketException.class);
	}

	@Test
	@DisplayName("한 학생은 총 18학점 상당의 수업을 담을 수 있다.")
	void addManyLectureToBasket() throws Exception {
		// given
		Student student = studentRepository.save(createStudent());
		int LECTURE_COUNT = 6;

		// when
		for (int i = 0; i < LECTURE_COUNT; i++) {                                        // 주어진 학생이 18학점의 수업을 수강바구니에 담음
			Subject subject = create3CreditSubject("선형대수학" + i);
			entityManager.persist(subject);
			Lecture lecture = lectureRepository.save(createLecture(subject));

			basketService.addLectureToBasket(student.getId(), lecture.getId());
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

		for (int i = 0; i < 6; i++) {                                        // 주어진 학생이 18학점의 수업을 수강바구니에 담음
			Subject subject = create3CreditSubject("선형대수학" + i);
			entityManager.persist(subject);
			Lecture lecture = lectureRepository.save(createLecture(subject));

			basketRepository.save(createBasket(student, lecture));
		}

		Subject subject = create3CreditSubject("법학입문");
		entityManager.persist(subject);
		Lecture lecture = lectureRepository.save(createLecture(subject));

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(student.getId(), lecture.getId()))
			.isInstanceOf(CreditLimitExceededException.class);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들의 시간표가 겹치지 않는다면 담을 수 있다.")
	void scheduleNoConflict() throws Exception {
		// given
		Student student = studentRepository.save(createStudent());

		Subject subject1 = create3CreditSubject("선형대수학");
		entityManager.persist(subject1);
		Lecture savedLecture = lectureRepository.save(createLecture(subject1));
		entityManager.persist(createSchedule(savedLecture, DayOfWeek.MON, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, savedLecture));

		Subject subject2 = create3CreditSubject("미분적분학");
		entityManager.persist(subject2);
		Lecture lectureToAdd = lectureRepository.save(createLecture(subject2));
		entityManager.persist(createSchedule(lectureToAdd, DayOfWeek.THU, Period.ONE, Period.FIVE));

		entityManager.flush();
		entityManager.clear();

		// when
		basketService.addLectureToBasket(student.getId(), lectureToAdd.getId());

		// then
		List<Basket> baskets = basketRepository.findAllByStudent(student);
		assertThat(baskets).hasSize(2);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들의 시간표가 겹친다면 담을 수 없다.")
	void scheduleConflict() throws Exception {
		// given
		Student student = studentRepository.save(createStudent());

		Subject subject1 = create3CreditSubject("선형대수학");
		entityManager.persist(subject1);
		Lecture savedLecture = lectureRepository.save(createLecture(subject1));
		entityManager.persist(createSchedule(savedLecture, DayOfWeek.MON, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, savedLecture));

		Subject subject2 = create3CreditSubject("미분적분학");
		entityManager.persist(subject2);
		Lecture lectureToAdd = lectureRepository.save(createLecture(subject2));
		entityManager.persist(createSchedule(lectureToAdd, DayOfWeek.MON, Period.FIVE, Period.NINE));

		entityManager.flush();
		entityManager.clear();

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(student.getId(), lectureToAdd.getId()))
			.isInstanceOf(ScheduleConflictException.class);
	}

	@Test
	@DisplayName("수강 바구니에 담은 수업들 목록을 가져올 수 있다.")
	void fetchBasketList() throws Exception {
		// given
		Professor professor = createProfessor();
		entityManager.persist(professor);
		Student student = studentRepository.save(createStudent());

		String SUBJECT1_NAME = "선형대수학";                                        // 주어진 student 가 `선형대수학` 수업을 수강바구니에 담는 과정
		Subject subject1 = create3CreditSubject(SUBJECT1_NAME);
		entityManager.persist(subject1);
		Lecture lecture1 = lectureRepository.save(createLecture(subject1, professor));
		entityManager.persist(createSchedule(lecture1, DayOfWeek.MON, Period.ONE, Period.FIVE));
		entityManager.persist(createBasket(student, lecture1));

		String SUBJECT2_NAME = "미분적분학";                                        // 주어진 student 가 `미분적분학` 수업을 수강바구니에 담는 과정
		Subject subject2 = create3CreditSubject(SUBJECT2_NAME);
		entityManager.persist(subject2);
		Lecture lecture2 = lectureRepository.save(createLecture(subject2, professor));
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
		Lecture lecture = lectureRepository.save(createLecture(subject));
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
		Lecture lecture = lectureRepository.save(createLecture(subject));
		Basket basket = basketRepository.save(createBasket(student1, lecture));

		Student student2 = studentRepository.save(createStudent());

		// when & then
		assertThatThrownBy(() -> basketService.deleteBasket(student2.getId(), basket.getId()))
			.isInstanceOf(NonexistenceBasketException.class);
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

	private Lecture createLecture(Subject subject) {
		return Lecture.builder()
			.lectureNumber(5349)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.build();
	}

	private Lecture createLecture(Subject subject, Professor professor) {
		return Lecture.builder()
			.lectureNumber(5349)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.professor(professor)
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

}
