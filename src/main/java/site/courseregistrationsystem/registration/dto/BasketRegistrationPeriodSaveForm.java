package site.courseregistrationsystem.registration.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.lecture.Semester;

@NoArgsConstructor
@Getter
public class BasketRegistrationPeriodSaveForm {

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Semester semester;

}
