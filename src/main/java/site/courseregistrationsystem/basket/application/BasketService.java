package site.courseregistrationsystem.basket.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.dto.BasketDetail;
import site.courseregistrationsystem.basket.dto.BasketList;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.basket.DuplicateBasketException;
import site.courseregistrationsystem.exception.basket.NonexistenceBasketException;
import site.courseregistrationsystem.exception.credit.CreditLimitExceededException;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.exception.schedule.ScheduleConflictException;
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
		Student student = getStudent(studentPk);

		Lecture lectureForBasket = lectureRepository.findById(lectureId)
			.orElseThrow(NonexistenceLectureException::new);

		List<Basket> baskets = basketRepository.findAllByStudent(student);

		checkSubjectInBasketDuplicated(baskets, lectureForBasket);
		checkCreditLimitExceeded(baskets, lectureForBasket);
		checkScheduleConflict(baskets, lectureForBasket);

		Basket basket = Basket.builder()
			.student(student)
			.lecture(lectureForBasket)
			.build();

		basketRepository.save(basket);

		return lectureForBasket.getId();
	}

	public BasketList fetchBaskets(Long studentPk) {
		Student student = getStudent(studentPk);

		List<Basket> baskets = basketRepository.findAllByStudent(student);
		List<BasketDetail> basketDetails = baskets.stream()
			.map(BasketDetail::new)
			.toList();

		return new BasketList(basketDetails);
	}

	@Transactional
	public Long deleteBasket(Long studentPk, Long basketId) {
		Student student = getStudent(studentPk);

		Basket basket = basketRepository.findByIdAndStudent(basketId, student)
			.orElseThrow(NonexistenceBasketException::new);
		basketRepository.delete(basket);

		return basket.getId();
	}

	private Student getStudent(Long studentPk) {
		return studentRepository.findById(studentPk)
			.orElseThrow(NonexistenceLectureException::new);
	}

	private void checkSubjectInBasketDuplicated(List<Basket> baskets, Lecture lectureForBasket) {
		boolean duplicated = baskets.stream()
			.map(Basket::getLecture)
			.map(Lecture::getSubject)
			.anyMatch(subject -> lectureForBasket.getSubject().equals(subject));

		if (duplicated)
			throw new DuplicateBasketException();
	}

	private void checkCreditLimitExceeded(List<Basket> baskets, Lecture lectureForBasket) {
		int creditSum = baskets.stream()
			.map(Basket::getLecture)
			.map(Lecture::getSubject)
			.mapToInt(Subject::getCredits)
			.sum();

		if (creditSum + lectureForBasket.getSubject().getCredits() > ProjectConstant.DEFAULT_CREDIT_LIMIT) {
			throw new CreditLimitExceededException();
		}
	}

	private void checkScheduleConflict(List<Basket> baskets, Lecture lectureForBasket) {
		boolean conflict = baskets.stream()
			.map(Basket::getLecture)
			.anyMatch(lecture -> lecture.hasScheduleConflict(lectureForBasket));

		if (conflict)
			throw new ScheduleConflictException();
	}

}
