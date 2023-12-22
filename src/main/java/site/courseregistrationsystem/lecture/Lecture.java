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
import lombok.Getter;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.subject.Subject;

@Entity
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

}
