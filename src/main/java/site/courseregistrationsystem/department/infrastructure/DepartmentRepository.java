package site.courseregistrationsystem.department.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.department.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
