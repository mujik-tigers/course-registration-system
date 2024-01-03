package site.courseregistrationsystem.lecture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.subject.Subject;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Lecture {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer lectureNumber;
	private String lectureRoom;
	private Integer totalCapacity;

	@OneToMany(mappedBy = "lecture")
	private List<Schedule> schedules = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private Subject subject;

	@ManyToOne(fetch = FetchType.LAZY)
	private Professor professor;

	@Builder
	private Lecture(Integer lectureNumber, String lectureRoom, Integer totalCapacity, Subject subject,
		Professor professor) {
		this.lectureNumber = lectureNumber;
		this.lectureRoom = lectureRoom;
		this.totalCapacity = totalCapacity;
		this.subject = subject;
		this.professor = professor;
	}

	public String generateSchedule() {
		return schedules.stream().map(schedule ->
				schedule.getDayOfWeek().getDescription()
					+ "("
					+ schedule.getFirstPeriod().getPeriodNumber()
					+ "-"
					+ schedule.getLastPeriod().getPeriodNumber()
					+ ")")
			.collect(Collectors.joining(", "));
	}

	public String fetchSubjectDivisionDescription() {
		return subject.getSubjectDivision().getDescription();
	}

	public String fetchSubjectName() {
		return subject.getName();
	}

	public Integer fetchHoursPerWeek() {
		return subject.getHoursPerWeek();
	}

	public Integer fetchCredits() {
		return subject.getCredits();
	}

	public int fetchTargetGradeNumber() {
		return subject.getTargetGrade().getGradeNumber();
	}

	public String fetchDepartmentName() {
		return subject.getDepartment().getName();
	}

	public String fetchProfessorName() {
		return professor.getName();
	}

	public boolean hasScheduleConflict(Lecture lectureToAdd) {
		return this.schedules.stream()
			.anyMatch(schedule -> lectureToAdd.getSchedules().stream().anyMatch(scheduleToAdd -> schedule.hasConflictWith(scheduleToAdd)));
	}

}
