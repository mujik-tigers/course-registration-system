package site.courseregistrationsystem.registration.infrastructure;

import org.springframework.data.repository.CrudRepository;

import site.courseregistrationsystem.registration.BasketRegistrationPeriod;

public interface BasketRegistrationPeriodStorage extends CrudRepository<BasketRegistrationPeriod, String> {
}
