package site.courseregistrationsystem.lecture.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.lecture.NonexistenceLectureException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.dto.BasketStoringCount;
import site.courseregistrationsystem.lecture.dto.LectureDetail;
import site.courseregistrationsystem.lecture.dto.LectureFilterOptions;
import site.courseregistrationsystem.lecture.dto.LectureSchedulePage;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureService {

	private final LectureRepository lectureRepository;
	private final BasketRepository basketRepository;

	public LectureSchedulePage fetchLectureSchedule(Pageable pageable, LectureFilterOptions lectureFilterOptions) {
		return new LectureSchedulePage(
			lectureRepository.findMatchedLectures(
					pageable,
					lectureFilterOptions.getSubjectDivision(),
					lectureFilterOptions.getDepartmentId(),
					lectureFilterOptions.getSubjectName())
				.map(LectureDetail::new));
	}

	public BasketStoringCount fetchBasketStoringCount(Long lectureId) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(NonexistenceLectureException::new);

		List<Basket> baskets = basketRepository.findAllByLecture(lecture);

		return new BasketStoringCount(lecture.getTotalCapacity(), baskets.size());
	}

}
