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

	public void setLecture(Lecture lecture) {
		this.lecture = lecture;
	}

	public boolean hasConflictWith(Schedule schedule) {
		if (this.getDayOfWeek() != schedule.getDayOfWeek()) {
			return false;
		}

		boolean isBefore = this.lastPeriod.getPeriodNumber() < schedule.firstPeriod.getPeriodNumber();
		boolean isAfter = this.firstPeriod.getPeriodNumber() > schedule.lastPeriod.getPeriodNumber();

		return !(isBefore || isAfter);
	}

}
