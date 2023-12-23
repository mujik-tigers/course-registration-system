package site.courseregistrationsystem.student.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.student.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	Optional<Student> findByStudentId(String studentId);

}
