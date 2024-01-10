package site.courseregistrationsystem.lecture;

import java.time.LocalDate;

import lombok.Getter;
import site.courseregistrationsystem.exception.semester.SemesterInvalidException;

@Getter
public enum Semester {

	FIRST, SECOND;

	public static Semester getCurrentSemester() {
		int nowMonthValue = LocalDate.now().getMonthValue();

		if (2 <= nowMonthValue && nowMonthValue <= 4) {
			return FIRST;
		}

		if (8 <= nowMonthValue && nowMonthValue <= 10) {
			return SECOND;
		}

		throw new SemesterInvalidException();
	}

}
