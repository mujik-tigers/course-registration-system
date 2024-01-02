package site.courseregistrationsystem.enrollment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.student.Student;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY)
	private Lecture lecture;

	@Builder
	private Enrollment(Student student, Lecture lecture) {
		this.student = student;
		this.lecture = lecture;
	}

	public Long fetchLectureId() {
		return lecture.getId();
	}

	public Long fetchSubjectId() {
		return lecture.fetchSubjectId();
	}

	public int fetchCredits() {
		return lecture.fetchCredits();
	}

}
