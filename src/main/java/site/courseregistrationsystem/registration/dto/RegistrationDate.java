package site.courseregistrationsystem.registration.dto;

import java.time.Year;

import lombok.Getter;
import site.courseregistrationsystem.lecture.Semester;

@Getter
public class RegistrationDate {

	private final Year year;
	private final Semester semester;

	public RegistrationDate(int year, String semester) {
		this.year = Year.of(year);
		this.semester = Semester.valueOf(semester);
	}

}
