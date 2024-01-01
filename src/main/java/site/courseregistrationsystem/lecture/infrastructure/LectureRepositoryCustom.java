package site.courseregistrationsystem.lecture.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.subject.SubjectDivision;

public interface LectureRepositoryCustom {

	Page<Lecture> findMatchedLectures(Pageable pageable, SubjectDivision subjectDivision, Long departmentId, String subjectName);

}
