package site.courseregistrationsystem.enrollment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EnrollmentCapacity {

	private final int capacity;
	private final int currentEnrollmentCount;

}
