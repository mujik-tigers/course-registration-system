package site.courseregistrationsystem.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.courseregistrationsystem.lecture.Lecture;

@AllArgsConstructor
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
	private final String schedule;
	private final Integer totalCapacity;

	public LectureDetail(Lecture lecture) {
		id = lecture.getId();
		lectureNumber = lecture.getLectureNumber();
		subjectDivision = lecture.fetchSubjectDivisionDescription();
		subjectName = lecture.fetchSubjectName();
		hoursPerWeek = lecture.fetchHoursPerWeek();
		credits = lecture.fetchCredits();
		targetGrade = lecture.fetchTargetGradeNumber();
		departmentName = lecture.fetchDepartmentName();
		professorName = lecture.fetchProfessorName();
		schedule = lecture.generateSchedule();
		totalCapacity = lecture.getTotalCapacity();
	}

}
