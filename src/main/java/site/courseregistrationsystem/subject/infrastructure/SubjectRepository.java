package site.courseregistrationsystem.subject.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.subject.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

}
