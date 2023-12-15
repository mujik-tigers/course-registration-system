package site.courseregistrationsystem.student.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.courseregistrationsystem.student.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@Query(value = "SELECT s FROM Student s WHERE s.studentId = :studentId AND s.password = :password")
	Optional<Student> findByLoginForm(String studentId, String password);

}
