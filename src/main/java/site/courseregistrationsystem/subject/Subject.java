package site.courseregistrationsystem.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.student.Grade;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Department department;

	@Enumerated(EnumType.STRING)
	private SubjectDivision subjectDivision;

	@Enumerated(EnumType.STRING)
	private Grade targetGrade;

	private String name;
	private Integer hoursPerWeek;
	private Integer credits;

	public Subject(Department department, SubjectDivision subjectDivision, Grade targetGrade, String name,
		Integer hoursPerWeek, Integer credits) {
		this.department = department;
		this.subjectDivision = subjectDivision;
		this.targetGrade = targetGrade;
		this.name = name;
		this.hoursPerWeek = hoursPerWeek;
		this.credits = credits;
	}

}
