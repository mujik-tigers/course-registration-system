package site.courseregistrationsystem.schedule.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.schedule.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}
