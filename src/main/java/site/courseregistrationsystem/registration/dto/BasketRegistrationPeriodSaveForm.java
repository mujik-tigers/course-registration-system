package site.courseregistrationsystem.registration.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.lecture.Semester;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BasketRegistrationPeriodSaveForm {

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Semester semester;

}
