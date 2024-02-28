package site.courseregistrationsystem.lecture.application;

import java.time.Year;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.basket.infrastructure.BasketRepository;
import site.courseregistrationsystem.exception.enrollment.LectureNotInCurrentSemesterException;
import site.courseregistrationsystem.exception.lecture.LectureNotFoundException;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
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

	@Cacheable(value = "lecture", key = "T(site.courseregistrationsystem.util.ProjectConstant).LECTURE_NO_OPTION_FIRST_PAGE",
		condition = "#lectureFilterOptions.fetchNoOptionFirstPage()")
	public LectureSchedulePage fetchLectureSchedule(Pageable pageable, LectureFilterOptions lectureFilterOptions) {
		return new LectureSchedulePage(
			lectureRepository.findMatchedLectures(
					pageable,
					lectureFilterOptions.getOpeningYear(),
					lectureFilterOptions.getSemester(),
					lectureFilterOptions.getSubjectDivision(),
					lectureFilterOptions.getDepartmentId(),
					lectureFilterOptions.getSubjectName())
				.map(LectureDetail::new));
	}

	public BasketStoringCount fetchBasketStoringCount(Year year, Semester semester, Long lectureId) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(LectureNotFoundException::new);

		checkLectureInCurrentSemester(year, semester, lecture);

		int basketCount = basketRepository.countByLecture(lecture);
		return new BasketStoringCount(lecture.getTotalCapacity(), basketCount);
	}

	private void checkLectureInCurrentSemester(Year year, Semester semester, Lecture lecture) {
		if (!lecture.hasSameSemester(year, semester)) {
			throw new LectureNotInCurrentSemesterException();
		}
	}

}
