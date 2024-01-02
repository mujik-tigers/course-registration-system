package site.courseregistrationsystem.enrollment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.enrollment.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
