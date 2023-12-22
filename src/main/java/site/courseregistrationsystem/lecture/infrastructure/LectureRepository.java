package site.courseregistrationsystem.lecture.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.subject.SubjectDivision;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

	@Query(value =
		"SELECT l FROM Lecture l "
			+ "JOIN FETCH l.subject s "
			+ "JOIN FETCH s.department d "
			+ "JOIN FETCH l.professor "
			+ "JOIN FETCH l.schedules sc "
			+ "WHERE (:subjectDivision IS NULL OR s.subjectDivision = :subjectDivision) "
			+ "AND (:departmentId IS NULL OR d.id = :departmentId) "
			+ "AND (:subjectName IS NULL OR s.name LIKE %:subjectName%) "
	)
	Page<Lecture> findMatchedLectures(Pageable pageable, SubjectDivision subjectDivision, Long departmentId,
		String subjectName);

}
