package site.courseregistrationsystem.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import site.courseregistrationsystem.student.Grade;

@Entity
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private SubjectDivision subjectDivision;

	@Enumerated(EnumType.STRING)
	private Grade targetGrade;

	private String name;
	private Integer hoursPerWeek;
	private Integer credits;
	
}
