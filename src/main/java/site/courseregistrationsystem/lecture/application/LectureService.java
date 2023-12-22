package site.courseregistrationsystem.lecture.application;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.lecture.dto.LectureDetail;
import site.courseregistrationsystem.lecture.dto.LectureSchedulePage;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.subject.SubjectDivision;

@Service
@RequiredArgsConstructor
public class LectureService {

	private final LectureRepository lectureRepository;

	@Transactional(readOnly = true)
	public LectureSchedulePage fetch(Pageable pageable, SubjectDivision subjectDivision, Long departmentId,
		String subjectName) {
		return new LectureSchedulePage(
			lectureRepository.findMatchedLectures(pageable, subjectDivision, departmentId, subjectName)
				.map(LectureDetail::new));
	}

}
