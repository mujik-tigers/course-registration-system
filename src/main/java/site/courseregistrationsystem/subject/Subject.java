package site.courseregistrationsystem.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.student.Grade;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

	@Builder
	private Subject(SubjectDivision subjectDivision, Grade targetGrade, String name,
		Integer hoursPerWeek, Integer credits) {
		this.subjectDivision = subjectDivision;
		this.targetGrade = targetGrade;
		this.name = name;
		this.hoursPerWeek = hoursPerWeek;
		this.credits = credits;
	}

}
