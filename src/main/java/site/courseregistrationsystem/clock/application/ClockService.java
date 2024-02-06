package site.courseregistrationsystem.clock.application;

import static site.courseregistrationsystem.util.ProjectConstant.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.clock.Clock;
import site.courseregistrationsystem.clock.dto.CurrentYearAndSemester;
import site.courseregistrationsystem.clock.infrastructure.ClockStorage;
import site.courseregistrationsystem.exception.clock.ClockNotFoundException;

@Service
@RequiredArgsConstructor
public class ClockService {

	private final ClockStorage clockStorage;

	public CurrentYearAndSemester fetchCurrentClock() {
		Clock currentClock = clockStorage.findById(CLOCK_ID)
			.orElseThrow(ClockNotFoundException::new);

		return new CurrentYearAndSemester(currentClock);
	}

}
