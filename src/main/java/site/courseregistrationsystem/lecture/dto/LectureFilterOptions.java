package site.courseregistrationsystem.lecture.dto;

import java.time.Year;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.subject.SubjectDivision;

@Getter
public class LectureFilterOptions {

	@NotNull
	private final Year openingYear;

	@NotNull
	private final Semester semester;

	private final SubjectDivision subjectDivision;
	private final Long departmentId;
	private final String subjectName;

	@Builder
	@Jacksonized
	private LectureFilterOptions(Year openingYear, Semester semester, SubjectDivision subjectDivision,
		Long departmentId,
		String subjectName) {
		this.openingYear = openingYear;
		this.semester = semester;
		this.subjectDivision = subjectDivision;
		this.departmentId = departmentId;
		this.subjectName = subjectName;
	}

	public boolean fetchNoOptionFirstPage() {
		return (subjectDivision == null) && (departmentId == null) && (subjectName == null);
	}

}
