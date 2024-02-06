package site.courseregistrationsystem.registration.infrastructure;

import org.springframework.data.repository.CrudRepository;

import site.courseregistrationsystem.registration.EnrollmentRegistrationPeriod;

public interface EnrollmentRegistrationPeriodStorage extends CrudRepository<EnrollmentRegistrationPeriod, String> {
}
