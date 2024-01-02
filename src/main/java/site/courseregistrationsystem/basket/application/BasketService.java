package site.courseregistrationsystem.basket.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.ExceededCreditLimitException;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.util.ProjectConstant;

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

		checkSubjectInBasketDuplicated(student, lecture);
		checkCreditLimitExceeded(student, lecture);

		Basket basket = Basket.builder()
			.student(student)
			.lecture(lecture)
			.build();

		basketRepository.save(basket);

		return lecture.getId();
	}

	private void checkSubjectInBasketDuplicated(Student student, Lecture lecture) {
		List<Basket> baskets = basketRepository.findAllByStudent(student);

		boolean duplicated = baskets.stream()
			.map(Basket::getLecture)
			.map(Lecture::getSubject)
			.anyMatch(subject -> lecture.getSubject().equals(subject));

		if (duplicated)
			throw new DuplicateBasketException();
	}

	private void checkCreditLimitExceeded(Student student, Lecture lecture) {
		List<Basket> baskets = basketRepository.findAllByStudent(student);

		int creditSum = baskets.stream()
			.map(Basket::getLecture)
			.map(Lecture::getSubject)
			.mapToInt(Subject::getCredits)
			.sum();

		if (creditSum + lecture.getSubject().getCredits() > ProjectConstant.DEFAULT_CREDIT_LIMIT) {
			throw new ExceededCreditLimitException();
		}
	}

}
