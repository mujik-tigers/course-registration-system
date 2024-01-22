package site.courseregistrationsystem.clock.dto;

import java.time.Year;

import lombok.Getter;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.lecture.Semester;

@Getter
public class CurrentYearAndSemester {

	private final int year;
	private final String semester;

	public CurrentYearAndSemester(Clock clock) {
		this.year = clock.getYear();
		this.semester = clock.getSemester();
	}

	public Year fetchIntYearToObject() {
		return Year.of(year);
	}

	public Semester fetchStringSemesterToObject() {
		return Semester.valueOf(semester);
	}

}
