package site.courseregistrationsystem.registration.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.student.Grade;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EnrollmentRegistrationPeriodSaveForm {

	private Grade grade;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

}
