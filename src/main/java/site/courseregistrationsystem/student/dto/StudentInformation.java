package site.courseregistrationsystem.student.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class StudentInformation {

	private final String name;
	private final String studentId;
	private final String departmentName;
	private final int grade;

}
