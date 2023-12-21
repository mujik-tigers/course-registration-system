package site.courseregistrationsystem.lecture.dto;

import java.time.LocalTime;

import lombok.Getter;
import site.courseregistrationsystem.lecture.Lecture;

@Getter
public class LectureDetail {

	private final Long id;
	private final Integer lectureNumber;
	private final String subjectDivision;
	private final String subjectName;
	private final Integer hoursPerWeek;
	private final Integer credits;
	private final int targetGrade;
	private final String departmentName;
	private final String professorName;
	private final LocalTime lectureStartTime;
	private final LocalTime lectureEndTime;
	private final Integer totalCapacity;

	public LectureDetail(Lecture lecture) {
		id = lecture.getId();
		lectureNumber = lecture.getLectureNumber();
		subjectDivision = lecture.getSubject().getSubjectDivision().getDescription();
		subjectName = lecture.getSubject().getName();
		hoursPerWeek = lecture.getSubject().getHoursPerWeek();
		credits = lecture.getSubject().getCredits();
		targetGrade = lecture.getSubject().getTargetGrade().getNumber();
		departmentName = lecture.getSubject().getDepartment().getName();
		professorName = lecture.getProfessor().getName();
		lectureStartTime = lecture.getLectureStartTime();
		lectureEndTime = lecture.getLectureEndTime();
		totalCapacity = lecture.getTotalCapacity();
	}

}
