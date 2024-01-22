package site.courseregistrationsystem.clock.infrastructure;

import org.springframework.data.repository.CrudRepository;

import site.courseregistrationsystem.clock.Clock;

public interface ClockStorage extends CrudRepository<Clock, String> {
}
