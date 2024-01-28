package site.courseregistrationsystem;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import site.courseregistrationsystem.clock.application.ClockService;
import site.courseregistrationsystem.registration.application.BasketRegistrationPeriodService;
import site.courseregistrationsystem.registration.application.EnrollmentRegistrationPeriodService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

	@SpyBean
	protected ClockService clockService;

	@SpyBean
	protected BasketRegistrationPeriodService basketRegistrationPeriodService;

	@SpyBean
	protected EnrollmentRegistrationPeriodService enrollmentRegistrationPeriodService;

}
