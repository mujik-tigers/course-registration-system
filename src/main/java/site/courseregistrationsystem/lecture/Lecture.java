package site.courseregistrationsystem.lecture;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.subject.Subject;

@Entity
public class Lecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer lectureNumber;
	private String lectureRoom;
	private LocalDateTime lectureStartTime;
	private LocalDateTime lectureEndTime;
	private Integer totalCapacity;

	@ManyToOne(fetch = FetchType.LAZY)
	private Subject subject;

	@ManyToOne(fetch = FetchType.LAZY)
	private Professor professor;

}
