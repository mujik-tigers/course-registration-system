package site.courseregistrationsystem.basket.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasketService {

	private final StudentRepository studentRepository;
	private final LectureRepository lectureRepository;
	private final BasketRepository basketRepository;

	@Transactional
	public Long addLectureToBasket(Long studentPk, Long lectureId) {
		Student student = studentRepository.findById(studentPk)
			.orElseThrow(NonexistenceStudentException::new);

		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(NonexistenceLectureException::new);

		checkBasketDuplicated(student, lecture);

		Basket basket = Basket.builder()
			.student(student)
			.lecture(lecture)
			.build();

		basketRepository.save(basket);

		return basket.getId();
	}

	private void checkBasketDuplicated(Student student, Lecture lecture) {
		if (basketRepository.existsByStudentAndLecture(student, lecture)) {
			throw new DuplicateBasketException();
		}
	}

}
