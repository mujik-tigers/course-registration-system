package site.courseregistrationsystem.professor.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.professor.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

}
