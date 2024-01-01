package site.courseregistrationsystem.lecture.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.subject.SubjectDivision;

@NoArgsConstructor
@Getter
public class LectureFilterOptions {

	private SubjectDivision subjectDivision;
	private Long departmentId;
	private String subjectName;

	@Builder
	private LectureFilterOptions(SubjectDivision subjectDivision, Long departmentId, String subjectName) {
		this.subjectDivision = subjectDivision;
		this.departmentId = departmentId;
		this.subjectName = subjectName;
	}

}
