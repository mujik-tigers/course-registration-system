package site.courseregistrationsystem.lecture.application;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.lecture.dto.LectureDetail;
import site.courseregistrationsystem.lecture.dto.LectureFilterOptions;
import site.courseregistrationsystem.lecture.dto.LectureSchedulePage;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;

@Service
@RequiredArgsConstructor
public class LectureService {

	private final LectureRepository lectureRepository;

	@Transactional(readOnly = true)
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

}
