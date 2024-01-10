package site.courseregistrationsystem.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.courseregistrationsystem.enrollment.Enrollment;
import site.courseregistrationsystem.lecture.Lecture;

@AllArgsConstructor
@Getter
public class EnrolledLectureDetail {

	private final Long id;
	private final Long lectureId;
	private final Integer lectureNumber;
	private final String subjectDivision;
	private final String subjectName;
	private final Integer hoursPerWeek;
	private final Integer credits;
	private final int targetGrade;
	private final String professorName;
	private final String schedule;

	public EnrolledLectureDetail(Enrollment enrollment) {
		id = enrollment.getId();

		Lecture lecture = enrollment.getLecture();
		lectureId = lecture.getId();
		lectureNumber = lecture.getLectureNumber();
		subjectDivision = lecture.fetchSubjectDivisionDescription();
		subjectName = lecture.fetchSubjectName();
		hoursPerWeek = lecture.fetchHoursPerWeek();
		credits = lecture.fetchCredits();
		targetGrade = lecture.fetchTargetGradeNumber();
		professorName = lecture.fetchProfessorName();
		schedule = lecture.generateSchedule();
	}

}
