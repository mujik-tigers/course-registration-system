package site.courseregistrationsystem.lecture;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public enum Semester {

	FIRST, SECOND;

	public static Semester getCurrentSemester() {
		int nowMonthValue = LocalDate.now().getMonthValue();

		if (nowMonthValue <= 6) {
			return FIRST;
		}

		return SECOND;
	}

}
