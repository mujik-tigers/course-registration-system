package site.courseregistrationsystem.lecture;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public enum Semester {

	FIRST, SECOND;

	public static Semester getCurrentSemester() {
		return LocalDate.now().getMonthValue() < 4 ? Semester.FIRST : Semester.SECOND;
	}

}
