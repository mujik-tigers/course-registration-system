package site.courseregistrationsystem.registration.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.student.Grade;

@NoArgsConstructor
@Getter
public class EnrollmentRegistrationPeriodSaveForm {

	private Grade grade;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Semester semester;

}
