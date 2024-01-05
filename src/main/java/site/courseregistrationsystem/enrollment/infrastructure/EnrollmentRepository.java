package site.courseregistrationsystem.enrollment.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import site.courseregistrationsystem.enrollment.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	@Query("SELECT e FROM Enrollment e "
		+ "JOIN e.student s "
		+ "JOIN FETCH e.lecture l "
		+ "JOIN FETCH l.schedules "
		+ "JOIN FETCH l.subject ls "
		+ "WHERE s.id = :studentPk")
	List<Enrollment> findAllBy(Long studentPk);

	@Modifying
	@Query("DELETE FROM Enrollment e "
		+ "WHERE e.student.id = :studentPk "
		+ "AND e.lecture.id = :lectureId")
	int deleteEnrollment(Long studentPk, Long lectureId);

}
