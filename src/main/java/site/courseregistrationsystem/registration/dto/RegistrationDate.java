package site.courseregistrationsystem.registration.dto;

import java.time.Year;

import lombok.Getter;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.lecture.Semester;

@Getter
public class RegistrationDate {

	private final Year year;
	private final Semester semester;

	public RegistrationDate(CurrentYearAndSemester currentYearAndSemester) {
		this.year = currentYearAndSemester.fetchIntYearToObject();
		this.semester = currentYearAndSemester.fetchStringSemesterToObject();
	}

}
