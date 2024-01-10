package site.courseregistrationsystem.basket.dto;

import lombok.Getter;
import site.courseregistrationsystem.basket.Basket;
import site.courseregistrationsystem.lecture.Lecture;

@Getter
public class BasketDetail {

	private final int targetGrade;
	private final String subjectDivision;
	private final Integer lectureNumber;
	private final String subjectName;
	private final Integer credits;
	private final Integer hoursPerWeek;
	private final String schedule;
	private final String professorName;
	private final Integer totalCapacity;

	public BasketDetail(Basket basket) {
		Lecture lecture = basket.getLecture();
		this.targetGrade = lecture.fetchTargetGradeNumber();
		this.subjectDivision = lecture.fetchSubjectDivisionDescription();
		this.lectureNumber = lecture.getLectureNumber();
		this.subjectName = lecture.fetchSubjectName();
		this.credits = lecture.fetchCredits();
		this.hoursPerWeek = lecture.fetchHoursPerWeek();
		this.schedule = lecture.generateSchedule();
		this.professorName = lecture.fetchProfessorName();
		this.totalCapacity = lecture.getTotalCapacity();
	}

}