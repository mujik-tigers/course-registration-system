package site.courseregistrationsystem.lecture.infrastructure;

import java.time.Year;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.subject.SubjectDivision;

public interface LectureRepositoryCustom {

	Page<Lecture> findMatchedLectures(Pageable pageable, Year openingYear, Semester semester,
		SubjectDivision subjectDivision, Long departmentId, String subjectName);

}
