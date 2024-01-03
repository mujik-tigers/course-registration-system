package site.courseregistrationsystem.schedule;

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
import site.courseregistrationsystem.lecture.Lecture;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Lecture lecture;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;

	@Enumerated(EnumType.STRING)
	private Period firstPeriod;

	@Enumerated(EnumType.STRING)
	private Period lastPeriod;

	@Builder
	private Schedule(Lecture lecture, DayOfWeek dayOfWeek, Period firstPeriod, Period lastPeriod) {
		this.lecture = lecture;
		this.dayOfWeek = dayOfWeek;
		this.firstPeriod = firstPeriod;
		this.lastPeriod = lastPeriod;
	}

	public boolean hasConflictWith(Schedule schedule) {
		boolean dayOfWeekConflict = this.getDayOfWeek() == schedule.getDayOfWeek();
		boolean scheduleConflict = !((this.firstPeriod.getPeriodNumber() > schedule.lastPeriod.getPeriodNumber()) ||
			(this.lastPeriod.getPeriodNumber() < schedule.firstPeriod.getPeriodNumber()));

		return dayOfWeekConflict && scheduleConflict;
	}

}
