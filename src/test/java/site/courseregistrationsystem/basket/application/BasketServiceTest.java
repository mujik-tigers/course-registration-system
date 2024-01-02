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
		Subject subject = createMockSubject("선형대수학");
		entityManager.persist(subject);

		Student savedStudent = studentRepository.save(createMockStudent());
		Lecture savedLecture = lectureRepository.save(createMockLecture(subject));

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
		Subject subject = createMockSubject("선형대수학");
		entityManager.persist(subject);

		Student savedStudent = studentRepository.save(createMockStudent());
		Lecture savedLecture = lectureRepository.save(createMockLecture(subject));

		basketRepository.save(createBasket(savedStudent, savedLecture));

		Lecture duplicateSubjectLecture = lectureRepository.save(createMockLecture(subject));

		// when & then
		assertThatThrownBy(() -> basketService.addLectureToBasket(savedStudent.getId(), duplicateSubjectLecture.getId()))
			.isInstanceOf(DuplicateBasketException.class);
	}

	private Student createMockStudent() {
		return Student.builder().build();
	}

	private Subject createMockSubject(String name) {
		return Subject.builder()
			.name(name)
			.credits(3)
			.build();
	}

	private Lecture createMockLecture(Subject subject) {
		return Lecture.builder()
			.lectureNumber(012345)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.subject(subject)
			.build();
	}

	private Basket createBasket(Student savedStudent, Lecture savedLecture) {
		return Basket.builder()
			.student(savedStudent)
			.lecture(savedLecture)
			.build();
	}

}
