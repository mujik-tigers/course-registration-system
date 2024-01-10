package site.courseregistrationsystem.enrollment.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.lecture.Lecture;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	@Query("SELECT e FROM Enrollment e "
		+ "JOIN e.student s "
		+ "JOIN FETCH e.lecture l "
		+ "JOIN FETCH l.schedules "
		+ "JOIN FETCH l.subject ls "
		+ "WHERE s.id = :studentPk")
	List<Enrollment> findAllBy(Long studentPk);

	int countByLecture(Lecture lecture);

	@Modifying
	@Query("DELETE FROM Enrollment e "
		+ "WHERE e.id = :enrollmentId "
		+ "AND e.student.id = :studentPk ")
	int deleteByIdAndStudent(Long enrollmentId, Long studentPk);

}
