package site.courseregistrationsystem.registration.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.Getter;
import site.courseregistrationsystem.registration.EnrollmentRegistrationPeriod;

@Getter
public class EnrollmentRegistrationPeriods {

	private final List<EnrollmentRegistrationPeriod> enrollmentRegistrationPeriods;

	public EnrollmentRegistrationPeriods(Iterable<EnrollmentRegistrationPeriod> registrationPeriods) {
		this.enrollmentRegistrationPeriods = StreamSupport.stream(registrationPeriods.spliterator(), false)
			.collect(Collectors.toList());
	}

}
