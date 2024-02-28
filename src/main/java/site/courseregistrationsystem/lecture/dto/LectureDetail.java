package site.courseregistrationsystem.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.courseregistrationsystem.lecture.Lecture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LectureDetail {

	private Long id;
	private Integer lectureNumber;
	private String subjectDivision;
	private String subjectName;
	private Integer hoursPerWeek;
	private Integer credits;
	private int targetGrade;
	private String departmentName;
	private String professorName;
	private String schedule;
	private Integer totalCapacity;

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
