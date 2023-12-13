package site.courseregistrationsystem.student;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import site.courseregistrationsystem.department.Department;

@Entity
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String studentId;
	private String password;
	private String name;

	@Enumerated(EnumType.STRING)
	private Grade grade;

	@ManyToOne(fetch = FetchType.LAZY)
	private Department department;

}
