package site.courseregistrationsystem.enrollment.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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

	@Query("SELECT e FROM Enrollment e "
		+ "JOIN FETCH e.student s "
		+ "WHERE e.id = :enrollmentId")
	Optional<Enrollment> findByIdWithStudent(Long enrollmentId);

}
