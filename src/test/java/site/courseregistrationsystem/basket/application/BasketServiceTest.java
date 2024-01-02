package site.courseregistrationsystem.basket.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.ExceededCreditLimitException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.subject.Subject;

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
	@DisplayName("학생은 원하는 수업을 선택하여 수강 바구니에 담은 뒤, 담은 수업(Lecture)의 id 를 반환한다.")
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
			.isInstanceOf(ExceededCreditLimitException.class);
	}

	private Student createStudent() {
		return Student.builder().build();
	}

	private Subject create3CreditSubject(String name) {
		return Subject.builder()
			.name(name)
			.credits(3)
			.build();
	}

	private Lecture createLecture(Subject subject) {
		return Lecture.builder()
			.lectureNumber(012345)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.build();
	}

	private Basket createBasket(Student student, Lecture lecture) {
		return Basket.builder()
			.student(student)
			.lecture(lecture)
			.build();
	}

}
