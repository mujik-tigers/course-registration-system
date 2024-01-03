package site.courseregistrationsystem.student;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.department.Department;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

	public Student(String studentId, String password) {
		this.studentId = studentId;
		this.password = password;
	}

	@Builder
	public Student(String studentId, String name, Grade grade, Department department) {
		this.studentId = studentId;
		this.name = name;
		this.grade = grade;
		this.department = department;
	}

	public String fetchDepartmentName() {
		return department.getName();
	}

	public int fetchGradeNumber() {
		return grade.getGradeNumber();
	}

}
