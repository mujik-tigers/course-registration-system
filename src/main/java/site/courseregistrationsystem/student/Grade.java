package site.courseregistrationsystem.student;

import lombok.Getter;

@Getter
public enum Grade {

	FRESHMAN(1), SOPHOMORE(2), JUNIOR(3), SENIOR(4), COMMON(0);

	private final int gradeNumber;

	Grade(int gradeNumber) {
		this.gradeNumber = gradeNumber;
	}

}
