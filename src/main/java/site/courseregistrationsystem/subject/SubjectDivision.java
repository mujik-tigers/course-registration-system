package site.courseregistrationsystem.subject;

import lombok.Getter;

@Getter
public enum SubjectDivision {

	MR("전필"),
	ME("전선"),
	GR("교필"),
	GE("교선");

	private final String description;

	SubjectDivision(String description) {
		this.description = description;
	}

}
