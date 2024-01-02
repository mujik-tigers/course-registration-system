package site.courseregistrationsystem.basket.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.Aes256Manager;

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
	private Aes256Manager aes256Manager;

	@Test
	@DisplayName("학생은 원하는 수업을 선택하여 수강 바구니에 담은 뒤, 담은 수업(Lecture)의 id 를 반환한다.")
	void addLectureToBasket() throws Exception {
		// given
		Student savedStudent = studentRepository.save(createMockStudent());
		Lecture savedLecture = lectureRepository.save(createMockLecture());

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

	private static Student createMockStudent() {
		return Student.builder().build();
	}

	private static Lecture createMockLecture() {
		return Lecture.builder()
			.lectureNumber(012345)
			.lectureRoom("법학관301")
			.totalCapacity(40)
			.build();
	}

}
