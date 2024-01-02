package site.courseregistrationsystem.enrollment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.student.Student;

@Entity
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY)
	private Lecture lecture;

}
