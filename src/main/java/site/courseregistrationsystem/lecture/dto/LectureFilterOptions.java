package site.courseregistrationsystem.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.courseregistrationsystem.subject.SubjectDivision;

@AllArgsConstructor
@Getter
public class LectureFilterOptions {

	private SubjectDivision subjectDivision;
	private Long departmentId;
	private String subjectName;

}
