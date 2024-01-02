package site.courseregistrationsystem.lecture.dto;

import java.time.Year;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.subject.SubjectDivision;

@NoArgsConstructor
@Getter
@Setter
public class LectureFilterOptions {

	@NotNull
	private Year openingYear;

	@NotNull
	private Semester semester;

	private SubjectDivision subjectDivision;
	private Long departmentId;
	private String subjectName;

	@Builder
	public LectureFilterOptions(Year openingYear, Semester semester, SubjectDivision subjectDivision, Long departmentId,
		String subjectName) {
		this.openingYear = openingYear;
		this.semester = semester;
		this.subjectDivision = subjectDivision;
		this.departmentId = departmentId;
		this.subjectName = subjectName;
	}

}
