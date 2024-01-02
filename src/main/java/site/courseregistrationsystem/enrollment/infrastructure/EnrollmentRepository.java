package site.courseregistrationsystem.enrollment.infrastructure;

import java.time.Year;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.lecture.Semester;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	@Query("SELECT e FROM Enrollment e "
		+ "JOIN e.student s "
		+ "JOIN FETCH e.lecture l "
		+ "JOIN FETCH l.schedules "
		+ "JOIN FETCH l.subject ls "
		+ "WHERE s.id = :studentPk "
		+ "AND l.openingYear = :openingYear "
		+ "AND l.semester = :semester")
	List<Enrollment> findAllInCurrentSemester(Long studentPk, Year openingYear, Semester semester);

}
