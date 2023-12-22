package site.courseregistrationsystem.student;

public enum Grade {

	FRESHMAN(1), SOPHOMORE(2), JUNIOR(3), SENIOR(4);

	private final int gradeNumber;

	Grade(int grade) {
		this.gradeNumber = grade;
	}

	public int getGradeNumber() {
		return gradeNumber;
	}

}
